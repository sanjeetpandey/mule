/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.enricher.fixed;

import static java.util.Arrays.asList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.mule.metadata.xml.api.SchemaCollector.getInstance;
import static org.mule.runtime.extension.api.loader.DeclarationEnricherPhase.INITIALIZE;
import static org.mule.runtime.extension.api.util.ExtensionMetadataTypeUtils.getType;
import static org.mule.runtime.module.extension.internal.loader.enricher.fixed.MetadataTypeProxy.doProxy;

import org.mule.metadata.api.annotation.TypeAnnotation;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.java.api.annotation.ClassInformationAnnotation;
import org.mule.metadata.json.api.JsonTypeLoader;
import org.mule.metadata.xml.api.XmlTypeLoader;
import org.mule.runtime.api.meta.model.declaration.fluent.BaseDeclaration;
import org.mule.runtime.api.meta.model.declaration.fluent.OperationDeclaration;
import org.mule.runtime.api.meta.model.declaration.fluent.ParameterDeclaration;
import org.mule.runtime.api.meta.model.declaration.fluent.SourceDeclaration;
import org.mule.runtime.api.meta.model.declaration.fluent.TypedDeclaration;
import org.mule.runtime.api.meta.model.declaration.fluent.WithOperationsDeclaration;
import org.mule.runtime.api.meta.model.declaration.fluent.WithSourcesDeclaration;
import org.mule.runtime.api.metadata.resolving.StaticTypeResolver;
import org.mule.runtime.core.api.util.IOUtils;
import org.mule.runtime.extension.api.annotation.metadata.OutputResolver;
import org.mule.runtime.extension.api.annotation.metadata.TypeResolver;
import org.mule.runtime.extension.api.annotation.metadata.fixed.InputJsonType;
import org.mule.runtime.extension.api.annotation.metadata.fixed.InputXmlType;
import org.mule.runtime.extension.api.annotation.metadata.fixed.OutputJsonType;
import org.mule.runtime.extension.api.annotation.metadata.fixed.OutputXmlType;
import org.mule.runtime.extension.api.declaration.fluent.util.IdempotentDeclarationWalker;
import org.mule.runtime.extension.api.declaration.type.annotation.CustomDefinedStaticTypeAnnotation;
import org.mule.runtime.extension.api.loader.DeclarationEnricher;
import org.mule.runtime.extension.api.loader.DeclarationEnricherPhase;
import org.mule.runtime.extension.api.loader.ExtensionLoadingContext;
import org.mule.runtime.module.extension.internal.loader.java.property.ImplementingMethodModelProperty;
import org.mule.runtime.module.extension.internal.loader.java.property.ImplementingParameterModelProperty;
import org.mule.runtime.module.extension.internal.loader.java.property.ImplementingTypeModelProperty;

import java.io.InputStream;
import java.lang.reflect.AnnotatedElement;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * @since 4.0
 */
public final class CustomStaticTypeDeclarationEnricher implements DeclarationEnricher {

  @Override
  public DeclarationEnricherPhase getExecutionPhase() {
    return INITIALIZE;
  }

  @Override
  public void enrich(ExtensionLoadingContext extensionLoadingContext) {
    new IdempotentDeclarationWalker() {

      @Override
      protected void onOperation(WithOperationsDeclaration owner, OperationDeclaration operation) {
        operation.getModelProperty(ImplementingMethodModelProperty.class)
          .map(ImplementingMethodModelProperty::getMethod)
          .ifPresent(method -> getOutputType(method).ifPresent(type -> declareCustomType(operation.getOutput(), type)));

        for (ParameterDeclaration param : operation.getAllParameters()) {
          param.getModelProperty(ImplementingParameterModelProperty.class)
            .map(ImplementingParameterModelProperty::getParameter)
            .ifPresent(annotated -> getInputType(annotated).ifPresent(type -> declareCustomType(param, type)));
        }
      }

      @Override
      protected void onSource(WithSourcesDeclaration owner, SourceDeclaration source) {
        source.getModelProperty(ImplementingTypeModelProperty.class)
          .map(ImplementingTypeModelProperty::getType)
          .ifPresent(clazz -> getOutputType(clazz).ifPresent(type -> declareCustomType(source.getOutput(), type)));
      }
    }.walk(extensionLoadingContext.getExtensionDeclarer().getDeclaration());
  }

  private <T extends BaseDeclaration & TypedDeclaration> void declareCustomType(T declaration, MetadataType overrideType) {
    MetadataType type = declaration.getType();
    Class<?> clazz = getType(type).orElseThrow(() -> new IllegalStateException("Could not find class in type [" + type + "]"));
    Set<TypeAnnotation> annotations =
      new HashSet<>(asList(new ClassInformationAnnotation(clazz), new CustomDefinedStaticTypeAnnotation()));
    declaration.setType(doProxy(overrideType, annotations), false);
  }

  private Optional<MetadataType> getOutputType(AnnotatedElement element) {
    OutputXmlType xml = element.getAnnotation(OutputXmlType.class);
    if (xml != null) {
      return of(parseXmlType(xml.schema(), xml.qName()));
    }
    OutputJsonType json = element.getAnnotation(OutputJsonType.class);
    if (json != null) {
      return of(parseJsonType(json.schema()));
    }
    OutputResolver resolver = element.getAnnotation(OutputResolver.class);
    if (resolver != null && isStaticResolver(resolver.output())) {
      return ofNullable(parseCustomStaticType((Class<? extends StaticTypeResolver>) resolver.output()));
    }
    return empty();
  }

  private Optional<MetadataType> getInputType(AnnotatedElement element) {
    InputXmlType xml = element.getAnnotation(InputXmlType.class);
    if (xml != null) {
      return of(parseXmlType(xml.schema(), xml.qName()));
    }
    InputJsonType json = element.getAnnotation(InputJsonType.class);
    if (json != null) {
      return of(parseJsonType(json.schema()));
    }
    TypeResolver resolver = element.getAnnotation(TypeResolver.class);
    if (resolver != null && isStaticResolver(resolver.value())) {
      return ofNullable(parseCustomStaticType((Class<? extends StaticTypeResolver>) resolver.value()));
    }
    return empty();
  }

  private boolean isStaticResolver(Class<?> resolverClazz) {
    return StaticTypeResolver.class.isAssignableFrom(resolverClazz);
  }

  private MetadataType parseCustomStaticType(Class<? extends StaticTypeResolver> resolver) {
    try {
      return resolver.newInstance().getStaticType();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalArgumentException("Can't obtain static type for element", e);
    }
  }

  private MetadataType parseJsonType(String schema) {
    return getTypeFromSchema(schema, content -> new JsonTypeLoader(IOUtils.toString(content)).load(null)
             .orElseThrow(() -> new IllegalArgumentException("Could not load type from Json schema [" + schema + "]")));

  }

  private MetadataType parseXmlType(String schema, String name) {
    return getTypeFromSchema(schema, content -> new XmlTypeLoader(getInstance().addSchema(schema, content)).load(name)
             .orElseThrow(() -> new IllegalArgumentException("Type [" + name + "] wasn't found in XML schema [" + schema + "]")));
  }

  private MetadataType getTypeFromSchema(String schemaName, Function<InputStream, MetadataType> loader) {
    InputStream schema = Thread.currentThread().getContextClassLoader().getResourceAsStream(schemaName);
    if (schema == null) {
      throw new IllegalArgumentException("Can't load schema [" + schemaName + "]. It was not found in the resources.");
    }
    return loader.apply(schema);
  }
}
