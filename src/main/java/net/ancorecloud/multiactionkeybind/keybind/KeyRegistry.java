package net.ancorecloud.multiactionkeybind.keybind;

import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Registry of supported keyboard keys and mouse buttons,
 * mapping GLFW codes to human-readable display names.
 */
public final class KeyRegistry {

    private KeyRegistry() {}

    /** All supported keyboard entries: code → displayName */
    public static final Map<Integer, String> KEYBOARD_KEYS = new LinkedHashMap<>();
    /** All supported mouse button entries: code → displayName */
    public static final Map<Integer, String> MOUSE_BUTTONS = new LinkedHashMap<>();

    static {
        // Letters
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_A, "A");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_B, "B");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_C, "C");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_D, "D");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_E, "E");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F, "F");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_G, "G");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_H, "H");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_I, "I");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_J, "J");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_K, "K");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_L, "L");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_M, "M");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_N, "N");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_O, "O");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_P, "P");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_Q, "Q");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_R, "R");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_S, "S");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_T, "T");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_U, "U");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_V, "V");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_W, "W");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_X, "X");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_Y, "Y");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_Z, "Z");

        // Digits
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_0, "0");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_1, "1");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_2, "2");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_3, "3");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_4, "4");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_5, "5");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_6, "6");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_7, "7");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_8, "8");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_9, "9");

        // Special keys
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_SPACE, "SPACE");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_LEFT_SHIFT, "L.SHIFT");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_RIGHT_SHIFT, "R.SHIFT");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_LEFT_CONTROL, "L.CTRL");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_RIGHT_CONTROL, "R.CTRL");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_LEFT_ALT, "L.ALT");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_RIGHT_ALT, "R.ALT");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_TAB, "TAB");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_ENTER, "ENTER");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_ESCAPE, "ESCAPE");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_BACKSPACE, "BACKSPACE");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_DELETE, "DELETE");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_UP, "UP");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_DOWN, "DOWN");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_LEFT, "LEFT");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_RIGHT, "RIGHT");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_HOME, "HOME");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_END, "END");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_PAGE_UP, "PG UP");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_PAGE_DOWN, "PG DN");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_INSERT, "INSERT");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_CAPS_LOCK, "CAPS");

        // Function keys
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F1, "F1");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F2, "F2");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F3, "F3");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F4, "F4");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F5, "F5");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F6, "F6");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F7, "F7");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F8, "F8");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F9, "F9");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F10, "F10");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F11, "F11");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_F12, "F12");

        // Numpad
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_KP_0, "NP 0");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_KP_1, "NP 1");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_KP_2, "NP 2");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_KP_3, "NP 3");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_KP_4, "NP 4");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_KP_5, "NP 5");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_KP_6, "NP 6");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_KP_7, "NP 7");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_KP_8, "NP 8");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_KP_9, "NP 9");

        // Symbols
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_GRAVE_ACCENT, "`");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_MINUS, "-");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_EQUAL, "=");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_LEFT_BRACKET, "[");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_RIGHT_BRACKET, "]");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_BACKSLASH, "\\");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_SEMICOLON, ";");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_APOSTROPHE, "'");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_COMMA, ",");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_PERIOD, ".");
        KEYBOARD_KEYS.put(GLFW.GLFW_KEY_SLASH, "/");

        // Mouse
        MOUSE_BUTTONS.put(GLFW.GLFW_MOUSE_BUTTON_LEFT, "LEFT CLICK");
        MOUSE_BUTTONS.put(GLFW.GLFW_MOUSE_BUTTON_RIGHT, "RIGHT CLICK");
        MOUSE_BUTTONS.put(GLFW.GLFW_MOUSE_BUTTON_MIDDLE, "MIDDLE CLICK");
        MOUSE_BUTTONS.put(GLFW.GLFW_MOUSE_BUTTON_4, "MOUSE 4");
        MOUSE_BUTTONS.put(GLFW.GLFW_MOUSE_BUTTON_5, "MOUSE 5");
    }

    public static String getKeyboardDisplayName(int code) {
        return KEYBOARD_KEYS.getOrDefault(code, "KEY " + code);
    }

    public static String getMouseDisplayName(int code) {
        return MOUSE_BUTTONS.getOrDefault(code, "BTN " + code);
    }

    public static boolean isKnownKeyboardKey(int code) {
        return KEYBOARD_KEYS.containsKey(code);
    }

    public static boolean isKnownMouseButton(int code) {
        return MOUSE_BUTTONS.containsKey(code);
    }
}
