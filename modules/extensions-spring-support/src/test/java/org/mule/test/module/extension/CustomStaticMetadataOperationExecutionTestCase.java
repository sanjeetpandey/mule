/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.module.extension;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertThat;
import static org.mule.functional.junit4.matchers.ThrowableRootCauseMatcher.hasRootCause;
import static org.mule.runtime.api.message.Message.of;
import static org.mule.runtime.api.metadata.DataType.MULE_MESSAGE_COLLECTION;
import static org.mule.runtime.api.metadata.MediaType.APPLICATION_JAVA;
import static org.mule.runtime.core.api.event.EventContextFactory.create;
import static org.mule.tck.MuleTestUtils.getTestFlow;
import static org.mule.tck.junit4.matcher.DataTypeCompatibilityMatcher.assignableTo;
import static org.mule.test.heisenberg.extension.HeisenbergConnectionProvider.SAUL_OFFICE_NUMBER;
import static org.mule.test.heisenberg.extension.HeisenbergOperations.CALL_GUS_MESSAGE;
import static org.mule.test.heisenberg.extension.HeisenbergOperations.CURE_CANCER_MESSAGE;
import static org.mule.test.heisenberg.extension.exception.HeisenbergConnectionExceptionEnricher.ENRICHED_MESSAGE;
import static org.mule.test.heisenberg.extension.model.HealthStatus.CANCER;
import static org.mule.test.heisenberg.extension.model.HealthStatus.DEAD;
import static org.mule.test.heisenberg.extension.model.HealthStatus.HEALTHY;
import static org.mule.test.heisenberg.extension.model.KnockeableDoor.knock;
import static org.mule.test.heisenberg.extension.model.Ricin.RICIN_KILL_MESSAGE;

import org.mule.functional.api.flow.FlowRunner;
import org.mule.runtime.api.connection.ConnectionException;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.message.Message;
import org.mule.runtime.api.metadata.TypedValue;
import org.mule.runtime.core.api.event.CoreEvent;
import org.mule.runtime.core.api.extension.ExtensionManager;
import org.mule.runtime.extension.api.runtime.parameter.ParameterResolver;
import org.mule.tck.testmodels.fruit.Apple;
import org.mule.test.heisenberg.extension.HeisenbergExtension;
import org.mule.test.heisenberg.extension.exception.HealthException;
import org.mule.test.heisenberg.extension.exception.HeisenbergException;
import org.mule.test.heisenberg.extension.model.BarberPreferences;
import org.mule.test.heisenberg.extension.model.CarDealer;
import org.mule.test.heisenberg.extension.model.CarWash;
import org.mule.test.heisenberg.extension.model.HealthStatus;
import org.mule.test.heisenberg.extension.model.Investment;
import org.mule.test.heisenberg.extension.model.KnockeableDoor;
import org.mule.test.heisenberg.extension.model.PersonalInfo;
import org.mule.test.heisenberg.extension.model.Ricin;
import org.mule.test.heisenberg.extension.model.SaleInfo;
import org.mule.test.heisenberg.extension.model.Weapon;
import org.mule.test.heisenberg.extension.model.types.IntegerAttributes;
import org.mule.test.heisenberg.extension.model.types.WeaponType;
import org.mule.test.module.extension.internal.util.ExtensionsTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hamcrest.Matchers;
import org.hamcrest.collection.IsMapContaining;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class CustomStaticMetadataOperationExecutionTestCase extends AbstractExtensionFunctionalTestCase {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  @Override
  protected String[] getConfigFiles() {
    return new String[] {"metadata-static.xml"};
  }

  @Test
  public void xmlOutput() throws Exception {
    Object payload = flowRunner("output").keepStreamsOpen().run().getMessage().getPayload().getValue();
    assertThat(payload, is(notNullValue()));
  }

  @Test
  public void xmlInput() throws Exception {
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("metadata-static.xml");
    Object payload = flowRunner("input").withPayload(is).run().getMessage().getPayload().getValue();
    assertThat(payload, is(notNullValue()));
  }

  @Test
  public void customOutput() throws Exception {
    Object payload = flowRunner("custom-output").run().getMessage().getPayload().getValue();
    assertThat(payload, is(notNullValue()));
  }

  @Test
  public void customInput() throws Exception {
    Object payload = flowRunner("custom-input").withPayload("{\"book\": \"aBook\"}").run().getMessage().getPayload().getValue();
    assertThat(payload, is(notNullValue()));
  }
}
