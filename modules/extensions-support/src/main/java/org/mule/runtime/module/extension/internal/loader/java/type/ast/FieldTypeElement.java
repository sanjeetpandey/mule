/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import static org.mule.runtime.module.extension.internal.loader.java.type.ast.ASTUtils.getReflectType;

import org.mule.runtime.api.util.LazyValue;
import org.mule.runtime.module.extension.internal.loader.java.type.FieldElement;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class FieldTypeElement implements FieldElement {

  private VariableElement elem;
  private ProcessingEnvironment processingEnvironment;
  private LazyValue<org.mule.runtime.module.extension.internal.loader.java.type.Type> outputType;

  public FieldTypeElement(VariableElement elem, ProcessingEnvironment processingEnvironment) {
    this.elem = elem;
    this.processingEnvironment = processingEnvironment;

    this.outputType = new LazyValue<>(() -> {

      //THINK WHAT TO DO with primitives

      return new ASTType(elem.asType(), processingEnvironment);
    });
  }

  @Override
  public Field getField() {
    return null;
  }

  @Override
  public Type getJavaType() {
    TypeElement concreteType = null;
    try {
      VariableElement var = elem;
      var.asType().getKind();
      TypeMirror typeMirror = elem.asType();
      if (getType().isPrimitive()) {
        return builtInMap.get(typeMirror.toString());
      }

      MuleTypeVisitor.TypeIntrospectionResult accept = typeMirror
          .accept(new MuleTypeVisitor(processingEnvironment), MuleTypeVisitor.TypeIntrospectionResultBuilder.newBuilder());

      concreteType = accept.getConcreteType();

      return getReflectType(getType());
    } catch (Exception e) {
      String s = concreteType.toString();
      int i = s.lastIndexOf(".");
      String substring = s.substring(0, i);
      try {
        return Class.forName(substring + "$" + s.substring(i + 1, s.length()));
      } catch (ClassNotFoundException e1) {
        throw new RuntimeException(e);
      }
    }
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

  @Override
  public String getName() {
    return elem.getSimpleName().toString();
  }

  @Override
  public <A extends Annotation> Optional<A> getAnnotation(Class<A> annotationClass) {
    return Optional.ofNullable(elem.getAnnotation(annotationClass));
  }

  @Override
  public String getOwnerDescription() {
    return null;
  }

  @Override
  public org.mule.runtime.module.extension.internal.loader.java.type.Type getType() {
    return outputType.get();
  }
}
