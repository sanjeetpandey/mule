/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import static java.util.stream.Collectors.toList;
import static javax.lang.model.element.ElementKind.METHOD;

import org.mule.runtime.extension.api.annotation.Ignore;
import org.mule.runtime.module.extension.internal.loader.java.type.MethodElement;
import org.mule.runtime.module.extension.internal.loader.java.type.OperationContainerElement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import java.util.List;

import com.sun.tools.javac.code.Symbol;

public class OperationContainerASTElement extends ASTType implements OperationContainerElement {

  public OperationContainerASTElement(TypeElement typeElement, ProcessingEnvironment processingEnvironment) {
    super(typeElement, processingEnvironment);
  }

  @Override
  public List<MethodElement> getOperations() {
    return typeElement.getEnclosedElements()
        .stream()
        .filter(elem -> elem.getKind().equals(METHOD))
        .filter(elem -> elem.getAnnotation(Ignore.class) == null)
        .map(elem -> new OperationASTElement((Symbol.MethodSymbol) elem, processingEnvironment))
        .collect(toList());
  }
}
