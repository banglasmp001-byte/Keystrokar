package net.ancorecloud.multiactionkeybind.gui;

import net.ancorecloud.multiactionkeybind.config.ConfigManager;
import net.ancorecloud.multiactionkeybind.keybind.BoundAction;
import net.ancorecloud.multiactionkeybind.keybind.MainButton;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * The main configuration screen listing all {@link MainButton}s.
 * Accessed via Mod Menu or via the open-config keybind.
 */
public class ConfigScreen extends Screen {

    private static final int ITEM_HEIGHT = 110;
    private static final int HEADER_H = 50;
    private static final int MARGIN = 6;

    private final Screen parent;
    private final ConfigManager config;

    // Scroll state
    private int scrollOffset = 0;
    private int contentHeight = 0;
    private boolean scrollDragging = false;

    // Row widgets (rebuilt on init/scroll)
    private final List<RowWidgets> rowWidgetList = new ArrayList<>();

    public ConfigScreen(Screen parent, ConfigManager config) {
        super(Text.translatable("multiactionkeybind.title"));
        this.parent = parent;
        this.config = config;
    }

    @Override
    protected void init() {
        clearChildren();
        rowWidgetList.clear();

        int cx = width / 2;

        // ── Header buttons ────────────────────────────────────────────────────
        addDrawableChild(ButtonWidget.builder(
                Text.translatable("multiactionkeybind.add_button"),
                btn -> openEdit(null)
        ).dimensions(cx - 100, 10, 200, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("multiactionkeybind.reset_all"),
                btn -> confirmResetAll()
        ).dimensions(cx - 100, 32, 95, 16).build());

        addDrawableChild(ButtonWidget.builder(
                Text.translatable("multiactionkeybind.back"),
                btn -> close()
        ).dimensions(cx + 5, 32, 95, 16).build());

        // ── Per-button rows ───────────────────────────────────────────────────
        List<MainButton> buttons = config.getButtons();
        contentHeight = HEADER_H + buttons.size() * ITEM_HEIGHT + 10;

        for (int i = 0; i < buttons.size(); i++) {
            MainButton btn = buttons.get(i);
            int rowY = HEADER_H + i * ITEM_HEIGHT - scrollOffset;
            RowWidgets row = buildRow(btn, i + 1, rowY);
            rowWidgetList.add(row);
            // addDrawableChild is protected — call it here inside the Screen subclass
            addDrawableChild(row.edit);
            addDrawableChild(row.delete);
            addDrawableChild(row.reset);
            addDrawableChild(row.toggle);
        }
    }

