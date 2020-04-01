package com.notvanilla.kevlar.wireless;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public enum NodeType {

    TRANSMITTER(0), REPEATER(1), RECEIVER(2);

    private static final Int2ObjectMap<NodeType> BY_ID = new Int2ObjectOpenHashMap<>();

    private final int id;
    NodeType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static NodeType byId(int id) {
        return BY_ID.getOrDefault(id, REPEATER);
    }

    static {
        for (NodeType val : values()) {
            BY_ID.put(val.getId(), val);
        }
    }
}
