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

import java.util.ArrayList;
import java.util.List;

public class XPBank extends JavaPlugin implements CommandExecutor, Listener {

    private final String GUI_NAME = "§6§lXP-Bank";

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Erstellt die config.yml falls sie fehlt
        getCommand("xpbank").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("XPBank v1.3 geladen!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        openBankGUI(player);
        return true;
    }

    public void openBankGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 9, GUI_NAME);
        String uuid = player.getUniqueId().toString();
        int savedLevels = getConfig().getInt("bank." + uuid, 0);

        // Hintergrund mit grauen Glasscheiben füllen
        ItemStack placeholder = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < 9; i++) gui.setItem(i, placeholder);

        // Item 1: Einzahlen (Slot 2)
        gui.setItem(2, createItem(Material.EMERALD, "§a§lEinzahlen", "§7Klicke, um 1 Level einzuzahlen"));
        
        // Item 2: Anzeige Guthaben (Slot 4)
        gui.setItem(4, createItem(Material.BOOK, "§6§lDein Guthaben", "§e" + savedLevels + " Level gespeichert"));

        // Item 3: Auszahlen (Slot 6)
        gui.setItem(6, createItem(Material.EXPERIENCE_BOTTLE, "§e§lAuszahlen", "§7Klicke, um 1 Level abzuheben"));

        player.openInventory(gui);
    }

    private ItemStack createItem(Material mat, String name, String lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (!lore.isEmpty()) {
                List<String> list = new ArrayList<>();
                list.add(lore);
                meta.setLore(list);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_NAME)) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        String uuid = player.getUniqueId().toString();
        int currentBankBalance = getConfig().getInt("bank." + uuid, 0);

        // EINZAHLEN
        if (event.getSlot() == 2) {
            if (player.getLevel() >= 1) {
                player.setLevel(player.getLevel() - 1); // Level beim Spieler abziehen
                getConfig().set("bank." + uuid, currentBankBalance + 1); // In Datei speichern
                saveConfig();
                player.sendMessage("§a1 Level eingezahlt!");
                openBankGUI(player); // GUI aktualisieren
            } else {
                player.sendMessage("§cDu hast keine Level zum Einzahlen!");
            }
        }

        // AUSZAHLEN
        if (event.getSlot() == 6) {
            if (currentBankBalance >= 1) {
                getConfig().set("bank." + uuid, currentBankBalance - 1); // Von Bank abziehen
                saveConfig();
                player.setLevel(player.getLevel() + 1); // Spieler geben
                player.sendMessage("§e1 Level ausgezahlt!");
                openBankGUI(player); // GUI aktualisieren
            } else {
                player.sendMessage("§cDeine Bank ist leer!");
            }
        }
    }
}
