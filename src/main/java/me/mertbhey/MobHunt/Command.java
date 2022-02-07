package me.mertbhey.MobHunt;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class Command implements CommandExecutor {
    Main main;
    HashMap<Inventory, Integer> inventoryPageMap = new HashMap<>();

    public Command(Main m) {
        main = m;
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (command.getLabel().equalsIgnoreCase("hunt")) {
            Player p = (Player) sender;
            main.bar.addPlayer(p);
            Inventory inv = this.createInventory(main.format("messages.guiTitle"), p);
            this.fillMobs(inv, 0);
            inventoryPageMap.put(inv, 0);
            p.openInventory(inv);
            return true;
        }
        return false;
    }

    public Inventory createInventory(String title, Player p) {
        Inventory inv = Bukkit.createInventory(null, 54, title);
        for(int i = 0; i < 54; i++) {
            if(i < 9 || i > 45) inv.setItem(i, getNamedItem(Material.GRAY_STAINED_GLASS_PANE, " "));
            if((i % 9) == 0) {
                inv.setItem(i, getNamedItem(Material.GRAY_STAINED_GLASS_PANE, " "));
                inv.setItem(i+8, getNamedItem(Material.GRAY_STAINED_GLASS_PANE, " "));
            }
        }

        inv.setItem(4, createSkull(p));
        inv.setItem(53, getNamedItem(Material.ARROW, main.format("messages.nextPage")));
        return inv;
    }

    public Inventory createInventory(String title, Player p, int page) {
        Inventory i = createInventory(title, p);
        if(page > 0) {
            i.setItem(45, getNamedItem(Material.ARROW, main.format("messages.prevPage")));
        }
        if(page > 1) {
            i.setItem(53, getNamedItem(Material.GRAY_STAINED_GLASS_PANE, " "));
        }

        return i;
    }

    public void fillMobs(Inventory inv, int page) {
        for( int i = (page*28); i < ((page*28)+28); i++ ) {
            if((main.mobList.size()-1) >= i) 
                inv.addItem(main.getSkull(main.tm.map.get(EntityType.valueOf(main.mobList.get(i))), main.mobList.get(i), main.completed.contains(main.mobList.get(i))));
        }
    }

    private ItemStack createSkull(Player p) {
        ItemStack i = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta m = (SkullMeta) i.getItemMeta();
        m.setOwningPlayer(Bukkit.getOfflinePlayer(p.getUniqueId()));
        m.setDisplayName(main.format("messages.head.name").replaceAll("%player%", p.getName()));
        String[] str = main.format("messages.head.lore")
            .replaceAll("%player%", p.getName())
            .split("\n");
        m.setLore(Arrays.asList(str));
        i.setItemMeta(m);

        return i;
    }

    private ItemStack getNamedItem(Material m, String name) {
        ItemStack i = new ItemStack(m);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(name);
        i.setItemMeta(meta);

        return i;
    }

}