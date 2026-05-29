package org.palladiosimulator.blockchainsystems.plugin.json;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class JsonObject extends JsonValue {
    private final Map<String, JsonValue> _entries;

    public JsonObject(Map<String, JsonValue> entries) {
        _entries = entries;
    }

    public Set<Map.Entry<String, JsonValue>> entrySet() { return _entries.entrySet(); }
    public int size() { return _entries.size(); }
    public boolean isEmpty() { return _entries.isEmpty(); }

    static JsonObject parseObject(String s, int[] pos) {
        pos[0]++; // consume '{'
        Map<String, JsonValue> map = new LinkedHashMap<>();
        skipWhitespace(s, pos);
        if (pos[0] < s.length() && s.charAt(pos[0]) == '}') { pos[0]++; return new JsonObject(map); }
        while (pos[0] < s.length()) {
            skipWhitespace(s, pos);
            JsonPrimitive key = (JsonPrimitive) parseString(s, pos);
            skipWhitespace(s, pos);
            pos[0]++; // consume ':'
            JsonValue value = parseValueSkipping(s, pos);
            map.put(key.getContent(), value);
            skipWhitespace(s, pos);
            if (pos[0] < s.length() && s.charAt(pos[0]) == '}') { pos[0]++; return new JsonObject(map); }
            pos[0]++; // consume ','
        }
        throw new IllegalArgumentException("Unterminated object");
    }
}
