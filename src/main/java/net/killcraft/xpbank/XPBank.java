package net.killcraft.xpbank;

import org.bukkit.plugin.java.JavaPlugin;

public class XPBank extends JavaPlugin {

    @Override
    public void onEnable() {
        // Diese Nachricht siehst du beim Starten in der Konsole
        getLogger().info("=======================================");
        getLogger().info("XPBank System fuer KillCraft geladen!");
        getLogger().info("Status: Einsatzbereit (v1.0)");
        getLogger().info("=======================================");
    }

    @Override
    public void onDisable() {
        // Diese Nachricht siehst du beim Stoppen des Servers
        getLogger().info("XPBank wurde erfolgreich deaktiviert.");
    }
}