    private RowWidgets buildRow(MainButton btn, int index, int rowY) {
        int cx = width / 2;

        ButtonWidget editBtn = ButtonWidget.builder(
                Text.translatable("multiactionkeybind.edit"),
                b -> openEdit(btn)
        ).dimensions(cx - 100, rowY + 80, 60, 16).build();

        ButtonWidget deleteBtn = ButtonWidget.builder(
                Text.translatable("multiactionkeybind.delete"),
                b -> confirmDelete(btn)
        ).dimensions(cx - 35, rowY + 80, 60, 16).build();

        ButtonWidget resetBtn = ButtonWidget.builder(
                Text.translatable("multiactionkeybind.reset"),
                b -> resetButton(btn)
        ).dimensions(cx + 30, rowY + 80, 60, 16).build();

        ButtonWidget toggleBtn = ButtonWidget.builder(
                Text.of(btn.isEnabled() ? "ON" : "OFF"),
                b -> {
                    btn.setEnabled(!btn.isEnabled());
                    b.setMessage(Text.of(btn.isEnabled() ? "ON" : "OFF"));
                    config.save();
                }
        ).dimensions(cx + 95, rowY + 80, 30, 16).build();

        return new RowWidgets(editBtn, deleteBtn, resetBtn, toggleBtn, btn, index);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        // Title
        String title = "Multi-Action Keybinds";
        int tw = textRenderer.getWidth(title);
        context.drawTextWithShadow(textRenderer, title, (width - tw) / 2, -2 + 2, 0xFFFFFFFF);

        // Render each row's text (widgets draw themselves via super)
        List<MainButton> buttons = config.getButtons();
        for (int i = 0; i < buttons.size(); i++) {
            MainButton btn = buttons.get(i);
            int rowY = HEADER_H + i * ITEM_HEIGHT - scrollOffset;

            if (rowY + ITEM_HEIGHT < 0 || rowY > height) continue; // skip off-screen

            // Row background
            context.fill(MARGIN, rowY, width - MARGIN, rowY + ITEM_HEIGHT - 4, 0x66000000);

            // Bind label
            String bindLabel = "Bind #" + (i + 1) + "  |  Main Button: [ " + btn.getTriggerDisplayName() + " ]";
            context.drawTextWithShadow(textRenderer, bindLabel, MARGIN + 6, rowY + 6, 0xFFFFFF55);

            // Actions
            List<BoundAction> actions = btn.getActions();
            for (int j = 0; j < MainButton.MAX_ACTIONS; j++) {
                String slotText = (j < actions.size()) ? actions.get(j).getDisplayName() : "EMPTY";
                int slotColor = (j < actions.size()) ? 0xFFCCFFCC : 0xFF666666;
                context.drawTextWithShadow(textRenderer,
                        (j + 1) + ". " + slotText,
                        MARGIN + 8, rowY + 22 + j * 12, slotColor);
            }

            // Size / opacity summary
            String summary = String.format("Size: %.0f  Opacity: %.0f%%  Pos: %.0f,%.0f",
                    btn.getSize(), btn.getOpacity() * 100, btn.getPosX(), btn.getPosY());
            context.drawTextWithShadow(textRenderer, summary, MARGIN + 6, rowY + 70, 0xFF888888);
        }

        // Empty-state message
        if (buttons.isEmpty()) {
            String msg = "No buttons configured. Click '+ Add Main Button' to start.";
            int mw = textRenderer.getWidth(msg);
            context.drawTextWithShadow(textRenderer, msg, (width - mw) / 2, height / 2, 0xFF888888);
        }

        // Scroll indicator
        if (contentHeight > height) {
            int trackH = height - HEADER_H;
            int thumbH = Math.max(20, trackH * height / contentHeight);
            int thumbY = HEADER_H + (scrollOffset * (trackH - thumbH)) / Math.max(1, contentHeight - height);
            context.fill(width - 6, HEADER_H, width - 2, height, 0x44FFFFFF);
            context.fill(width - 6, thumbY, width - 2, thumbY + thumbH, 0xAAFFFFFF);
        }
    }

    // ── Scroll handling ───────────────────────────────────────────────────────

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll((int) (-verticalAmount * 16));
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (scrollDragging) {
            scroll((int) deltaY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX > width - 10 && mouseY > HEADER_H) {
            scrollDragging = true;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        scrollDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private void scroll(int delta) {
        int maxScroll = Math.max(0, contentHeight - height);
        scrollOffset = Math.max(0, Math.min(scrollOffset + delta, maxScroll));
        init(); // rebuild widget positions
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    private void openEdit(MainButton btn) {
        assert client != null;
        client.setScreen(new EditButtonScreen(this, config, btn));
    }

    private void confirmDelete(MainButton btn) {
        assert client != null;
        client.setScreen(new ConfirmActionScreen(this,
                "Delete button '" + btn.getTriggerDisplayName() + "'?",
                () -> {
                    config.removeButton(btn.getId());
                    config.save();
                    // reset scroll if needed
                    scrollOffset = Math.max(0, scrollOffset - ITEM_HEIGHT);
                    init();
                }
        ));
    }

    private void confirmResetAll() {
        assert client != null;
        client.setScreen(new ConfirmActionScreen(this,
                "Reset ALL binds? This cannot be undone.",
                () -> {
                    config.getButtons().clear();
                    config.save();
                    scrollOffset = 0;
                    init();
                }
        ));
    }

    private void resetButton(MainButton btn) {
        btn.setPosX(50);
        btn.setPosY(50);
        btn.setSize(48);
        btn.setOpacity(0.8f);
        btn.setEnabled(true);
        config.save();
        init();
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

    // ── Inner helper ──────────────────────────────────────────────────────────

    private static class RowWidgets {
        final ButtonWidget edit, delete, reset, toggle;

        RowWidgets(ButtonWidget edit, ButtonWidget delete, ButtonWidget reset,
                   ButtonWidget toggle, MainButton btn, int index) {
            this.edit = edit;
            this.delete = delete;
            this.reset = reset;
            this.toggle = toggle;
        }
    }
}
