package net.ancorecloud.multiactionkeybind.input;

import net.ancorecloud.multiactionkeybind.MultiActionKeybindMod;
import net.ancorecloud.multiactionkeybind.keybind.ActionType;
import net.ancorecloud.multiactionkeybind.keybind.BoundAction;
import net.ancorecloud.multiactionkeybind.keybind.MainButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import java.util.List;

/**
 * Executes all child actions for a given MainButton.
 *
 * Uses Minecraft's own KeyBinding.setKeyPressed / KeyBinding.onKeyPressed
 * so that vanilla keybind logic is correctly updated.
 */
public final class ActionExecutor {

    private ActionExecutor() {}

    /** Press all actions (called on button touch-down). */
    public static void pressAll(MainButton button) {
        if (button == null || !button.isEnabled()) return;
        List<BoundAction> actions = button.getActions();
        for (BoundAction action : actions) {
            try {
                press(action);
            } catch (Exception e) {
                MultiActionKeybindMod.LOGGER.warn(
                        "Failed to press action {} for button {}: {}",
                        action.getDisplayName(), button.getTriggerDisplayName(), e.getMessage());
            }
        }
    }

    /** Release all actions (called on button touch-up). */
    public static void releaseAll(MainButton button) {
        if (button == null || !button.isEnabled()) return;
        List<BoundAction> actions = button.getActions();
        for (BoundAction action : actions) {
            try {
                release(action);
            } catch (Exception e) {
                MultiActionKeybindMod.LOGGER.warn(
                        "Failed to release action {} for button {}: {}",
                        action.getDisplayName(), button.getTriggerDisplayName(), e.getMessage());
            }
        }
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private static void press(BoundAction action) {
        if (action.getActionType() == ActionType.KEYBOARD) {
            pressKeyboard(action.getCode());
        } else {
            pressMouse(action.getCode());
        }
    }

    private static void release(BoundAction action) {
        if (action.getActionType() == ActionType.KEYBOARD) {
            releaseKeyboard(action.getCode());
        } else {
            releaseMouse(action.getCode());
        }
    }

    private static void pressKeyboard(int glfwKey) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        InputUtil.Key key = InputUtil.fromKeyCode(glfwKey, 0);
        for (KeyBinding kb : client.options.allKeys) {
            if (kb.matchesKey(glfwKey, 0)) {
                KeyBinding.onKeyPressed(key);
                break;
            }
        }
        KeyBinding.setKeyPressed(key, true);
    }

    private static void releaseKeyboard(int glfwKey) {
        InputUtil.Key key = InputUtil.fromKeyCode(glfwKey, 0);
        KeyBinding.setKeyPressed(key, false);
    }

    private static void pressMouse(int glfwButton) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        // Build the InputUtil.Key for this mouse button.
        // InputUtil.Type.MOUSE.createFromCode(n) creates a key with
        // translationKey "key.mouse.<n+1>" — we compare by translation key
        // to find matching vanilla KeyBindings without needing getBoundKeyOf().
        InputUtil.Key key = InputUtil.Type.MOUSE.createFromCode(glfwButton);
        String translationKey = key.getTranslationKey();

        for (KeyBinding kb : client.options.allKeys) {
            // getBoundKeyTranslationKey() returns e.g. "key.mouse.0" for left click
            if (translationKey.equals(kb.getBoundKeyTranslationKey())) {
                KeyBinding.onKeyPressed(key);
                break;
            }
        }
        KeyBinding.setKeyPressed(key, true);
    }

    private static void releaseMouse(int glfwButton) {
        InputUtil.Key key = InputUtil.Type.MOUSE.createFromCode(glfwButton);
        KeyBinding.setKeyPressed(key, false);
    }
}
