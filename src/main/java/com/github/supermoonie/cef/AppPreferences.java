package com.github.supermoonie.cef;

import java.util.prefs.Preferences;

/**
 * @author supermoonie
 * @since 2021/2/25
 */
public class AppPreferences {

    public static final String KEY_LOCAL_VERSION = "localVersion";

    public static final float DEFAULT_LOCAL_VERSION = 1.1f;

    private static Preferences state;

    public static void init(String rootPath) {
        state = Preferences.userRoot().node(rootPath);
    }

    public static Preferences getState() {
        return state;
    }
}
