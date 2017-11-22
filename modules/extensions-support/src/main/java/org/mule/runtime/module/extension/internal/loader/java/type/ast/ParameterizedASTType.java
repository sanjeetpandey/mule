/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import org.mule.runtime.module.extension.internal.loader.java.type.ExtensionParameter;
import org.mule.runtime.module.extension.internal.loader.java.type.WithParameters;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import java.lang.annotation.Annotation;
import java.util.List;

public class ParameterizedASTType extends ASTType implements WithParameters {

  public ParameterizedASTType(TypeElement typeElement, ProcessingEnvironment processingEnvironment) {
    super(typeElement, processingEnvironment);
  }

  @Override
  public List<ExtensionParameter> getParameters() {
    return null;
  }

  @Override
  public List<ExtensionParameter> getParameterGroups() {
    return null;
  }

  @Override
  public List<ExtensionParameter> getParametersAnnotatedWith(Class<? extends Annotation> annotationClass) {
    return null;
  }
}
