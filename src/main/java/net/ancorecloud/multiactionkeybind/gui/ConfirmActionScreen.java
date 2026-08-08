package net.ancorecloud.multiactionkeybind.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

/**
 * Simple yes/no confirmation dialog.
 */
public class ConfirmActionScreen extends Screen {

    private final Screen parent;
    private final String message;
    private final Runnable onConfirm;

    public ConfirmActionScreen(Screen parent, String message, Runnable onConfirm) {
        super(Text.of("Confirm"));
        this.parent = parent;
        this.message = message;
        this.onConfirm = onConfirm;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int cy = height / 2;

        addDrawableChild(ButtonWidget.builder(
                Text.of("Yes"),
                btn -> {
                    onConfirm.run();
                    close();
                }
        ).dimensions(cx - 105, cy + 10, 100, 20).build());

        addDrawableChild(ButtonWidget.builder(
                Text.of("No"),
                btn -> close()
        ).dimensions(cx + 5, cy + 10, 100, 20).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        int cx = width / 2;
        int cy = height / 2;

        // Dialog box background
        context.fill(cx - 120, cy - 30, cx + 120, cy + 40, 0xDD000000);
        context.fill(cx - 120, cy - 30, cx + 120, cy - 29, 0xFFAAAAAA);
        context.fill(cx - 120, cy + 39, cx + 120, cy + 40, 0xFFAAAAAA);
        context.fill(cx - 120, cy - 30, cx - 119, cy + 40, 0xFFAAAAAA);
        context.fill(cx + 119, cy - 30, cx + 120, cy + 40, 0xFFAAAAAA);

        // Message (word-wrap if needed)
        int mw = textRenderer.getWidth(message);
        if (mw > 220) {
            // Simple two-line split
            String[] words = message.split(" ");
            StringBuilder line = new StringBuilder();
            int lineY = cy - 20;
            for (String w : words) {
                String test = line.isEmpty() ? w : line + " " + w;
                if (textRenderer.getWidth(test) > 220) {
                    int lw = textRenderer.getWidth(line.toString());
                    context.drawTextWithShadow(textRenderer, line.toString(), cx - lw / 2, lineY, 0xFFFFFFFF);
                    line = new StringBuilder(w);
                    lineY += 12;
                } else {
                    line = new StringBuilder(test);
                }
            }
            if (!line.isEmpty()) {
                int lw = textRenderer.getWidth(line.toString());
                context.drawTextWithShadow(textRenderer, line.toString(), cx - lw / 2, lineY, 0xFFFFFFFF);
            }
        } else {
            context.drawTextWithShadow(textRenderer, message, cx - mw / 2, cy - 20, 0xFFFFFFFF);
        }
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
