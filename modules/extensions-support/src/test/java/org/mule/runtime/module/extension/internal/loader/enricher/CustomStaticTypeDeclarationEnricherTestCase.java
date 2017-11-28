/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.enricher;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mule.metadata.api.model.MetadataFormat.CSV;
import static org.mule.metadata.api.model.MetadataFormat.JSON;
import static org.mule.metadata.api.model.MetadataFormat.XML;
import static org.mule.runtime.module.extension.api.util.MuleExtensionUtils.loadExtension;

import org.mule.metadata.api.model.MetadataType;
import org.mule.runtime.api.meta.model.ExtensionModel;
import org.mule.runtime.api.meta.model.operation.OperationModel;
import org.mule.tck.junit4.AbstractMuleTestCase;
import org.mule.test.metadata.extension.MetadataExtension;

import org.junit.Test;

public class CustomStaticTypeDeclarationEnricherTestCase extends AbstractMuleTestCase {

  ExtensionModel extension = loadExtension(MetadataExtension.class);

  @Test
  public void withInputXmlStaticType() throws Exception {
    OperationModel o = getOperation("xmlInput");
    MetadataType type = o.getAllParameterModels().get(0).getType();
    assertThat(type.getMetadataFormat(), is(XML));
    assertThat(type.toString(), is("shiporder"));
  }

  @Test
  public void withOutputXmlStaticType() throws Exception {
    OperationModel o = getOperation("xmlOutput");
    MetadataType type = o.getOutput().getType();
    assertThat(type.getMetadataFormat(), is(XML));
    assertThat(type.toString(), is("shiporder"));
  }

  @Test
  public void customTypeOutput() throws Exception {
    OperationModel o = getOperation("customTypeOutput");
    MetadataType type = o.getOutput().getType();
    assertThat(type.getMetadataFormat(), is(CSV));
    assertThat(type.toString(), is("csv-object"));
  }

  @Test
  public void customTypeInput() throws Exception {
    OperationModel o = getOperation("customTypeInput");
    MetadataType type = o.getAllParameterModels().get(0).getType();
    assertThat(type.getMetadataFormat(), is(JSON));
    assertThat(type.toString(), is("json-object"));
  }

  @Test
  public void customTypeInputAndOutput() throws Exception {
    OperationModel o = getOperation("customInputAndOutput");
    MetadataType type = o.getAllParameterModels().get(0).getType();
    assertThat(type.getMetadataFormat(), is(JSON));
    assertThat(type.toString(), is("json-object"));
  }

  private OperationModel getOperation(String ope) {
    return extension.getOperationModel(ope).orElseThrow(() -> new RuntimeException(ope + " not found"));
  }
}
