package net.ancorecloud.multiactionkeybind.keybind;

/**
 * Defines the category of an action binding.
 * Extensible: new categories can be added without breaking existing configs.
 */
public enum ActionType {
    KEYBOARD("keyboard"),
    MOUSE("mouse");

    private final String id;

    ActionType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public static ActionType fromId(String id) {
        for (ActionType type : values()) {
            if (type.id.equalsIgnoreCase(id)) {
                return type;
            }
        }
        return KEYBOARD;
    }
}
