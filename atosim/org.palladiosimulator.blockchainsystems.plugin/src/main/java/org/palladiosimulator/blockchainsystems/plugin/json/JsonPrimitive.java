package org.palladiosimulator.blockchainsystems.plugin.json;

public class JsonPrimitive extends JsonValue {
    private final String _content;
    private final boolean _isString;

    public JsonPrimitive(String content, boolean isString) {
        _content = content;
        _isString = isString;
    }

    public String getContent() { return _content; }
    public boolean isString() { return _isString; }
}
