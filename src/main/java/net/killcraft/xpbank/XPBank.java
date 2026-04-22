package net.killcraft.xpbank;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
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
        saveDefaultConfig();
        getCommand("xpbank").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("XPBank v1.5.4 mit Multi-Klick Support geladen!");
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

        ItemStack placeholder = createItem(Material.GRAY_STAINED_GLASS_PANE, " ", "");
        for (int i = 0; i < 9; i++) gui.setItem(i, placeholder);

 (Anleitung)
        gui.setItem(2, createItem(Material.EMERALD, "§a§lEinzahlen", 
            "§7Links: §f1 Level", "§7Rechts: §f10 Level", "§7Shift+Links: §fAlle Level"));
        
        gui.setItem(4, createItem(Material.BOOK, "§6§lDein Guthaben", "§e" + savedLevels + " Level "));

        gui.setItem(6, createItem(Material.EXPERIENCE_BOTTLE, "§e§lAuszahlen", 
            "§7Links: §f1 Level", "§7Rechts: §f10 Level", "§7Shift+Links: §fAlle Level"));

        player.openInventory(gui);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals(GUI_NAME)) return;
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        String uuid = player.getUniqueId().toString();
        int currentBank = getConfig().getInt("bank." + uuid, 0);
        ClickType click = event.getClick();

        if (event.getSlot() == 2) {
            int amountToSave = 0;

            if (click == ClickType.LEFT) amountToSave = 1;
            else if (click == ClickType.RIGHT) amountToSave = 10;
            else if (click == ClickType.SHIFT_LEFT) amountToSave = player.getLevel();

            if (amountToSave > 0 && player.getLevel() >= amountToSave) {
                player.setLevel(player.getLevel() - amountToSave);
                getConfig().set("bank." + uuid, currentBank + amountToSave);
                saveConfig();
                player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
                player.sendMessage("§a§l+ " + amountToSave + " Level §7eingezahlt.");
                openBankGUI(player);
            } else {
                player.sendMessage("§cDu hast nicht genug Level!");
            }
        }

        if (event.getSlot() == 6) {
            int amountToGet = 0;

            if (click == ClickType.LEFT) amountToGet = 1;
            else if (click == ClickType.RIGHT) amountToGet = 10;
            else if (click == ClickType.SHIFT_LEFT) amountToGet = currentBank;

            if (amountToGet > 0 && currentBank >= amountToGet) {
                getConfig().set("bank." + uuid, currentBank - amountToGet);
                saveConfig();
                player.setLevel(player.getLevel() + amountToGet);
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
                player.sendMessage("§e§l- " + amountToGet + " Level §7ausgezahlt.");
                openBankGUI(player);
            } else {
                player.sendMessage("§cDeine Bank hat nicht genug Level!");
            }
        }
    }

    private ItemStack createItem(Material mat, String name, String... lores) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> list = new ArrayList<>();
            for (String s : lores) list.add(s);
            meta.setLore(list);
            item.setItemMeta(meta);
        }
        return item;
    }
}
