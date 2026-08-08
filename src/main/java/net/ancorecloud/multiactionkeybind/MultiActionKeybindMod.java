package net.ancorecloud.multiactionkeybind;

import net.ancorecloud.multiactionkeybind.config.ConfigManager;
import net.ancorecloud.multiactionkeybind.gui.ConfigScreen;
import net.ancorecloud.multiactionkeybind.overlay.OverlayRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class MultiActionKeybindMod implements ClientModInitializer {

    public static final String MOD_ID = "multiactionkeybind";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static MultiActionKeybindMod instance;

    private ConfigManager configManager;
    private OverlayRenderer overlayRenderer;

    private KeyBinding toggleOverlayKey;
    private KeyBinding openConfigKey;
    private KeyBinding editModeKey;

    @Override
    public void onInitializeClient() {
        instance = this;
        LOGGER.info("Multi-Action Keybind initializing...");

        // Initialize config
        configManager = new ConfigManager();
        configManager.load();

        // Initialize overlay renderer
        overlayRenderer = new OverlayRenderer(configManager);

        // Register mod keybindings
        toggleOverlayKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.multiactionkeybind.toggle_overlay",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_0,
                "category.multiactionkeybind"
        ));

        openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.multiactionkeybind.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_ENTER,
                "category.multiactionkeybind"
        ));

        editModeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.multiactionkeybind.edit_mode",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_DECIMAL,
                "category.multiactionkeybind"
        ));

        // Register HUD renderer
        // In Fabric API for MC 1.21.1, HudRenderCallback provides (DrawContext, RenderTickCounter)
        HudRenderCallback.EVENT.register((drawContext, renderTickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && client.currentScreen == null) {
                overlayRenderer.render(drawContext, client);
            }
        });

        // Tick events for keybinding checks
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (toggleOverlayKey.wasPressed()) {
                overlayRenderer.toggleVisible();
            }
            if (openConfigKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new ConfigScreen(null, configManager));
                }
            }
            if (editModeKey.wasPressed()) {
                overlayRenderer.toggleEditMode();
            }
        });

        LOGGER.info("Multi-Action Keybind initialized successfully.");
    }

    public static MultiActionKeybindMod getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public OverlayRenderer getOverlayRenderer() {
        return overlayRenderer;
    }
}
