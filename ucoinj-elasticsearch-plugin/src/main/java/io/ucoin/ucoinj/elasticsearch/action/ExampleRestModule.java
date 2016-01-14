package io.ucoin.ucoinj.elasticsearch.action;

import org.elasticsearch.common.inject.AbstractModule;

public class ExampleRestModule extends AbstractModule {

    @Override protected void configure() {
        bind(HelloRestHandler.class).asEagerSingleton();
    }
}