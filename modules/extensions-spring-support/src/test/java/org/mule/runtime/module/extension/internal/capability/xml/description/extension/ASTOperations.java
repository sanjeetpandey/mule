/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.capability.xml.description.extension;

import org.mule.runtime.extension.api.annotation.metadata.MetadataKeyId;
import org.mule.runtime.extension.api.annotation.metadata.MetadataScope;
import org.mule.runtime.extension.api.annotation.param.Optional;
import org.mule.runtime.extension.api.annotation.param.display.DisplayName;

@MetadataScope(keysResolver = ASTKeyResolver.class)
public class ASTOperations {


  public String simpleOperation() {
    return null;
  }

  public void voidOperation() {

  }

  public String operationWithParamters(String param1, @DisplayName("display-name") String withDisplayName,
                                       @Optional String optionalParam,
                                       @MetadataKeyId(ASTKeyResolver.class) String metadataKeyId) {
    return null;
  }
}
