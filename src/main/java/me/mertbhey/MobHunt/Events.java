package me.mertbhey.MobHunt;

import java.util.Locale;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class Events implements Listener {
    Main main;
    public Events(Main m) {
        main = m;
    }

    @EventHandler
    public void Welcome(PlayerJoinEvent e) {
        main.bar.addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onEntityDie(EntityDamageByEntityEvent e) {
        if(!e.getDamager().getType().name().equals("PLAYER")) return;
        Player p = (Player) e.getDamager();
        String entityType = e.getEntityType().name();
        if(!main.getConfig().getKeys(true).contains("mobs."+entityType)) return;
        if(main.getConfig().getBoolean("mobs."+entityType)) return;
        new BukkitRunnable() {
            @Override
            public void run() {
                if(e.getEntity().isDead()) {
                    main.getConfig().set("mobs."+entityType, true);
                    main.saveConfig();
                    main.progress++;
                    main.completed.add(entityType);
                    main.sendTitle(p.getName(), entityType.toLowerCase(Locale.ENGLISH));
                }
            }
        }.runTaskLater(main, 20*1);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(!e.getView().getTitle().equals(main.format("messages.guiTitle"))) return;
        e.setCancelled(true);
        Player pl = ((Player) e.getWhoClicked());

        if(e.getCurrentItem().getItemMeta().getDisplayName().equals(main.format("messages.nextPage"))) {
            int page = main.cmd.inventoryPageMap.get(e.getInventory());
            main.cmd.inventoryPageMap.remove(e.getInventory());
            Inventory inv = main.cmd.createInventory(e.getView().getTitle(), pl, page+1);
            pl.closeInventory();
            main.cmd.inventoryPageMap.put(inv, page+1);
            main.cmd.fillMobs(inv, page+1);
            pl.openInventory(inv);
        }
        if(e.getCurrentItem().getItemMeta().getDisplayName().equals(main.format("messages.prevPage"))) {
            int page = main.cmd.inventoryPageMap.get(e.getInventory());
            main.cmd.inventoryPageMap.remove(e.getInventory());
            Inventory inv = main.cmd.createInventory(e.getView().getTitle(), pl, page-1);
            pl.closeInventory();
            main.cmd.inventoryPageMap.put(inv, page-1);
            main.cmd.fillMobs(inv, page-1);
            pl.openInventory(inv);
        }
    }
}
