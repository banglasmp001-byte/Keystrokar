package net.ancorecloud.multiactionkeybind.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.ancorecloud.multiactionkeybind.MultiActionKeybindMod;

/**
 * Registers this mod with Mod Menu so the user can open the config
 * screen directly from the Mod Menu interface.
 */
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigScreen(parent, MultiActionKeybindMod.getInstance().getConfigManager());
    }
}
