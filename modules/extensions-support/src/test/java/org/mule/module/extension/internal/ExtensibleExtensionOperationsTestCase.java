/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.module.extension.internal;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.mule.module.extension.HeisenbergExtension.EXTENSION_DESCRIPTION;
import static org.mule.module.extension.HeisenbergExtension.HEISENBERG;
import org.mule.extension.api.annotation.Extensible;
import org.mule.extension.api.annotation.Extension;
import org.mule.extension.api.annotation.ExtensionOf;
import org.mule.extension.api.annotation.Operations;
import org.mule.extension.api.introspection.declaration.fluent.Declaration;
import org.mule.extension.api.introspection.declaration.fluent.OperationDeclaration;
import org.mule.module.extension.internal.model.property.ExtendingOperationModelProperty;

import org.junit.Test;

public class ExtensibleExtensionOperationsTestCase extends AbstractAnnotationsBasedDescriberTestCase
{

    private static final String SAY_HELLO_OPERATION = "sayHello";
    private static final String SAY_BYE_OPERATION = "sayBye";

    @Test
    public void operationIsExtensionOfSameExtension() throws Exception
    {
        setDescriber(describerFor(ExtensibleExtension.class));
        assertOperationExtensionOf(SAY_HELLO_OPERATION, ExtensibleExtension.class);
    }

    @Test
    public void operationIsExtensionOfDifferentExtension()
    {
        setDescriber(describerFor(ExtendingExtension.class));
        assertOperationExtensionOf(SAY_HELLO_OPERATION, ExtensibleExtension.class);
        assertOperationExtensionOf(SAY_BYE_OPERATION, ExtensibleExtension.class);
    }

    @Test
    public void operationIsExtensionOfSameAndDifferentExtension()
    {
        setDescriber(describerFor(ExtensibleExtendingExtension.class));
        assertOperationExtensionOf(SAY_HELLO_OPERATION, ExtensibleExtendingExtension.class);
        assertOperationExtensionOf(SAY_BYE_OPERATION, ExtensibleExtension.class);
    }

    private void assertOperationExtensionOf(String operationName, Class propertyType)
    {
        Declaration declaration = getDescriber().describe(new DefaultDescribingContext()).getRootDeclaration().getDeclaration();
        OperationDeclaration operation = getOperation(declaration, operationName);

        ExtendingOperationModelProperty<ExtensibleExtension> modelProperty = operation.getModelProperty(ExtendingOperationModelProperty.KEY);
        assertThat(modelProperty.getType(), is(sameInstance(propertyType)));
    }

    @Extension(name = HEISENBERG, description = EXTENSION_DESCRIPTION)
    @Operations(ExtensibleExtensionOperation.class)
    @Extensible
    public static class ExtensibleExtension
    {

    }

    @Extension(name = HEISENBERG, description = EXTENSION_DESCRIPTION)
    @Operations({ClassLevelExtensionOfOperation.class, MethodLevelExtensionOfOperation.class})
    public static class ExtendingExtension
    {

    }

    @Extension(name = HEISENBERG, description = EXTENSION_DESCRIPTION)
    @Operations({MethodLevelExtensionOfOperation.class, ExtensibleExtensionOperation.class})
    @Extensible
    public static class ExtensibleExtendingExtension
    {

    }

    @ExtensionOf(ExtensibleExtension.class)
    private static class ClassLevelExtensionOfOperation
    {

        public String sayHello()
        {
            return "Hello!";
        }
    }


    private static class MethodLevelExtensionOfOperation
    {

        @ExtensionOf(ExtensibleExtension.class)
        public String sayBye()
        {
            return "Bye!";
        }
    }

    private static class ExtensibleExtensionOperation
    {

        public String sayHello()
        {
            return "Hello!";
        }
    }
}
