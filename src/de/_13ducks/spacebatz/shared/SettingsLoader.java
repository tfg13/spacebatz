package de._13ducks.spacebatz.shared;

import java.lang.reflect.Field;

/**
 * Liest die privaten Einstellungen, die keiner Versionskontrolle unterliegen und überschreibt die richtigen Einstellungen damit.
 *
 * @author Tobias Fleig <tobifleig@googlemail.com>
 */
public class SettingsLoader {

    /**
     * Privater Konstruktor, da Utility-Class
     */
    private SettingsLoader() {
    }

    public static void overrideValues() {
        Class<DefaultSettings> defaultSettings = DefaultSettings.class;
        Class privateSettings = null;
        try {
        privateSettings = ClassLoader.getSystemClassLoader().loadClass("de._13ducks.spacebatz.MySettings");
        for (Field myField : privateSettings.getDeclaredFields()) {
            try {
                Field realField = defaultSettings.getDeclaredField(myField.getName());
                if (myField.getType().equals(realField.getType())) {
                    try {
                        realField.set(null, myField.get(null));
                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    } catch (IllegalAccessException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    System.out.println("WARN: Settings: Cannot override Field \"" + myField.getName() + "\", types do not match!");
                }
            } catch (NoSuchFieldException ex) {
                // Diese Einstellung kann nicht überschrieben werden, denn die gibt es nicht.
                System.out.println("WARN: Settings: Cannot override Field \"" + myField.getName() + "\", no such Field in DefaultSettings");
            }
        }
        } catch (ClassNotFoundException ex) {
            System.out.println("INFO: Settings: No private settings found, using defaults");
        }
    }
}
