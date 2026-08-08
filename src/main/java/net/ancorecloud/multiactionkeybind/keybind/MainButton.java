package net.ancorecloud.multiactionkeybind.keybind;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a single Main Button with up to {@value #MAX_ACTIONS} child actions.
 */
public class MainButton {

    public static final int MAX_ACTIONS = 4;

    private String id;
    /** The GLFW key code of the trigger key, or mouse button code. */
    private int triggerKeyCode;
    /** ActionType of the trigger (KEYBOARD or MOUSE). */
    private ActionType triggerType;
    /** Human-readable label for the trigger key (e.g. "R"). */
    private String triggerDisplayName;

    private final List<BoundAction> actions;

    // Overlay position and appearance
    private float posX;
    private float posY;
    private float size;
    private float opacity;
    private boolean enabled;

    public MainButton() {
        this.id = UUID.randomUUID().toString();
        this.actions = new ArrayList<>();
        this.posX = 50.0f;
        this.posY = 50.0f;
        this.size = 48.0f;
        this.opacity = 0.8f;
        this.enabled = true;
        this.triggerKeyCode = -1;
        this.triggerType = ActionType.KEYBOARD;
        this.triggerDisplayName = "?";
    }

    public MainButton(String id) {
        this();
        this.id = id;
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    public boolean canAddAction() {
        return actions.size() < MAX_ACTIONS;
    }

    public boolean addAction(BoundAction action) {
        if (!canAddAction()) return false;
        actions.add(action);
        return true;
    }

    public void removeAction(int index) {
        if (index >= 0 && index < actions.size()) {
            actions.remove(index);
        }
    }

    public void setAction(int index, BoundAction action) {
        if (index >= 0 && index < actions.size()) {
            actions.set(index, action);
        }
    }

    public List<BoundAction> getActions() {
        return actions;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTriggerKeyCode() {
        return triggerKeyCode;
    }

    public void setTriggerKeyCode(int triggerKeyCode) {
        this.triggerKeyCode = triggerKeyCode;
    }

    public ActionType getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(ActionType triggerType) {
        this.triggerType = triggerType;
    }

    public String getTriggerDisplayName() {
        return triggerDisplayName;
    }

    public void setTriggerDisplayName(String triggerDisplayName) {
        this.triggerDisplayName = triggerDisplayName;
    }

    public float getPosX() {
        return posX;
    }

    public void setPosX(float posX) {
        this.posX = posX;
    }

    public float getPosY() {
        return posY;
    }

    public void setPosY(float posY) {
        this.posY = posY;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = Math.max(24.0f, Math.min(120.0f, size));
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = Math.max(0.1f, Math.min(1.0f, opacity));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** Deep copy for editing without mutating the live instance. */
    public MainButton copy() {
        MainButton copy = new MainButton(this.id);
        copy.triggerKeyCode = this.triggerKeyCode;
        copy.triggerType = this.triggerType;
        copy.triggerDisplayName = this.triggerDisplayName;
        copy.posX = this.posX;
        copy.posY = this.posY;
        copy.size = this.size;
        copy.opacity = this.opacity;
        copy.enabled = this.enabled;
        for (BoundAction a : this.actions) {
            copy.actions.add(new BoundAction(a.getActionType(), a.getCode(), a.getDisplayName()));
        }
        return copy;
    }
}
