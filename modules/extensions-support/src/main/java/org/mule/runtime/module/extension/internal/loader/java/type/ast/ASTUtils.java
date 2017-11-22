/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.loader.java.type.ast;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import org.mule.runtime.api.util.Reference;
import org.mule.runtime.module.extension.internal.loader.java.type.Type;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.function.Function;

import com.google.gson.reflect.TypeToken;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

public class ASTUtils {

  private ProcessingEnvironment processingEnvironment;

  public ASTUtils(ProcessingEnvironment processingEnvironment) {
    this.processingEnvironment = processingEnvironment;
  }


  public <T extends Annotation> ValueFetcher<T> fromAnnotation(Class<T> annotationClass, Element element) {
    return new ValueFetcher<>(annotationClass, element, processingEnvironment);
  }

  public static class ValueFetcher<T extends Annotation> {

    private final Class<T> configurationClass;
    private final Element typeElement;
    private ProcessingEnvironment processingEnvironment;

    public ValueFetcher(Class<T> configurationClass, Element typeElement, ProcessingEnvironment processingEnvironment) {
      this.configurationClass = configurationClass;
      this.typeElement = typeElement;
      this.processingEnvironment = processingEnvironment;
    }

    public String getStringValue(Function<T, String> function) {
      return (String) getConstant(function).getValue();
    }

    public List<Type> getClassArrayValue(Function<T, Class[]> function) {
      AnnotationValue value = (AnnotationValue) getObjectValue(function);
      if (value != null) {
        List<AnnotationValue> array = (List<AnnotationValue>) value.getValue();
        return array.stream().map(attr -> ((DeclaredType) attr.getValue()))
            .map(declaredType -> new ASTType((TypeElement) declaredType.asElement(), processingEnvironment))
            .collect(toList());
      } else {
        return emptyList();
      }
    }

    public Type getClassValue(Function<T, Class> function) {
      return new ASTType((TypeElement) ((DeclaredType) ((AnnotationValue) getObjectValue(function)).getValue()).asElement(),
                         processingEnvironment);
    }

    public Integer getIntValue(Function<T, Integer> function) {
      return (Integer) getConstant(function).getValue();
    }

    public <E extends Enum> VariableElement getEnumValue(Function<T, E> function) {
      return (VariableElement) ((AnnotationValue) getObjectValue(function)).getValue();
    }

    public <R> R getValue(Function<T, R> function) {
      return (R) getObjectValue(function);
    }

    AnnotationValue getConstant(Function function) {
      return (AnnotationValue) getObjectValue(function);
    }

    private Object getObjectValue(Function function) {
      return getExecutable(configurationClass, typeElement, function, processingEnvironment).get();
    }
  }

  public static <T> Reference<Object> getExecutable(Class<T> configurationClass, Element element, Function function,
                                                    ProcessingEnvironment processingEnvironment) {
    CountDownLatch latch = new CountDownLatch(1);
    Enhancer enhancer = new Enhancer();
    Reference<Object> reference = new Reference<>();
    enhancer.setSuperclass(configurationClass);
    enhancer.setCallback((MethodInterceptor) (o, method, objects, methodProxy) -> {
      if (method.getName().equals("toString")) {
        return "String";
      }
      System.out.println(Thread.currentThread().toString());
      reference.set(null);

      getAnnotationFrom(configurationClass, element, processingEnvironment)
          .ifPresent(annotation -> getAnnotationElementValue(annotation, method.getName())
              .ifPresent(reference::set));

      latch.countDown();
      return null;
    });
    function.apply((T) enhancer.create());
    return reference;
  }

  private static Optional<? extends AnnotationValue> getAnnotationElementValue(AnnotationMirror annotation, String name) {
    for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
      if (entry.getKey().getSimpleName().toString().equals(name)) {
        return of(entry.getValue());
      }
    }

    for (Element element : annotation.getAnnotationType().asElement().getEnclosedElements()) {
      if (element.getKind().equals(ElementKind.METHOD)) {
        if (element.getSimpleName().toString().equals(name)) {
          return ofNullable(((ExecutableElement) element).getDefaultValue());
        }
      }
    }

    return empty();
  }

  private static Optional<AnnotationMirror> getAnnotationFrom(Class configurationClass, Element typeElement,
                                                              ProcessingEnvironment processingEnvironment) {
    TypeElement annotationTypeElement = processingEnvironment.getElementUtils().getTypeElement(configurationClass.getTypeName());
    for (AnnotationMirror annotationMirror : typeElement.getAnnotationMirrors()) {
      DeclaredType annotationType = annotationMirror.getAnnotationType();
      TypeMirror obj = annotationTypeElement.asType();
      if (annotationType.equals(obj)) {
        return of(annotationMirror);
      }
    }
    return empty();
  }

  public static java.lang.reflect.Type getReflectType(Type type) {
    List<GenericInfo> generics = type.getGenerics();
    try {
      if (!generics.isEmpty()) {
        java.lang.reflect.Type[] types = generics.stream().map(generic -> {
          try {
            return Class.forName(generic.getConcreteType().getTypeName());
          } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
          }
        }).toArray(java.lang.reflect.Type[]::new);

        return TypeToken.getParameterized(Class.forName(type.getTypeName()), types).getType();
      } else {
        return Class.forName(type.getTypeName());
      }
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }
}
