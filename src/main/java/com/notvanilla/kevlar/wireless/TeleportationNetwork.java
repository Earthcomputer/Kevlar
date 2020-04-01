package com.notvanilla.kevlar.wireless;

public class TeleportationNetwork extends KevlarNetwork<TeleportationNode> {

    public TeleportationNetwork() {
        super(64);
    }

    @Override
    protected TeleportationNode createNode() {
        return new TeleportationNode();
    }
}
