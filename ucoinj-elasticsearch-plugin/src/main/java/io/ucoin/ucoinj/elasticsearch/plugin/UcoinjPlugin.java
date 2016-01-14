package io.ucoin.ucoinj.elasticsearch.plugin;

import org.elasticsearch.plugins.AbstractPlugin;

public class UcoinjPlugin extends AbstractPlugin { @Override public String name() { return "example-plugin"; }

@Override public String description() { return "Example Plugin Description"; }

}