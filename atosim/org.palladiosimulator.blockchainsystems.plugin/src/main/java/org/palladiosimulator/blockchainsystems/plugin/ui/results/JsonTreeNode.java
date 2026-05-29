package org.palladiosimulator.blockchainsystems.plugin.ui.results;

import org.palladiosimulator.blockchainsystems.plugin.json.JsonArray;
import org.palladiosimulator.blockchainsystems.plugin.json.JsonNull;
import org.palladiosimulator.blockchainsystems.plugin.json.JsonObject;
import org.palladiosimulator.blockchainsystems.plugin.json.JsonPrimitive;
import org.palladiosimulator.blockchainsystems.plugin.json.JsonValue;

public class JsonTreeNode {

    public final String key;
    public final JsonValue value;
    public final JsonTreeNode parent;

    public JsonTreeNode(String key, JsonValue value, JsonTreeNode parent) {
        this.key = key;
        this.value = value;
        this.parent = parent;
    }

    public JsonTreeNode(String key, JsonValue value) {
        this(key, value, null);
    }

    public String getDisplayText() {
        if (value instanceof JsonPrimitive p) {
            return p.isString() ? key + ": \"" + p.getContent() + "\"" : key + ": " + p.getContent();
        }
        if (value instanceof JsonObject o) {
            return getDisplayTextWithChildren(key, o.size(), "{}");
        }
        if (value instanceof JsonArray a) {
            return getDisplayTextWithChildren(key, a.size(), "[]");
        }
        if (value instanceof JsonNull) {
            return key + ": null";
        }
        return key;
    }

    private String getDisplayTextWithChildren(String k, int size, String wrapper) {
        String descr = size == 1 ? "item" : "items";
        return k + ": " + wrapper.charAt(0) + " " + size + " " + descr + " " + wrapper.charAt(1);
    }

    @Override
    public String toString() {
        return getDisplayText();
    }
}
