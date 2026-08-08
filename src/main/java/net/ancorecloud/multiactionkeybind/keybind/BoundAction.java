package net.ancorecloud.multiactionkeybind.keybind;

/**
 * Represents one child action within a MainButton.
 * Can be a keyboard key or a mouse button.
 */
public class BoundAction {

    private ActionType actionType;
    /**
     * For KEYBOARD: the GLFW key code (e.g. GLFW_KEY_W).
     * For MOUSE: the GLFW mouse button (e.g. GLFW_MOUSE_BUTTON_LEFT).
     */
    private int code;
    /** Human-readable display label (e.g. "W", "LEFT CLICK", "SPACE"). */
    private String displayName;

    public BoundAction(ActionType actionType, int code, String displayName) {
        this.actionType = actionType;
        this.code = code;
        this.displayName = displayName;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName + " (" + actionType.getId() + ":" + code + ")";
    }
}
