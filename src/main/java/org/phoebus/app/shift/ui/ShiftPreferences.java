package org.phoebus.app.shift.ui;

import java.util.prefs.Preferences;

/**
 * Persistent preferences for the shift app, backed by java.util.prefs.Preferences.
 * System properties always take precedence (for Docker / container deployments).
 * Stored prefs are used when running standalone between sessions.
 */
public class ShiftPreferences {

    private static final Preferences prefs =
            Preferences.userNodeForPackage(ShiftPreferences.class);

    // -------------------------------------------------------------------------
    // Accessors — system property > stored pref > default
    // -------------------------------------------------------------------------

    public static String getShiftUrl() {
        return sysProp("shift.url", prefs.get("shift.url", "http://localhost:8282/Shift/resources"));
    }

    public static String getShiftTypes() {
        return sysProp("shift.type", prefs.get("shift.type", "Operations"));
    }

    public static String getUsername() {
        return sysProp("shift.username", prefs.get("shift.username", ""));
    }

    public static String getPassword() {
        return sysProp("shift.password", prefs.get("shift.password", ""));
    }

    public static int getCacheTtlSeconds() {
        return parseInt(sysProp("shift.cache.ttl", prefs.get("shift.cache.ttl", "30")), 30);
    }

    public static int getConnectTimeoutMs() {
        return parseInt(sysProp("shift.connect.timeout",
                prefs.get("shift.connect.timeout", "3000")), 3000);
    }

    public static int getReadTimeoutMs() {
        return parseInt(sysProp("shift.read.timeout",
                prefs.get("shift.read.timeout", "5000")), 5000);
    }

    // -------------------------------------------------------------------------
    // Setters — only persist to prefs; system properties are not modified
    // -------------------------------------------------------------------------

    public static void setShiftUrl(String v)         { prefs.put("shift.url", v); }
    public static void setShiftTypes(String v)       { prefs.put("shift.type", v); }
    public static void setUsername(String v)         { prefs.put("shift.username", v); }
    public static void setPassword(String v)         { prefs.put("shift.password", v); }
    public static void setCacheTtlSeconds(int v)     { prefs.put("shift.cache.ttl", String.valueOf(v)); }
    public static void setConnectTimeoutMs(int v)    { prefs.put("shift.connect.timeout", String.valueOf(v)); }
    public static void setReadTimeoutMs(int v)       { prefs.put("shift.read.timeout", String.valueOf(v)); }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static String sysProp(String key, String fallback) {
        String v = System.getProperty(key);
        return (v != null && !v.isEmpty()) ? v : fallback;
    }

    private static int parseInt(String value, int defaultValue) {
        try { return Integer.parseInt(value); }
        catch (NumberFormatException e) { return defaultValue; }
    }
}
