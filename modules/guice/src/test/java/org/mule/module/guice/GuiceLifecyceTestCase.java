/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.guice;

import org.mule.module.client.MuleClient;
import org.mule.registry.AbstractLifecycleTracker;
import org.mule.tck.AbstractMuleTestCase;

import org.junit.Assert;

public class GuiceLifecyceTestCase extends AbstractMuleTestCase
{
    @Override
    protected void doSetUp() throws Exception
    {
        GuiceConfigurationBuilder cb = new GuiceConfigurationBuilder(new GuiceLifecycleModule());
        cb.configure(muleContext);
    }

    /**
     * ASSERT: - Mule lifecycle methods invoked - Service and muleContext injected
     * (Component implements ServiceAware/MuleContextAware)
     * 
     * @throws Exception
     */
    public void testSingletonServiceLifecycle() throws Exception
    {
        testComponentLifecycle("MuleSingletonService",
            "[setProperty, setMuleContext, setService, initialise, start, stop, dispose]");
    }

    /**
     * ASSERT: - Mule lifecycle methods invoked - Service and muleContext injected
     * (Component implements ServiceAware/MuleContextAware)
     * 
     * @throws Exception
     */
    public void testMulePrototypeServiceLifecycle() throws Exception
    {
        testComponentLifecycle("MulePrototypeService",
            "[setProperty, setMuleContext, setService, initialise, start, stop, dispose]");
    }

    /**
     * ASSERT: - Mule lifecycle methods invoked each time singleton is used to create
     * new object in pool - Service and muleContext injected each time singleton is
     * used to create new object in pool (Component implements
     * ServiceAware/MuleContextAware)
     * 
     * @throws Exception
     */
    public void testMulePooledSingletonServiceLifecycle() throws Exception
    {
        // Initialisation policy not enabled in iBeans
        // testComponentLifecycle("MulePooledSingletonService",
        // "[setProperty, setMuleContext, setService, initialise, initialise, initialise, start, start, start, stop, stop, stop, dispose, dispose, dispose]");
        testComponentLifecycle("MulePooledSingletonService",
            "[setProperty, setMuleContext, setService, initialise, start, stop, dispose]");
    }

    private void testComponentLifecycle(final String serviceName, final String expectedLifeCycle)
        throws Exception
    {

        final AbstractLifecycleTracker tracker = exerciseComponent(serviceName);

        muleContext.dispose();

        Assert.assertEquals(serviceName, expectedLifeCycle, tracker.getTracker().toString());
    }

    private AbstractLifecycleTracker exerciseComponent(final String serviceName) throws Exception
    {
        MuleClient muleClient = new MuleClient(muleContext);
        final AbstractLifecycleTracker ltc = (AbstractLifecycleTracker)muleClient.send(
            "vm://" + serviceName + ".In", null, null).getPayload();

        Assert.assertNotNull(ltc);

        return ltc;
    }

    public class GuiceLifecycleModule extends AbstractMuleGuiceModule
    {
        @Override
        protected void doConfigure() throws Exception
        {

        }

        // @Provides @AnnotatedService
        // public PrototypeService createPrototypeService()
        // {
        // PrototypeService service = new PrototypeService();
        // service.setProperty("mps");
        // return service;
        // }
        //
        // @Provides @AnnotatedService @Singleton
        // public SingletonService createSingletonService()
        // {
        // SingletonService service = new SingletonService();
        // service.setProperty("mms");
        // return service;
        // }
        //
        // @Provides @AnnotatedService
        // public PooledService createPooledService()
        // {
        // PooledService service = new PooledService();
        // service.setProperty("mmps");
        // return service;
        // }
    }
}
