package org.mule.runtime.module.extension.internal.capability.xml.extension.multiple.config;

import org.mule.runtime.api.connection.ConnectionProvider;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.extension.api.annotation.param.Connection;
import org.mule.runtime.extension.api.runtime.source.Source;
import org.mule.runtime.extension.api.runtime.source.SourceCallback;

public class ASource extends Source<String, String> {

    @Connection
    ConnectionProvider<String> connectionProvider;

    @Override
    public void onStart(SourceCallback<String, String> sourceCallback) throws MuleException {

    }

    @Override
    public void onStop() {

    }
}
