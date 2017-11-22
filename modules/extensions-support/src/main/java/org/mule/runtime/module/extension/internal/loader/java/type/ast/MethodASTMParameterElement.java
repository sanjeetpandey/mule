/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static org.mule.runtime.module.extension.internal.loader.java.type.ast.ASTUtils.getReflectType;

import org.mule.runtime.module.extension.internal.loader.java.type.ExtensionParameter;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.VariableElement;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Optional;

public class MethodASTMParameterElement implements ExtensionParameter {

  private final VariableElement param;
  private final ProcessingEnvironment processingEnvironment;

  public MethodASTMParameterElement(VariableElement param, ProcessingEnvironment processingEnvironment) {
    this.param = param;
    this.processingEnvironment = processingEnvironment;
  }

  @Override
  public Optional<AnnotatedElement> getDeclaringElement() {
    return empty();
  }

  @Override
  public Type getJavaType() {
    return getReflectType(getType());
  }

  @Override
  public String getName() {
    return param.getSimpleName().toString();
  }

  @Override
  public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass) {
    return ofNullable(param.getAnnotation(annotationClass));
  }

  @Override
  public String getOwnerDescription() {
    return "Method";
  }

  @Override
  public org.mule.runtime.module.extension.internal.loader.java.type.Type getType() {
    return new ASTType(param.asType(), processingEnvironment);
  }
}
