/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import static java.util.Optional.empty;

import org.mule.runtime.module.extension.internal.loader.java.type.MethodElement;
import org.mule.runtime.module.extension.internal.loader.java.type.SourceElement;
import org.mule.runtime.module.extension.internal.loader.java.type.Type;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SourceASTElement extends ASTType implements SourceElement {

  public SourceASTElement(TypeElement typeElement, ProcessingEnvironment processingEnvironment) {
    super(typeElement, processingEnvironment);
  }

  @Override
  public List<Type> getSuperClassGenerics() {
    TypeMirror superclass = this.getTypeElement().getSuperclass();
    MuleTypeVisitor.TypeIntrospectionResult accept = superclass
        .accept(new MuleTypeVisitor(processingEnvironment), MuleTypeVisitor.TypeIntrospectionResultBuilder.newBuilder());
    return accept.getGenerics().stream().map(type -> new ASTType(typeElement, processingEnvironment))
        .collect(Collectors.toList());
  }

  @Override
  public Optional<MethodElement> getOnResponseMethod() {
    return empty();
  }

  @Override
  public Optional<MethodElement> getOnErrorMethod() {
    return empty();
  }

  @Override
  public Optional<MethodElement> getOnTerminateMethod() {
    return empty();
  }
}
