/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import org.mule.runtime.api.util.LazyValue;
import org.mule.runtime.extension.api.annotation.param.ParameterGroup;
import org.mule.runtime.module.extension.internal.loader.java.type.ExtensionParameter;
import org.mule.runtime.module.extension.internal.loader.java.type.MethodElement;
import org.mule.runtime.module.extension.internal.loader.java.type.OperationContainerElement;
import org.mule.runtime.module.extension.internal.loader.java.type.Type;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class OperationASTElement implements MethodElement {

  private final ExecutableElement method;
  private final ProcessingEnvironment processingEnvironment;
  private final LazyValue<List<ExtensionParameter>> parameters;
  private final LazyValue<List<ExtensionParameter>> parameterGroups;
  private final ASTUtils astUtils;

  public OperationASTElement(ExecutableElement method, ProcessingEnvironment processingEnvironment) {
    this.method = method;
    this.processingEnvironment = processingEnvironment;
    this.astUtils = new ASTUtils(processingEnvironment);

    parameters = new LazyValue<>(() -> method.getParameters().stream()
        .map(param -> new MethodASTMParameterElement(param, processingEnvironment))
        .collect(toList()));
    parameterGroups = new LazyValue<>(() -> getParametersAnnotatedWith(ParameterGroup.class));
  }

  @Override
  public Optional<Method> getMethod() {
    return empty();
  }

  @Override
  public OperationContainerElement getEnclosingType() {
    return new OperationContainerASTElement((TypeElement) method.getEnclosingElement(), processingEnvironment);
  }

  @Override
  public List<ExtensionParameter> getParameters() {
    return parameters.get();
  }

  @Override
  public List<ExtensionParameter> getParameterGroups() {
    return parameterGroups.get();
  }

  @Override
  public List<ExtensionParameter> getParametersAnnotatedWith(Class<? extends Annotation> annotationClass) {
    return getParameters().stream()
        .filter(param -> param.getAnnotation(annotationClass).isPresent())
        .collect(toList());
  }

  @Override
  public Class<?> getReturnType() {
    try {
      String className = this.getReturnTypeElement().getTypeElement().toString();
      if (className.equals("void")) {
        return Void.class;
      }
      return Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public Type getReturnTypeElement() {
    return new ASTType(method.getReturnType(), processingEnvironment);

    //    if (name.equals("void")) {
    //      return new ASTType(processingEnvironment.getElementUtils().getTypeElement(Void.class.getName()), processingEnvironment);
    //    } else {
    //      return new ASTType(processingEnvironment.getElementUtils().getTypeElement(name), processingEnvironment);
    //    }
  }

  @Override
  public String getName() {
    return method.getSimpleName().toString();
  }

  @Override
  public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass) {
    return ofNullable(method.getAnnotation(annotationClass));
  }

  @Override
  public <A extends Annotation> ASTUtils.ValueFetcher<A> getAnnotation2(Class<A> annotationClass) {
    return astUtils.fromAnnotation(annotationClass, method);
  }

  @Override
  public Class getDeclaringClass() {
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;

    if (o == null || getClass() != o.getClass())
      return false;

    OperationASTElement that = (OperationASTElement) o;

    return new EqualsBuilder()
        .append(method, that.method)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(method)
        .toHashCode();
  }



}
