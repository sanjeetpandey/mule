/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import static java.util.stream.Collectors.toList;

import org.mule.runtime.extension.api.annotation.ExpressionFunctions;
import org.mule.runtime.extension.api.annotation.Operations;
import org.mule.runtime.extension.api.annotation.Sources;
import org.mule.runtime.extension.api.annotation.connectivity.ConnectionProviders;
import org.mule.runtime.module.extension.internal.loader.java.type.ConfigurationElement;
import org.mule.runtime.module.extension.internal.loader.java.type.ConnectionProviderElement;
import org.mule.runtime.module.extension.internal.loader.java.type.FunctionContainerElement;
import org.mule.runtime.module.extension.internal.loader.java.type.OperationContainerElement;
import org.mule.runtime.module.extension.internal.loader.java.type.SourceElement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;

import java.util.List;

public class ConfigurationASTElement extends ASTType implements ConfigurationElement {

  public ConfigurationASTElement(TypeElement typeElement, ProcessingEnvironment processingEnvironment) {
    super(typeElement, processingEnvironment);
  }

  @Override
  public List<ConnectionProviderElement> getConnectionProviders() {
    return astUtils.fromAnnotation(ConnectionProviders.class, typeElement)
        .getClassArrayValue(ConnectionProviders::value)
        .stream().map(connElem -> new ConnectionProviderASTElement(connElem.getTypeElement(),
                                                                   processingEnvironment))
        .collect(toList());
  }

  @Override
  public List<FunctionContainerElement> getFunctionContainers() {
    return astUtils.fromAnnotation(ExpressionFunctions.class, typeElement)
        .getClassArrayValue(ExpressionFunctions::value)
        .stream().map(sourceElem -> new FunctionContainerASTElement(sourceElem.getTypeElement(), processingEnvironment))
        .collect(toList());
  }

  @Override
  public List<SourceElement> getSources() {
    return astUtils.fromAnnotation(Sources.class, typeElement)
        .getClassArrayValue(Sources::value)
        .stream().map(sourceElem -> new SourceASTElement(sourceElem.getTypeElement(), processingEnvironment))
        .collect(toList());
  }

  @Override
  public List<OperationContainerElement> getOperationContainers() {
    return astUtils.fromAnnotation(Operations.class, typeElement)
        .getClassArrayValue(Operations::value)
        .stream().map(operationElem -> new OperationContainerASTElement(operationElem.getTypeElement(), processingEnvironment))
        .collect(toList());
  }
}
