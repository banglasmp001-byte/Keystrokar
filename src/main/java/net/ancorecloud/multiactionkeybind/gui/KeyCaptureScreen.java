package net.ancorecloud.multiactionkeybind.gui;

import net.ancorecloud.multiactionkeybind.keybind.ActionType;
import net.ancorecloud.multiactionkeybind.keybind.BoundAction;
import net.ancorecloud.multiactionkeybind.keybind.KeyRegistry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

/**
 * A modal screen that waits for the user to press any key or mouse button.
 * On capture it calls the provided {@code onCaptured} callback and closes.
 */
public class KeyCaptureScreen extends Screen {

    private final Screen parent;
    private final Consumer<BoundAction> onCaptured;
    private boolean cancelled = false;

    public KeyCaptureScreen(Screen parent, Consumer<BoundAction> onCaptured) {
        super(Text.translatable("multiactionkeybind.press_key"));
        this.parent = parent;
        this.onCaptured = onCaptured;
    }

    @Override
    protected void init() {
        addDrawableChild(ButtonWidget.builder(
                Text.translatable("multiactionkeybind.cancel"),
                btn -> {
                    cancelled = true;
                    close();
                }
        ).dimensions(width / 2 - 60, height / 2 + 30, 120, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        String title = "Press any key or mouse button...";
        int tw = textRenderer.getWidth(title);
        context.drawTextWithShadow(textRenderer, title, (width - tw) / 2, height / 2 - 10, 0xFFFFFFFF);

        String hint = "Press Cancel or Escape to abort";
        int hw = textRenderer.getWidth(hint);
        context.drawTextWithShadow(textRenderer, hint, (width - hw) / 2, height / 2 + 10, 0xFFAAAAAA);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            cancelled = true;
            close();
            return true;
        }
        // Ignore modifier-only keys by themselves (Shift, Ctrl, Alt, Super)
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT
                || keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL
                || keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT
                || keyCode == GLFW.GLFW_KEY_LEFT_SUPER || keyCode == GLFW.GLFW_KEY_RIGHT_SUPER) {
            // Still capture them as valid action targets (e.g. Shift sprint)
        }

        String name = KeyRegistry.getKeyboardDisplayName(keyCode);
        BoundAction action = new BoundAction(ActionType.KEYBOARD, keyCode, name);
        onCaptured.accept(action);
        close();
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Let Cancel button work
        if (super.mouseClicked(mouseX, mouseY, button)) return true;

        if (!cancelled) {
            String name = KeyRegistry.getMouseDisplayName(button);
            BoundAction action = new BoundAction(ActionType.MOUSE, button, name);
            onCaptured.accept(action);
            close();
        }
        return true;
    }

    @Override
    public void close() {
        assert client != null;
        client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
