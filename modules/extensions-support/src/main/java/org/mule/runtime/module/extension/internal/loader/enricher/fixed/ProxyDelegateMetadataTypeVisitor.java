/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.module.extension.internal.loader.enricher.fixed;

import org.mule.metadata.api.model.AnyType;
import org.mule.metadata.api.model.ArrayType;
import org.mule.metadata.api.model.AttributeFieldType;
import org.mule.metadata.api.model.AttributeKeyType;
import org.mule.metadata.api.model.BinaryType;
import org.mule.metadata.api.model.BooleanType;
import org.mule.metadata.api.model.DateTimeType;
import org.mule.metadata.api.model.DateType;
import org.mule.metadata.api.model.FunctionType;
import org.mule.metadata.api.model.IntersectionType;
import org.mule.metadata.api.model.LocalDateTimeType;
import org.mule.metadata.api.model.LocalTimeType;
import org.mule.metadata.api.model.MetadataType;
import org.mule.metadata.api.model.NothingType;
import org.mule.metadata.api.model.NullType;
import org.mule.metadata.api.model.NumberType;
import org.mule.metadata.api.model.ObjectFieldType;
import org.mule.metadata.api.model.ObjectKeyType;
import org.mule.metadata.api.model.ObjectType;
import org.mule.metadata.api.model.PeriodType;
import org.mule.metadata.api.model.RegexType;
import org.mule.metadata.api.model.SimpleType;
import org.mule.metadata.api.model.StringType;
import org.mule.metadata.api.model.TimeType;
import org.mule.metadata.api.model.TimeZoneType;
import org.mule.metadata.api.model.TupleType;
import org.mule.metadata.api.model.TypeParameterType;
import org.mule.metadata.api.model.UnionType;
import org.mule.metadata.api.model.VoidType;
import org.mule.metadata.api.visitor.MetadataTypeVisitor;

class ProxyDelegateMetadataTypeVisitor extends MetadataTypeVisitor {

  private final MetadataTypeVisitor delegate;
  private final MetadataType proxy;

  ProxyDelegateMetadataTypeVisitor(MetadataTypeVisitor delegate, MetadataType proxy) {
    this.delegate = delegate;
    this.proxy = proxy;
  }

  @Override
  public void visitSimpleType(SimpleType simpleType) {
    delegate.visitSimpleType(((SimpleType) proxy));
  }

  @Override
  public void visitAnyType(AnyType anyType) {
    delegate.visitAnyType(((AnyType) proxy));
  }

  @Override
  public void visitArrayType(ArrayType arrayType) {
    delegate.visitArrayType(((ArrayType) proxy));
  }

  @Override
  public void visitBinaryType(BinaryType binaryType) {
    delegate.visitBinaryType((BinaryType) proxy);
  }

  @Override
  public void visitBoolean(BooleanType booleanType) {
    delegate.visitBoolean(((BooleanType) proxy));
  }

  @Override
  public void visitDateTime(DateTimeType dateTimeType) {
    delegate.visitDateTime(((DateTimeType) proxy));
  }

  @Override
  public void visitDate(DateType dateType) {
    delegate.visitDate(((DateType) proxy));
  }

  @Override
  public void visitIntersection(IntersectionType intersectionType) {
    delegate.visitIntersection(((IntersectionType) proxy));
  }

  @Override
  public void visitNull(NullType nullType) {
    delegate.visitNull(((NullType) proxy));
  }

  @Override
  public void visitVoid(VoidType voidType) {
    delegate.visitVoid(((VoidType) proxy));
  }

  @Override
  public void visitNumber(NumberType numberType) {
    delegate.visitNumber(((NumberType) proxy));
  }

  @Override
  public void visitObject(ObjectType objectType) {
    delegate.visitObject(((ObjectType) proxy));
  }

  @Override
  public void visitString(StringType stringType) {
    delegate.visitString(((StringType) proxy));
  }

  @Override
  public void visitTime(TimeType timeType) {
    delegate.visitTime(((TimeType) proxy));
  }

  @Override
  public void visitTuple(TupleType tupleType) {
    delegate.visitTuple(((TupleType) proxy));
  }

  @Override
  public void visitUnion(UnionType unionType) {
    delegate.visitUnion(((UnionType) proxy));
  }

  @Override
  public void visitObjectKey(ObjectKeyType objectKeyType) {
    delegate.visitObjectKey((ObjectKeyType) proxy);
  }

  @Override
  public void visitAttributeKey(AttributeKeyType attributeKeyType) {
    delegate.visitAttributeKey((AttributeKeyType) proxy);
  }

  @Override
  public void visitAttributeField(AttributeFieldType attributeFieldType) {
    delegate.visitAttributeField((AttributeFieldType) proxy);
  }

  @Override
  public void visitObjectField(ObjectFieldType objectFieldType) {
    delegate.visitObjectField((ObjectFieldType) proxy);
  }

  @Override
  public void visitNothing(NothingType nothingType) {
    delegate.visitNothing((NothingType) proxy);
  }

  @Override
  public void visitFunction(FunctionType functionType) {
    delegate.visitFunction((FunctionType) proxy);
  }

  @Override
  public void visitLocalDateTime(LocalDateTimeType localDateTimeType) {
    delegate.visitLocalDateTime((LocalDateTimeType) proxy);
  }

  @Override
  public void visitLocalTime(LocalTimeType localTimeType) {
    delegate.visitLocalTime((LocalTimeType) proxy);
  }

  @Override
  public void visitPeriod(PeriodType periodType) {
    delegate.visitPeriod((PeriodType) proxy);
  }

  @Override
  public void visitRegex(RegexType regexType) {
    delegate.visitRegex((RegexType) proxy);
  }

  @Override
  public void visitTimeZone(TimeZoneType timeZoneType) {
    delegate.visitTimeZone((TimeZoneType) proxy);
  }

  @Override
  public void visitTypeParameter(TypeParameterType defaultTypeParameter) {
    delegate.visitTypeParameter((TypeParameterType) proxy);
  }
}
