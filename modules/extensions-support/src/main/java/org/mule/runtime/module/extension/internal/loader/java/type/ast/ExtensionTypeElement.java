/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import static java.util.stream.Collectors.toList;

import org.mule.runtime.api.meta.Category;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.ExpressionFunctions;
import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.module.extension.internal.loader.java.type.ConfigurationElement;
import org.mule.runtime.module.extension.internal.loader.java.type.ExtensionElement;
import org.mule.runtime.module.extension.internal.loader.java.type.MethodElement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import java.util.Collection;
import java.util.List;

public class ExtensionTypeElement extends ConfigurationASTElement implements ExtensionElement {

  private TypeElement typeElement;
  private final ProcessingEnvironment processingEnv;
  private final RoundEnvironment env;

  public ExtensionTypeElement(TypeElement typeElement, ProcessingEnvironment processingEnv, RoundEnvironment env) {
    super(typeElement, processingEnv);
    this.typeElement = typeElement;
    this.processingEnv = processingEnv;
    this.env = env;
  }

  @Override
  public List<ConfigurationElement> getConfigurations() {
    return astUtils.fromAnnotation(Configurations.class, typeElement)
        .getClassArrayValue(Configurations::value)
        .stream().map(configType -> new ConfigurationASTElement(configType.getTypeElement(),
                                                                processingEnv))
        .collect(toList());
  }

  @Override
  public List<MethodElement> getFunctions() {
    return astUtils.fromAnnotation(ExpressionFunctions.class, typeElement)
        .getClassArrayValue(ExpressionFunctions::value)
        .stream().map(conWrapper -> new FunctionContainerASTElement(conWrapper.getTypeElement(), processingEnv))
        .map(FunctionContainerASTElement::getFunctions)
        .flatMap(Collection::stream)
        .collect(toList());
  }

  @Override
  public List<MethodElement> getOperations() {
    return astUtils.fromAnnotation(Operations.class, typeElement)
        .getClassArrayValue(Operations::value)
        .stream().map(conWrapper -> new OperationContainerASTElement(conWrapper.getTypeElement(), processingEnv))
        .map(OperationContainerASTElement::getOperations)
        .flatMap(Collection::stream)
        .collect(toList());
  }

  @Override
  public Category getCategory() {
    VariableElement enumValue = getAnnotation2(Extension.class).getEnumValue(Extension::category);
    return Category.valueOf(enumValue.toString());
  }

  @Override
  public String getVendor() {
    return getAnnotation2(Extension.class).getStringValue(Extension::vendor);
  }

  @Override
  public String getName() {
    return getAnnotation2(Extension.class).getStringValue(Extension::name);
  }
}
