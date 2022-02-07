package me.mertbhey.MobHunt;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Main extends JavaPlugin {
    int progress = 0;
    int count = 0;
    ArrayList<String> completed = new ArrayList<>();
    Textures tm = new Textures();
    ArrayList<String> mobList = new ArrayList<>();
    BossBar bar = Bukkit.createBossBar("a", BarColor.BLUE, BarStyle.SEGMENTED_6);
    Command cmd;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        bar.setVisible(true);
        cmd = new Command(this);
        
        new BukkitRunnable() {
            @Override
            public void run() {
                bar.setTitle(format("messages.remaining"));
            }
        }.runTaskTimer(this, 0, 20);

        for (String key : getConfig().getKeys(true)) {
            if (key.startsWith("mobs.")) {
                mobList.add(key.replace("mobs.", ""));
                count++;
                if (getConfig().getBoolean(key)) {
                    completed.add(key.replace("mobs.", ""));
                    progress++;
                }
            }
        }

        Bukkit.getPluginManager().registerEvents(new Events(this), this);
        Bukkit.getPluginCommand("hunt").setExecutor(cmd);
    }

    @Override
    public void onDisable() {
        this.bar.setVisible(false);
    }

    // Shitcode below
    public void sendTitle(String u, String mob) {
        if(mob.equals("snowman")) mob = "snow_golem";
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a times 10 80 10");

        String string1 = format("messages.found.title").replaceAll("%user%", u);
        String[] list1 = string1.split("%mob%");
        String cmd1 = "[\""+list1[0]+"\"";
        if(string1.contains("%mob%")) cmd1 += ", {\"translate\": \"entity.minecraft."+mob+"\"}";
        if(list1.length == 2) cmd1 += ", \""+list1[1]+"\"]";
        else cmd1 += "]";

        String string2 = format("messages.found.subtitle").replaceAll("%user%", u);
        String[] list2 = string2.split("%mob%");
        String cmd2 = "[\""+list2[0]+"\"";
        if(string2.contains("%mob%")) cmd2 += ", {\"translate\": \"entity.minecraft."+mob+"\"}";
        if(list2.length == 2) cmd2 += ", \""+list2[1]+"\"]";
        else cmd2 += "]";

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a title " + cmd1);
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title @a subtitle " + cmd2 );
    }

    public String format(String key) {
        return getConfig().getString(key)
            .replaceAll("&", "§")
            .replaceAll("%remaining%", "" + (count - progress))
            .replaceAll("%count%", "" + count)
            .replaceAll("%progress%", "" + progress)
            .replaceAll("%prefix%", getConfig().getString("prefix")
                .replaceAll("&", "§"));
    }

    // https://www.spigotmc.org/threads/cache-player-skull-heads.147544/#post-1571051
    public ItemStack getSkull(String url, String mob, Boolean isFound) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if(url.isEmpty()) return head;
     
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        headMeta.setDisplayName((isFound ? "§a" : "§c") + toTitleCase(mob.toLowerCase(Locale.ENGLISH).replaceAll("_", " ")));
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.getEncoder().encode(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try
        {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        }
        catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }

    // https://stackoverflow.com/a/1086134
    public String toTitleCase(String input) {
        StringBuilder titleCase = new StringBuilder(input.length());
        boolean nextTitleCase = true;
    
        for (char c : input.toCharArray()) {
            if (Character.isSpaceChar(c)) {
                nextTitleCase = true;
            } else if (nextTitleCase) {
                c = Character.toTitleCase(c);
                nextTitleCase = false;
            }
    
            titleCase.append(c);
        }
    
        return titleCase.toString();
    }
}