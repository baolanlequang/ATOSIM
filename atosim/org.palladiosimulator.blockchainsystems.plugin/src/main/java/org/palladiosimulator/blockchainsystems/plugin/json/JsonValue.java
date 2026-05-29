package org.palladiosimulator.blockchainsystems.plugin.json;

/**
 * Abstract base for a minimal JSON value hierarchy used by the results explorer.
 * Replaces kotlinx.serialization.json.JsonElement.
 */
public abstract class JsonValue {

    public static JsonValue parse(String json) {
        int[] pos = {0};
        skipWhitespace(json, pos);
        JsonValue result = parseValue(json, pos);
        return result;
    }

    private static JsonValue parseValue(String s, int[] pos) {
        if (pos[0] >= s.length()) throw new IllegalArgumentException("Unexpected end of input");
        char c = s.charAt(pos[0]);
        if (c == '{') return JsonObject.parseObject(s, pos);
        if (c == '[') return JsonArray.parseArray(s, pos);
        if (c == '"') return parseString(s, pos);
        if (c == 't' || c == 'f') return parseBoolean(s, pos);
        if (c == 'n') return parseNull(s, pos);
        return parseNumber(s, pos);
    }

    static JsonValue parseString(String s, int[] pos) {
        pos[0]++; // consume opening "
        StringBuilder sb = new StringBuilder();
        while (pos[0] < s.length()) {
            char c = s.charAt(pos[0]++);
            if (c == '"') return new JsonPrimitive(sb.toString(), true);
            if (c == '\\' && pos[0] < s.length()) {
                char esc = s.charAt(pos[0]++);
                switch (esc) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'u' -> {
                        String hex = s.substring(pos[0], pos[0] + 4);
                        sb.append((char) Integer.parseInt(hex, 16));
                        pos[0] += 4;
                    }
                    default -> sb.append(esc);
                }
            } else {
                sb.append(c);
            }
        }
        throw new IllegalArgumentException("Unterminated string");
    }

    private static JsonValue parseBoolean(String s, int[] pos) {
        if (s.startsWith("true", pos[0])) { pos[0] += 4; return new JsonPrimitive("true", false); }
        if (s.startsWith("false", pos[0])) { pos[0] += 5; return new JsonPrimitive("false", false); }
        throw new IllegalArgumentException("Unexpected token at " + pos[0]);
    }

    private static JsonValue parseNull(String s, int[] pos) {
        if (s.startsWith("null", pos[0])) { pos[0] += 4; return JsonNull.INSTANCE; }
        throw new IllegalArgumentException("Unexpected token at " + pos[0]);
    }

    private static JsonValue parseNumber(String s, int[] pos) {
        int start = pos[0];
        if (pos[0] < s.length() && s.charAt(pos[0]) == '-') pos[0]++;
        while (pos[0] < s.length() && (Character.isDigit(s.charAt(pos[0])) ||
                s.charAt(pos[0]) == '.' || s.charAt(pos[0]) == 'e' ||
                s.charAt(pos[0]) == 'E' || s.charAt(pos[0]) == '+' || s.charAt(pos[0]) == '-')) {
            pos[0]++;
        }
        return new JsonPrimitive(s.substring(start, pos[0]), false);
    }

    static void skipWhitespace(String s, int[] pos) {
        while (pos[0] < s.length() && Character.isWhitespace(s.charAt(pos[0]))) pos[0]++;
    }

    static JsonValue parseValueSkipping(String s, int[] pos) {
        skipWhitespace(s, pos);
        return parseValue(s, pos);
    }
}
