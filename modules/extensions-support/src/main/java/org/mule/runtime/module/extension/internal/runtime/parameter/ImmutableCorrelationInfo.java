/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.runtime.parameter;

import org.mule.runtime.extension.api.runtime.parameter.CorrelationInfo;

import java.util.Optional;

public class ImmutableCorrelationInfo implements CorrelationInfo {

  private final String eventId;
  private final boolean outboundCorrelationEnabled;
  private final Optional<String> correlationId;

  public ImmutableCorrelationInfo(String eventId, boolean outboundCorrelationEnabled,
                                  Optional<String> correlationId) {
    this.eventId = eventId;
    this.outboundCorrelationEnabled = outboundCorrelationEnabled;
    this.correlationId = correlationId;
  }

  @Override
  public String getEventId() {
    return eventId;
  }

  @Override
  public boolean isOutboundCorrelationEnabled() {
    return outboundCorrelationEnabled;
  }

  @Override
  public Optional<String> getCorrelationId() {
    return correlationId;
  }
}
