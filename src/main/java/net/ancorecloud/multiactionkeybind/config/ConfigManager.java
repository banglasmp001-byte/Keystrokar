package net.ancorecloud.multiactionkeybind.config;

import com.google.gson.*;
import net.ancorecloud.multiactionkeybind.MultiActionKeybindMod;
import net.ancorecloud.multiactionkeybind.keybind.ActionType;
import net.ancorecloud.multiactionkeybind.keybind.BoundAction;
import net.ancorecloud.multiactionkeybind.keybind.MainButton;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages persistence of all {@link MainButton} configurations.
 *
 * <p>Config is stored as JSON at:
 * {@code <gameDir>/config/multiactionkeybind.json}</p>
 */
public class ConfigManager {

    private static final String CONFIG_FILE = "multiactionkeybind.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path configPath;
    private final List<MainButton> buttons = new ArrayList<>();
    private boolean overlayVisible = true;
    private boolean editMode = false;

    public ConfigManager() {
        this.configPath = FabricLoader.getInstance()
                .getConfigDir()
                .resolve(CONFIG_FILE);
    }

    // ── Public API ────────────────────────────────────────────────────────────

    public List<MainButton> getButtons() {
        return buttons;
    }

    public void addButton(MainButton button) {
        buttons.add(button);
    }

    public void removeButton(String id) {
        buttons.removeIf(b -> b.getId().equals(id));
    }

    public void replaceButton(String id, MainButton updated) {
        for (int i = 0; i < buttons.size(); i++) {
            if (buttons.get(i).getId().equals(id)) {
                buttons.set(i, updated);
                return;
            }
        }
    }

    public MainButton getButtonById(String id) {
        return buttons.stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
    }

    public boolean isOverlayVisible() {
        return overlayVisible;
    }

    public void setOverlayVisible(boolean visible) {
        this.overlayVisible = visible;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    // ── Persistence ───────────────────────────────────────────────────────────

    public void load() {
        if (!Files.exists(configPath)) {
            MultiActionKeybindMod.LOGGER.info("No config found, starting fresh.");
            return;
        }
        try (Reader reader = new InputStreamReader(Files.newInputStream(configPath), StandardCharsets.UTF_8)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            buttons.clear();

            overlayVisible = getBoolean(root, "overlayVisible", true);
            editMode = getBoolean(root, "editMode", false);

            JsonArray arr = root.has("buttons") ? root.getAsJsonArray("buttons") : new JsonArray();
            for (JsonElement elem : arr) {
                try {
                    MainButton btn = deserializeButton(elem.getAsJsonObject());
                    if (btn != null) buttons.add(btn);
                } catch (Exception e) {
                    MultiActionKeybindMod.LOGGER.warn("Skipping corrupted button entry: {}", e.getMessage());
                }
            }
            MultiActionKeybindMod.LOGGER.info("Loaded {} buttons from config.", buttons.size());
        } catch (Exception e) {
            MultiActionKeybindMod.LOGGER.error("Failed to load config, resetting: {}", e.getMessage());
            buttons.clear();
        }
    }

    public void save() {
        try {
            Files.createDirectories(configPath.getParent());
            JsonObject root = new JsonObject();
            root.addProperty("overlayVisible", overlayVisible);
            root.addProperty("editMode", editMode);

            JsonArray arr = new JsonArray();
            for (MainButton btn : buttons) {
                arr.add(serializeButton(btn));
            }
            root.add("buttons", arr);

            try (Writer writer = new OutputStreamWriter(Files.newOutputStream(configPath), StandardCharsets.UTF_8)) {
                GSON.toJson(root, writer);
            }
        } catch (Exception e) {
            MultiActionKeybindMod.LOGGER.error("Failed to save config: {}", e.getMessage());
        }
    }

    // ── Serialization helpers ─────────────────────────────────────────────────

    private JsonObject serializeButton(MainButton btn) {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", btn.getId());
        obj.addProperty("triggerKeyCode", btn.getTriggerKeyCode());
        obj.addProperty("triggerType", btn.getTriggerType().getId());
        obj.addProperty("triggerDisplayName", btn.getTriggerDisplayName());
        obj.addProperty("posX", btn.getPosX());
        obj.addProperty("posY", btn.getPosY());
        obj.addProperty("size", btn.getSize());
        obj.addProperty("opacity", btn.getOpacity());
        obj.addProperty("enabled", btn.isEnabled());

        JsonArray actionsArr = new JsonArray();
        for (BoundAction action : btn.getActions()) {
            JsonObject a = new JsonObject();
            a.addProperty("actionType", action.getActionType().getId());
            a.addProperty("code", action.getCode());
            a.addProperty("displayName", action.getDisplayName());
            actionsArr.add(a);
        }
        obj.add("actions", actionsArr);
        return obj;
    }

    private MainButton deserializeButton(JsonObject obj) {
        String id = getString(obj, "id", null);
        MainButton btn = id != null ? new MainButton(id) : new MainButton();

        btn.setTriggerKeyCode(getInt(obj, "triggerKeyCode", -1));
        btn.setTriggerType(ActionType.fromId(getString(obj, "triggerType", "keyboard")));
        btn.setTriggerDisplayName(getString(obj, "triggerDisplayName", "?"));
        btn.setPosX(getFloat(obj, "posX", 50f));
        btn.setPosY(getFloat(obj, "posY", 50f));
        btn.setSize(getFloat(obj, "size", 48f));
        btn.setOpacity(getFloat(obj, "opacity", 0.8f));
        btn.setEnabled(getBoolean(obj, "enabled", true));

        JsonArray actionsArr = obj.has("actions") ? obj.getAsJsonArray("actions") : new JsonArray();
        for (JsonElement elem : actionsArr) {
            try {
                JsonObject a = elem.getAsJsonObject();
                ActionType type = ActionType.fromId(getString(a, "actionType", "keyboard"));
                int code = getInt(a, "code", -1);
                String displayName = getString(a, "displayName", "?");
                if (code >= 0) {
                    btn.addAction(new BoundAction(type, code, displayName));
                }
            } catch (Exception e) {
                MultiActionKeybindMod.LOGGER.warn("Skipping corrupted action: {}", e.getMessage());
            }
        }
        return btn;
    }

    // ── JSON util ─────────────────────────────────────────────────────────────

    private static String getString(JsonObject o, String key, String def) {
        return o.has(key) && !o.get(key).isJsonNull() ? o.get(key).getAsString() : def;
    }

    private static int getInt(JsonObject o, String key, int def) {
        try { return o.has(key) ? o.get(key).getAsInt() : def; } catch (Exception e) { return def; }
    }

    private static float getFloat(JsonObject o, String key, float def) {
        try { return o.has(key) ? o.get(key).getAsFloat() : def; } catch (Exception e) { return def; }
    }

    private static boolean getBoolean(JsonObject o, String key, boolean def) {
        try { return o.has(key) ? o.get(key).getAsBoolean() : def; } catch (Exception e) { return def; }
    }
}
