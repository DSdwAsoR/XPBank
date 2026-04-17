package net.killcraft.xpbank;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class XPBank extends JavaPlugin implements CommandExecutor, Listener {

    private final String GUI_NAME = "§6§lXP-Bank";

    @Override
    public void onEnable() {
        getCommand("xpbank").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("XPBank GUI geladen!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        // Die GUI wird erstellt (9 Slots = 1 Reihe)
        Inventory gui = Bukkit.createInventory(null, 9, GUI_NAME);

        // Item 1: Einzahlen (Smaragd)
        gui.setItem(2, createItem(Material.EMERALD, "§a§lEinzahlen", "§7Klicke, um 1 Level einzuzahlen"));
        
        // Item 2: Auszahlen (Glasflasche)
        gui.setItem(6, createItem(Material.EXPERIENCE_BOTTLE, "§e§lAuszahlen", "§7Klicke, um 1 Level abzuheben"));

        player.openInventory(gui);
        return true;
    }

    // Hilfsmethode zum Erstellen von Items
    private ItemStack createItem(Material mat, String name, String lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        java.util.List<String> list = new java.util.ArrayList<>();
        list.add(lore);
        meta.setLore(list);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Prüfen, ob es unsere GUI ist
        if (!event.getView().getTitle().equals(GUI_NAME)) return;
        if (event.getCurrentItem() == null) return;

        event.setCancelled(true); // Verhindert, dass man Items aus der GUI klaut
        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() == 2) { // Einzahlen
            if (player.getLevel() >= 1) {
                player.setLevel(player.getLevel() - 1);
                player.sendMessage("§a1 Level in die Bank eingezahlt! §7(Speicherung folgt im nächsten Step)");
            } else {
                player.sendMessage("§cDu hast nicht genug Level!");
            }
        }

        if (event.getSlot() == 6) { // Auszahlen
            player.setLevel(player.getLevel() + 1);
            player.sendMessage("§e1 Level von der Bank abgehoben!");
        }
    }
}
