/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.capability.xml.description;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;
import static javax.lang.model.SourceVersion.RELEASE_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mule.runtime.module.extension.internal.resources.ExtensionResourcesGeneratorAnnotationProcessor.EXTENSION_VERSION;

import org.mule.runtime.api.meta.model.ExtensionModel;
import org.mule.runtime.api.meta.model.declaration.fluent.ExtensionDeclaration;
import org.mule.runtime.extension.internal.loader.DefaultExtensionLoadingContext;
import org.mule.runtime.extension.internal.loader.ExtensionModelFactory;
import org.mule.runtime.module.extension.internal.AbstractAnnotationProcessorTestCase;
import org.mule.runtime.module.extension.internal.capability.xml.description.extension.ASTExtension;
import org.mule.tck.size.SmallTest;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

@SmallTest
public class ASTExtensionGeneratorTestCase extends AbstractAnnotationProcessorTestCase {

  @Test
  public void persistDocumentation2() throws Exception {
    TestProcessor processor = new TestProcessor(ASTExtension.class);
    doCompile(processor);

    ProcessingEnvironment processingEnvironment = processor.getProcessingEnvironment();
    RoundEnvironment roundEnviroment = processor.getRoundEnviroment();

    System.out.println("hola");
  }

  private void doCompile(AbstractProcessor processor) throws Exception {
    assert_().about(javaSources()).that(testSourceFiles()).withCompilerOptions("-Aextension.version=1.0.0-dev")
        .processedWith(processor).compilesWithoutError();
  }

  @SupportedAnnotationTypes(value = {"org.mule.runtime.extension.api.annotation.Extension"})
  @SupportedSourceVersion(RELEASE_8)
  @SupportedOptions(EXTENSION_VERSION)
  private class TestProcessor extends AbstractProcessor {

    private final Class<?> extensionClass;
    private ExtensionDeclaration declaration;
    private DefaultExtensionLoadingContext ctx;
    private RoundEnvironment roundEnv;
    private ProcessingEnvironment processingEnvironment;

    public TestProcessor(Class<?> extensionClass) {
      this.extensionClass = extensionClass;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
      this.roundEnv = roundEnv;
      this.processingEnvironment = processingEnvironment;
      if (declaration == null) {
        //                ExtensionDescriptionDocumenter documenter = new ExtensionDescriptionDocumenter(processingEnv, roundEnv);
        //                Element extension = roundEnv.getElementsAnnotatedWith(Extension.class).stream()
        //                        .filter(element -> element.getSimpleName().contentEquals(extensionClass.getSimpleName()))
        //                        .findFirst()
        //                        .get();

        //                assertThat(extension, instanceOf(TypeElement.class));
        //                ctx = new DefaultExtensionLoadingContext(currentThread().getContextClassLoader(), getDefault(emptySet()));
        //                DefaultJavaModelLoaderDelegate loader = new DefaultJavaModelLoaderDelegate(extensionClass, "1.0.0-dev");
        //                declaration = loader.declare(ctx).getDeclaration();
        //                documenter.document(declaration, (TypeElement) extension);
      }
      return false;
    }

    ExtensionModel getExtensionModel() {
      ExtensionModelFactory factory = new ExtensionModelFactory();
      return factory.create(ctx);
    }

    RoundEnvironment getRoundEnviroment() {
      return roundEnv;
    }

    ProcessingEnvironment getProcessingEnvironment() {
      return processingEnvironment;
    }
  }

  protected Iterable<JavaFileObject> testSourceFiles() throws Exception {
    // this will be xxx/target/test-classes
    File folder = new File(getClass().getClassLoader().getResource("").getPath().toString());
    // up to levels
    folder = folder.getParentFile().getParentFile();
    folder = new File(folder, "src/test/java/org/mule/runtime/module/extension/internal/capability/xml/description/extension");
    File[] files = folder.listFiles((dir, name) -> name.endsWith(".java"));
    assertThat(files, is(notNullValue()));
    List<JavaFileObject> javaFileObjects = new ArrayList<>(files.length);
    for (File file : files) {
      javaFileObjects.add(JavaFileObjects.forResource(file.toURI().toURL()));
    }
    return javaFileObjects;
  }
}
