package net.ancorecloud.multiactionkeybind.gui;

import net.ancorecloud.multiactionkeybind.config.ConfigManager;
import net.ancorecloud.multiactionkeybind.keybind.BoundAction;
import net.ancorecloud.multiactionkeybind.keybind.MainButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.text.Text;

import java.util.List;

/**
 * Screen for creating or editing a single {@link MainButton}.
 * Shows trigger key, up to 4 action slots, position/size/opacity sliders,
 * enabled toggle, and Save/Cancel.
 */
public class EditButtonScreen extends Screen {

    private static final int SLOT_COUNT = MainButton.MAX_ACTIONS;

    private final Screen parent;
    private final ConfigManager config;
    private final MainButton editing;   // working copy
    private final boolean isNew;

    // Layout constants
    private static final int MARGIN = 20;
    private static final int ROW_H = 24;

    private ButtonWidget triggerBtn;
    private final ButtonWidget[] actionBtns = new ButtonWidget[SLOT_COUNT];
    private final ButtonWidget[] removeActionBtns = new ButtonWidget[SLOT_COUNT];
    private ButtonWidget addActionBtn;
    private ButtonWidget enabledBtn;
    private ButtonWidget saveBtn;

    private float posX, posY, size, opacity;

    public EditButtonScreen(Screen parent, ConfigManager config, MainButton buttonToEdit) {
        super(Text.of(buttonToEdit == null ? "New Button" : "Edit Button"));
        this.parent = parent;
        this.config = config;
        this.isNew = (buttonToEdit == null);
        this.editing = (buttonToEdit != null) ? buttonToEdit.copy() : new MainButton();
        this.posX = editing.getPosX();
        this.posY = editing.getPosY();
        this.size = editing.getSize();
        this.opacity = editing.getOpacity();
    }

    @Override
    protected void init() {
        clearChildren();
        int cx = width / 2;
        int y = 40;

        // ── Trigger key ───────────────────────────────────────────────────────
        triggerBtn = ButtonWidget.builder(
                Text.of("Main Button: [ " + editing.getTriggerDisplayName() + " ]"),
                btn -> openKeyCapture(false, -1)
        ).dimensions(cx - 100, y, 200, 20).build();
        addDrawableChild(triggerBtn);
        y += ROW_H + 8;

        // ── Action slots ──────────────────────────────────────────────────────
        addDrawableChild(makeLabel(cx, y, "Actions (max " + SLOT_COUNT + "):"));
        y += ROW_H;

        List<BoundAction> actions = editing.getActions();
        for (int i = 0; i < SLOT_COUNT; i++) {
            final int idx = i;
            String slotLabel = (i < actions.size())
                    ? actions.get(i).getDisplayName()
                    : "[ EMPTY ]";

            actionBtns[i] = ButtonWidget.builder(
                    Text.of(slotLabel),
                    btn -> openKeyCapture(true, idx)
            ).dimensions(cx - 100, y, 150, 20).build();
            addDrawableChild(actionBtns[i]);

            removeActionBtns[i] = ButtonWidget.builder(
                    Text.of("X"),
                    btn -> {
                        editing.removeAction(idx);
                        init(); // rebuild UI
                    }
            ).dimensions(cx + 55, y, 20, 20).build();
            removeActionBtns[i].active = (i < actions.size());
            addDrawableChild(removeActionBtns[i]);

            y += ROW_H;
        }

        // Add action button
        addActionBtn = ButtonWidget.builder(
                Text.translatable("multiactionkeybind.add_action"),
                btn -> openKeyCapture(true, actions.size())
        ).dimensions(cx - 100, y, 200, 20).build();
        addActionBtn.active = editing.canAddAction();
        addDrawableChild(addActionBtn);
        y += ROW_H + 12;

        // ── Appearance sliders ────────────────────────────────────────────────
        addDrawableChild(makeLabel(cx, y, "Position X: " + (int) posX));
        y += ROW_H;
        addDrawableChild(new FloatSlider(cx - 100, y, 200, 20, "X", posX, 0, width, v -> {
            posX = v;
            editing.setPosX(v);
        }));
        y += ROW_H;

        addDrawableChild(makeLabel(cx, y, "Position Y: " + (int) posY));
        y += ROW_H;
        addDrawableChild(new FloatSlider(cx - 100, y, 200, 20, "Y", posY, 0, height, v -> {
            posY = v;
            editing.setPosY(v);
        }));
        y += ROW_H;

        addDrawableChild(makeLabel(cx, y, "Size: " + (int) size));
        y += ROW_H;
        addDrawableChild(new FloatSlider(cx - 100, y, 200, 20, "Size", size, 24, 120, v -> {
            size = v;
            editing.setSize(v);
        }));
        y += ROW_H;

        addDrawableChild(makeLabel(cx, y, "Opacity: " + String.format("%.0f%%", opacity * 100)));
        y += ROW_H;
        addDrawableChild(new FloatSlider(cx - 100, y, 200, 20, "Opacity", opacity, 0.1f, 1.0f, v -> {
            opacity = v;
            editing.setOpacity(v);
        }));
        y += ROW_H + 4;

        // ── Enabled toggle ────────────────────────────────────────────────────
        enabledBtn = ButtonWidget.builder(
                Text.of(editing.isEnabled() ? "Enabled: ON" : "Enabled: OFF"),
                btn -> {
                    editing.setEnabled(!editing.isEnabled());
                    btn.setMessage(Text.of(editing.isEnabled() ? "Enabled: ON" : "Enabled: OFF"));
                }
        ).dimensions(cx - 60, y, 120, 20).build();
        addDrawableChild(enabledBtn);
        y += ROW_H + 8;

        // ── Save / Cancel ─────────────────────────────────────────────────────
        saveBtn = ButtonWidget.builder(
                Text.translatable("multiactionkeybind.save"),
                btn -> save()
        ).dimensions(cx - 105, y, 100, 20).build();
        addDrawableChild(saveBtn);

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("multiactionkeybind.cancel"),
                btn -> close()
        ).dimensions(cx + 5, y, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Title
        String title = isNew ? "New Button" : "Edit Button";
        int tw = textRenderer.getWidth(title);
        context.drawTextWithShadow(textRenderer, title, (width - tw) / 2, 10, 0xFFFFFFFF);

        // Max-actions warning
        if (!editing.canAddAction()) {
            String warn = "Maximum 4 actions per button";
            int ww = textRenderer.getWidth(warn);
            int wy = addActionBtn.getY();
            context.drawTextWithShadow(textRenderer, warn, width / 2 - ww / 2, wy + 24, 0xFFFF5555);
        }
    }

