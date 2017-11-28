/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.module.extension.internal.loader.enricher.fixed;

import static java.lang.reflect.Proxy.newProxyInstance;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

import org.mule.metadata.api.annotation.TypeAnnotation;
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

import com.google.common.collect.ImmutableSet;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Proxy wrapper for {@link MetadataType} in order to add annotation dynamically
 *
 * @since 4.1
 */
public class MetadataTypeProxy implements InvocationHandler {

  private static final String GET_ANNOTATION_METHOD = "getAnnotation";
  private static final String GET_ANNOTATIONS_METHOD = "getAnnotations";
  private static final String ACCEPT_METHOD = "accept";

  private static final Method OBJECT_EQUALS = getObjectMethod("equals", Object.class);
  private static final Method OBJECT_HASHCODE = getObjectMethod("hashCode");

  private MetadataType target;
  private Map<Class<? extends TypeAnnotation>, TypeAnnotation> annotations;

  private MetadataTypeProxy(MetadataType target, Map<Class<? extends TypeAnnotation>, TypeAnnotation> annotations) {
    this.target = target;
    this.annotations = annotations;
  }

  private MetadataType getTarget() {
    return target;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (OBJECT_EQUALS.equals(method)) {
      return equalsInternal(proxy, args[0]);
    }

    if (OBJECT_HASHCODE == method) {
      return target.hashCode();
    }

    Object result = null;
    String methodName = method.getName();
    if (GET_ANNOTATION_METHOD.equals(methodName) && annotations.containsKey(args[0])) {
      result = Optional.of(annotations.get(args[0]));
    } else if (GET_ANNOTATIONS_METHOD.equals(methodName)) {
      result = Stream.concat(annotations.values().stream(), target.getAnnotations().stream()).collect(toSet());
    } else if (ACCEPT_METHOD.equals(methodName)) {
      target.accept(new ProxyDelegateMetadataTypeVisitor(((MetadataTypeVisitor) args[0]), ((MetadataType) proxy)));
    } else {
      result = method.invoke(target, args);
    }
    return result;
  }

  private boolean equalsInternal(Object me, Object other) {
    if (other == null) {
      return false;
    }
    if (!Proxy.isProxyClass(other.getClass())) {
      return other.equals(target);
    } else {
      Optional<MetadataType> otherTarget = getTarget(other);
      return otherTarget.isPresent() ? otherTarget.get().equals(target) : false;
    }
  }

  private Optional<MetadataType> getTarget(Object object) {
    InvocationHandler handler = Proxy.getInvocationHandler(object);
    if (!(handler instanceof MetadataTypeProxy)) {
      return Optional.empty();
    }
    return Optional.of(((MetadataTypeProxy) handler).target);
  }

  private static Method getObjectMethod(String name, Class<?>... types) {
    try {
      // null 'types' is OK.
      return Object.class.getMethod(name, types);
    } catch (NoSuchMethodException e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static MetadataType doProxy(MetadataType target, Set<TypeAnnotation> annotations) {
    Class<? extends MetadataType> clazz = target.getClass();
    Set<TypeAnnotation> all = ImmutableSet.<TypeAnnotation>builder().addAll(target.getAnnotations()).addAll(annotations).build();
    Map<Class<? extends TypeAnnotation>, TypeAnnotation> map = all.stream().collect(toMap(TypeAnnotation::getClass, identity()));
    return (MetadataType) newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), new MetadataTypeProxy(target, map));
  }

  public static MetadataType undoProxy(MetadataType proxy) {
    MetadataType unwrapped = proxy;
    if (Proxy.isProxyClass(proxy.getClass())) {
      InvocationHandler invocationHandler = Proxy.getInvocationHandler(unwrapped);
      if (invocationHandler instanceof MetadataTypeProxy) {
        MetadataTypeProxy metadataTypeProxy = (MetadataTypeProxy) invocationHandler;
        unwrapped = metadataTypeProxy.getTarget();
      }
    }
    return unwrapped;
  }
}
