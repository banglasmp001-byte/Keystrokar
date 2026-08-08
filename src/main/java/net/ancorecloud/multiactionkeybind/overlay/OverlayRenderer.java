package net.ancorecloud.multiactionkeybind.overlay;

import net.ancorecloud.multiactionkeybind.MultiActionKeybindMod;
import net.ancorecloud.multiactionkeybind.config.ConfigManager;
import net.ancorecloud.multiactionkeybind.input.ActionExecutor;
import net.ancorecloud.multiactionkeybind.keybind.MainButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Renders the on-screen touch buttons and handles drag/resize in edit mode.
 */
public class OverlayRenderer {

    private final ConfigManager config;

    // Edit mode drag state
    private String draggingId = null;
    private float dragOffsetX = 0;
    private float dragOffsetY = 0;

    // Press state: which button IDs are currently visually pressed
    private final Map<String, Boolean> pressedState = new HashMap<>();

    public OverlayRenderer(ConfigManager config) {
        this.config = config;
    }

    public void toggleVisible() {
        config.setOverlayVisible(!config.isOverlayVisible());
        config.save();
    }

    public void toggleEditMode() {
        config.setEditMode(!config.isEditMode());
        MultiActionKeybindMod.LOGGER.info("Overlay edit mode: {}", config.isEditMode());
    }

    public boolean isEditMode() {
        return config.isEditMode();
    }

    /**
     * Called from {@code HudRenderCallback} every frame while in-game with no screen open.
     */
    public void render(DrawContext context, MinecraftClient client) {
        if (!config.isOverlayVisible()) return;

        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();
        TextRenderer textRenderer = client.textRenderer;

        for (MainButton btn : config.getButtons()) {
            if (!btn.isEnabled() && !config.isEditMode()) continue;
            renderButton(context, textRenderer, btn, screenW, screenH);
        }

        if (config.isEditMode()) {
            // Draw edit-mode indicator
            String label = "[EDIT MODE]";
            int lw = textRenderer.getWidth(label);
            context.drawTextWithShadow(textRenderer, label, screenW - lw - 4, 4, 0xFFFFAA00);
        }
    }

    private void renderButton(DrawContext context, TextRenderer textRenderer,
                               MainButton btn, int screenW, int screenH) {
        float px = btn.getPosX();
        float py = btn.getPosY();
        float size = btn.getSize();
        float opacity = btn.getOpacity();

        int x = (int) px;
        int y = (int) py;
        int s = (int) size;

        boolean pressed = pressedState.getOrDefault(btn.getId(), false);
        boolean editMode = config.isEditMode();

        // Determine colours
        int alpha = (int) (opacity * 255) << 24;
        int bgColor = pressed ? (alpha | 0x4488FF) : (alpha | 0x222222);
        int borderColor = editMode ? (0xFF000000 | 0xFFAA00) : (alpha | 0x888888);
        int textColor = pressed ? 0xFFFFFF00 : 0xFFFFFFFF;
        if (!btn.isEnabled()) {
            bgColor = alpha | 0x111111;
            textColor = 0xFF888888;
        }

        // Background
        context.fill(x, y, x + s, y + s, bgColor);
        // Border
        context.fill(x, y, x + s, y + 1, borderColor);
        context.fill(x, y + s - 1, x + s, y + s, borderColor);
        context.fill(x, y, x + 1, y + s, borderColor);
        context.fill(x + s - 1, y, x + s, y + s, borderColor);

        // Label text (centred)
        String label = btn.getTriggerDisplayName();
        int tw = textRenderer.getWidth(label);
        int tx = x + (s - tw) / 2;
        int ty = y + (s - textRenderer.fontHeight) / 2;
        context.drawTextWithShadow(textRenderer, label, tx, ty, textColor);

        // In edit mode, show position hint
        if (editMode) {
            String pos = (int) px + "," + (int) py;
            int pw = textRenderer.getWidth(pos);
            context.drawTextWithShadow(textRenderer, pos, x + (s - pw) / 2, y + s + 2, 0xFFFFAA00);
        }
    }

    // ── Touch / mouse input handling (called from OverlayInputHandler) ────────

    /**
     * Called when the user taps/clicks a button during normal gameplay.
     * @return true if a button was hit
     */
    public boolean onPress(double mouseX, double mouseY) {
        for (MainButton btn : config.getButtons()) {
            if (!btn.isEnabled()) continue;
            if (hitTest(btn, mouseX, mouseY)) {
                pressedState.put(btn.getId(), true);
                ActionExecutor.pressAll(btn);
                return true;
            }
        }
        return false;
    }

    /**
     * Called when the user releases touch/click.
     */
    public void onRelease(double mouseX, double mouseY) {
        for (MainButton btn : config.getButtons()) {
            if (pressedState.getOrDefault(btn.getId(), false)) {
                pressedState.put(btn.getId(), false);
                ActionExecutor.releaseAll(btn);
            }
        }
    }

    // ── Edit mode drag ────────────────────────────────────────────────────────

    public boolean onEditPress(double mouseX, double mouseY) {
        for (MainButton btn : config.getButtons()) {
            if (hitTest(btn, mouseX, mouseY)) {
                draggingId = btn.getId();
                dragOffsetX = (float) mouseX - btn.getPosX();
                dragOffsetY = (float) mouseY - btn.getPosY();
                return true;
            }
        }
        return false;
    }

    public void onEditDrag(double mouseX, double mouseY, int screenW, int screenH) {
        if (draggingId == null) return;
        MainButton btn = config.getButtonById(draggingId);
        if (btn == null) return;
        float nx = (float) mouseX - dragOffsetX;
        float ny = (float) mouseY - dragOffsetY;
        // Clamp to screen bounds
        nx = MathHelper.clamp(nx, 0, screenW - btn.getSize());
        ny = MathHelper.clamp(ny, 0, screenH - btn.getSize());
        btn.setPosX(nx);
        btn.setPosY(ny);
    }

    public void onEditRelease() {
        if (draggingId != null) {
            config.save();
            draggingId = null;
        }
    }

    public boolean isDragging() {
        return draggingId != null;
    }

    // ── Utility ───────────────────────────────────────────────────────────────

    private boolean hitTest(MainButton btn, double mx, double my) {
        float px = btn.getPosX();
        float py = btn.getPosY();
        float s = btn.getSize();
        return mx >= px && mx <= px + s && my >= py && my <= py + s;
    }
}
