/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import org.mule.runtime.module.extension.internal.loader.java.type.ConnectionProviderElement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConnectionProviderASTElement extends ASTType implements ConnectionProviderElement {

  public ConnectionProviderASTElement(TypeElement typeElement, ProcessingEnvironment processingEnvironment) {
    super(typeElement, processingEnvironment);
  }

  @Override
  public List<Type> getSuperClassGenerics() {
    return null;
  }

  @Override
  public List<Class<?>> getInterfaceGenerics(Class clazz) {
    List<? extends TypeMirror> interfaces = typeElement.getInterfaces();
    TypeElement typeElement = processingEnvironment.getElementUtils().getTypeElement(clazz.getName());
    typeElement.asType();
    List<Class<?>> generics = new ArrayList<>();

    for (TypeMirror anInterface : interfaces) {
      DeclaredType theInterface = (DeclaredType) anInterface;
      typeElement.equals(theInterface.asElement());
      List<? extends TypeMirror> typeArguments = theInterface.getTypeArguments();

      for (TypeMirror typeArgument : typeArguments) {
        try {
          generics.add(Class.forName(typeArgument.toString()));
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }
    }

    return generics;
  }
}