    private void openKeyCapture(boolean isAction, int slotIndex) {
        client.setScreen(new KeyCaptureScreen(this, captured -> {
            if (isAction) {
                List<BoundAction> actions = editing.getActions();
                if (slotIndex < actions.size()) {
                    editing.setAction(slotIndex, captured);
                } else {
                    editing.addAction(captured);
                }
            } else {
                editing.setTriggerKeyCode(captured.getCode());
                editing.setTriggerType(captured.getActionType());
                editing.setTriggerDisplayName(captured.getDisplayName());
            }
            // Rebuild UI after capture
            init();
        }));
    }

    private void save() {
        if (isNew) {
            config.addButton(editing);
        } else {
            config.replaceButton(editing.getId(), editing);
        }
        config.save();
        close();
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

    // ── Helpers ───────────────────────────────────────────────────────────────

    private net.minecraft.client.gui.widget.TextWidget makeLabel(int cx, int y, String text) {
        return new net.minecraft.client.gui.widget.TextWidget(
                cx - 100, y, 200, 12,
                Text.of(text),
                textRenderer
        );
    }

    // ── Inner slider widget ───────────────────────────────────────────────────

    private static class FloatSlider extends SliderWidget {
        private final float min;
        private final float max;
        private final java.util.function.Consumer<Float> onChange;
        private final String labelPrefix;

        FloatSlider(int x, int y, int width, int height,
                    String label, float current, float min, float max,
                    java.util.function.Consumer<Float> onChange) {
            super(x, y, width, height, Text.of(label + ": " + formatValue(label, current, min, max)),
                    (current - min) / (max - min));
            this.min = min;
            this.max = max;
            this.onChange = onChange;
            this.labelPrefix = label;
        }

        private static String formatValue(String label, float v, float min, float max) {
            if (label.equals("Opacity")) return String.format("%.0f%%", v * 100);
            return String.format("%.0f", v);
        }

        @Override
        protected void updateMessage() {
            float val = min + (float) value * (max - min);
            setMessage(Text.of(labelPrefix + ": " + formatValue(labelPrefix, val, min, max)));
        }

        @Override
        protected void applyValue() {
            float val = min + (float) value * (max - min);
            onChange.accept(val);
        }
    }
}
