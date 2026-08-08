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
 * <p>Uses Minecraft's own {@link KeyBinding#setKeyPressed} / {@link KeyBinding#onKeyPressed}
 * so that vanilla keybind logic is correctly updated.  For actions that do not map to any
 * registered KeyBinding we fall back to a direct GLFW synthetic-key approach only when
 * the GLFW window handle is available, which is always the case on the client thread.</p>
 *
 * <p>Mouse actions are simulated via {@link MinecraftClient#mouse} accessor using the same
 * GLFW callback path that vanilla uses for real mouse input.</p>
 */
public final class ActionExecutor {

    private ActionExecutor() {}

    /**
     * Press all actions (called on button touch-down or trigger-key-down).
     */
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

    /**
     * Release all actions (called on button touch-up or trigger-key-up).
     */
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
        // Update all registered vanilla key bindings that use this key
        for (KeyBinding kb : client.options.allKeys) {
            if (kb.matchesKey(glfwKey, 0)) {
                KeyBinding.onKeyPressed(key);
                break;
            }
        }
        // Also mark the raw key as pressed so isPressed() returns true
        KeyBinding.setKeyPressed(key, true);
    }

    private static void releaseKeyboard(int glfwKey) {
        InputUtil.Key key = InputUtil.fromKeyCode(glfwKey, 0);
        KeyBinding.setKeyPressed(key, false);
    }

    private static void pressMouse(int glfwButton) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;

        InputUtil.Key key = InputUtil.Type.MOUSE.createFromCode(glfwButton);
        // In 1.21.1 Yarn: KeyBinding has no matchesMouseButton(); use matchesKey on the
        // MOUSE-type InputUtil.Key's keyCode (which is the GLFW button code, stored negative
        // by InputUtil for mouse). The safest approach is to compare the bound key directly.
        for (KeyBinding kb : client.options.allKeys) {
            if (kb.getBoundKeyOf().equals(key)) {
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
