package io.ucoin.ucoinj.elasticsearch.plugin;

import com.google.common.collect.Lists;
import io.ucoin.ucoinj.elasticsearch.action.ExampleRestModule;
import io.ucoin.ucoinj.elasticsearch.action.HelloRestHandler;
import org.elasticsearch.common.inject.Module;
import org.elasticsearch.rest.RestModule;

import java.util.Collection;

public class Plugin extends org.elasticsearch.plugins.Plugin {

    @Override
    public String name() {
        return "ucoinj-elasticsearch";
    }

    @Override
    public String description() {
        return "uCoinj ElasticSearch Plugin";
    }

    /*
    @Override
    public Collection<Module> nodeModules() {
        Collection<Module> modules = Lists.newArrayList();
        modules.add(ExampleRestModule.class);
        return modules;
    }*/

    public void onModule(RestModule module) {
        module.addRestAction(HelloRestHandler.class);
    }
}