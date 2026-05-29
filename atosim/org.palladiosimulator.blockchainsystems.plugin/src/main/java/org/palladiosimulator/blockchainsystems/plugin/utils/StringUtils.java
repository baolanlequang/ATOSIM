package org.palladiosimulator.blockchainsystems.plugin.utils;

public final class StringUtils {

    public static final long MINIMUM_PORT = 1;
    public static final long MAXIMUM_PORT = 65536;

    private StringUtils() {}

    public static boolean isPort(String s) {
        if (s == null) return false;
        try {
            long nr = Long.parseLong(s);
            return nr >= MINIMUM_PORT && nr <= MAXIMUM_PORT;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
