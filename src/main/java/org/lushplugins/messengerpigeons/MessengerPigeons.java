package org.lushplugins.messengerpigeons;

import org.bukkit.plugin.java.JavaPlugin;

public final class MessengerPigeons extends JavaPlugin {
    private static MessengerPigeons plugin;

    @Override
    public void onLoad() {
        plugin = this;
    }

    @Override
    public void onEnable() {
        // Enable implementation
    }

    @Override
    public void onDisable() {
        // Disable implementation
    }

    public static MessengerPigeons getInstance() {
        return plugin;
    }
}
