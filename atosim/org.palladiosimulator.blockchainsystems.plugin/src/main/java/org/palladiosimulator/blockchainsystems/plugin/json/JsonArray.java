package org.palladiosimulator.blockchainsystems.plugin.json;

import java.util.ArrayList;
import java.util.List;

public class JsonArray extends JsonValue {
    private final List<JsonValue> _elements;

    public JsonArray(List<JsonValue> elements) {
        _elements = elements;
    }

    public List<JsonValue> getElements() { return _elements; }
    public int size() { return _elements.size(); }
    public boolean isEmpty() { return _elements.isEmpty(); }

    static JsonArray parseArray(String s, int[] pos) {
        pos[0]++; // consume '['
        List<JsonValue> list = new ArrayList<>();
        skipWhitespace(s, pos);
        if (pos[0] < s.length() && s.charAt(pos[0]) == ']') { pos[0]++; return new JsonArray(list); }
        while (pos[0] < s.length()) {
            list.add(parseValueSkipping(s, pos));
            skipWhitespace(s, pos);
            if (pos[0] < s.length() && s.charAt(pos[0]) == ']') { pos[0]++; return new JsonArray(list); }
            pos[0]++; // consume ','
        }
        throw new IllegalArgumentException("Unterminated array");
    }
}
