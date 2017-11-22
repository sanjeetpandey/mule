/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import org.mule.runtime.module.extension.internal.loader.java.type.FieldElement;
import org.mule.runtime.module.extension.internal.loader.java.type.Type;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ASTType implements Type {

  final ASTUtils astUtils;
  final TypeElement typeElement;
  final ProcessingEnvironment processingEnvironment;
  private final List<GenericInfo> genericInfos;

  public ASTType(TypeMirror typeMirror, ProcessingEnvironment processingEnvironment) {
    this.astUtils = new ASTUtils(processingEnvironment);
    this.processingEnvironment = processingEnvironment;
    MuleTypeVisitor.TypeIntrospectionResult accept = typeMirror
        .accept(new MuleTypeVisitor(processingEnvironment), MuleTypeVisitor.TypeIntrospectionResultBuilder.newBuilder());
    this.typeElement = accept.getConcreteType();
    this.genericInfos = accept.getGenerics().stream()
        .map(te -> new GenericInfo(new ASTType(te, processingEnvironment), emptyList())).collect(toList());
  }

  public ASTType(TypeElement typeElement, ProcessingEnvironment processingEnvironment) {
    this.astUtils = new ASTUtils(processingEnvironment);
    this.processingEnvironment = processingEnvironment;
    this.typeElement = typeElement;
    this.genericInfos = emptyList();
  }

  @Override
  public Class getDeclaringClass() {
    try {
      return Class.forName(typeElement.toString());
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String getName() {
    return typeElement.getSimpleName().toString();
  }

  @Override
  public List<FieldElement> getFields() {
    List<? extends Element> enclosedElements = typeElement.getEnclosedElements();
    return enclosedElements
        .stream()
        .filter(elem -> elem.getKind() == ElementKind.FIELD)
        .map(elem1 -> new FieldTypeElement((VariableElement) elem1, processingEnvironment))
        .collect(toList());
  }

  @Override
  public List<FieldElement> getAnnotatedFields(Class<? extends Annotation>... annotations) {
    return getFields().stream()
        .filter(elem -> Stream.of(annotations)
            .anyMatch(annotation -> elem.getAnnotation(annotation).isPresent()))
        .collect(Collectors.toList());
  }

  @Override
  public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass) {
    return ofNullable(typeElement.getAnnotation(annotationClass));
  }

  @Override
  public <A extends Annotation> ASTUtils.ValueFetcher<A> getAnnotation2(Class<A> annotationClass) {
    return astUtils.fromAnnotation(annotationClass, typeElement);
  }

  @Override
  public TypeElement getTypeElement() {
    return typeElement;
  }

  @Override
  public List<GenericInfo> getGenerics() {
    return genericInfos;
  }

  @Override
  public java.lang.reflect.Type getReflectType() {
    return ASTUtils.getReflectType(this);
  }

  @Override
  public boolean isAssignableTo(Class<?> clazz) {
    return processingEnvironment.getTypeUtils()
        .isAssignable(typeElement.asType(), processingEnvironment.getElementUtils().getTypeElement(clazz.getName()).asType());
  }

  @Override
  public boolean isAssignableFrom(Class<?> clazz) {
    return processingEnvironment.getTypeUtils()
        .isAssignable(processingEnvironment.getElementUtils().getTypeElement(clazz.getName()).asType(), typeElement.asType());
  }

  @Override
  public String getTypeName() {
    return typeElement.toString();
  }
}
