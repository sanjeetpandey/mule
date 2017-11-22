/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class MuleTypeVisitor
    implements TypeVisitor<MuleTypeVisitor.TypeIntrospectionResult, MuleTypeVisitor.TypeIntrospectionResultBuilder> {

  private ProcessingEnvironment processingEnvironment;

  public MuleTypeVisitor(ProcessingEnvironment processingEnvironment) {
    this.processingEnvironment = processingEnvironment;
  }

  static Map<String, Class> builtInMap = new HashMap<String, Class>();
  {
    builtInMap.put("int", Integer.TYPE);
    builtInMap.put("long", Long.TYPE);
    builtInMap.put("double", Double.TYPE);
    builtInMap.put("float", Float.TYPE);
    builtInMap.put("bool", Boolean.TYPE);
    builtInMap.put("char", Character.TYPE);
    builtInMap.put("byte", Byte.TYPE);
    builtInMap.put("void", Void.TYPE);
    builtInMap.put("short", Short.TYPE);
  }
  static Map<String, Class> builtInMap2 = new HashMap<String, Class>();
  {
    builtInMap2.put("int", Integer.class);
    builtInMap2.put("long", Long.class);
    builtInMap2.put("double", Double.class);
    builtInMap2.put("float", Float.class);
    builtInMap2.put("boolean", Boolean.class);
    builtInMap2.put("char", Character.class);
    builtInMap2.put("byte", Byte.class);
    builtInMap2.put("void", Void.class);
    builtInMap2.put("short", Short.class);
  }

  @Override
  public TypeIntrospectionResult visit(TypeMirror t, TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    return null;
  }

  @Override
  public TypeIntrospectionResult visit(TypeMirror t) {
    return null;
  }

  @Override
  public TypeIntrospectionResult visitPrimitive(PrimitiveType t,
                                                TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    TypeElement typeElement = processingEnvironment.getElementUtils().getTypeElement(builtInMap2.get(t.toString()).getTypeName());
    return typeIntrospectionResultBuilder.setConcreteType(typeElement).build();
  }

  @Override
  public TypeIntrospectionResult visitNull(NullType t, TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    return null;
  }

  @Override
  public TypeIntrospectionResult visitArray(ArrayType t, TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    return null;
  }

  @Override
  public TypeIntrospectionResult visitDeclared(DeclaredType t, TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    typeIntrospectionResultBuilder.setConcreteType((TypeElement) t.asElement());

    for (TypeMirror typeMirror : t.getTypeArguments()) {
      TypeIntrospectionResult accept = typeMirror.accept(this, TypeIntrospectionResultBuilder.newBuilder());
      typeIntrospectionResultBuilder.addGenericType(accept.getConcreteType());
    }

    return typeIntrospectionResultBuilder.build();
  }

  @Override
  public TypeIntrospectionResult visitError(ErrorType t, TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    return null;
  }

  @Override
  public TypeIntrospectionResult visitTypeVariable(TypeVariable t,
                                                   TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    return null;
  }

  @Override
  public TypeIntrospectionResult visitWildcard(WildcardType t, TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    return null;
  }

  @Override
  public TypeIntrospectionResult visitExecutable(ExecutableType t,
                                                 TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    return null;
  }

  @Override
  public TypeIntrospectionResult visitNoType(NoType t, TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    typeIntrospectionResultBuilder
        .setConcreteType(processingEnvironment.getElementUtils().getTypeElement(Void.class.getTypeName()));
    return typeIntrospectionResultBuilder.build();
  }

  @Override
  public TypeIntrospectionResult visitUnknown(TypeMirror t, TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    return null;
  }

  @Override
  public TypeIntrospectionResult visitUnion(UnionType t, TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    return null;
  }

  @Override
  public TypeIntrospectionResult visitIntersection(IntersectionType t,
                                                   TypeIntrospectionResultBuilder typeIntrospectionResultBuilder) {
    return null;
  }

  public static class TypeIntrospectionResult {

    private final TypeElement concreteType;
    private final List<TypeElement> generics;

    public TypeIntrospectionResult(TypeElement concreteType, List<TypeElement> generics) {

      this.concreteType = concreteType;
      this.generics = generics;
    }

    public TypeElement getConcreteType() {
      return concreteType;
    }

    public List<TypeElement> getGenerics() {
      return generics;
    }
  }


  public static class TypeIntrospectionResultBuilder {

    TypeElement concreteType;
    List<TypeElement> generics = new ArrayList<>();

    public static TypeIntrospectionResultBuilder newBuilder() {
      return new TypeIntrospectionResultBuilder();
    }

    public TypeIntrospectionResultBuilder setConcreteType(TypeElement concreteType) {
      this.concreteType = concreteType;
      return this;
    }

    public TypeIntrospectionResultBuilder addGenericType(TypeElement genericType) {
      this.generics.add(genericType);
      return this;
    }

    public TypeIntrospectionResult build() {
      return new TypeIntrospectionResult(concreteType, generics);
    }
  }
}
