package net.verany.hubsystem;

import net.verany.api.Verany;
import net.verany.api.config.IngameConfig;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.module.VeranyModule;
import net.verany.api.module.VeranyProject;
import net.verany.hubsystem.events.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@VeranyModule(name = "HubSystem", prefix = "HubSystem", version = "0.1", authors = {"NicoVRNY", "tylix"}, user = "NicoVRNY", host = "159.69.63.105", password = "8Vu0T5MFd9KGTE1t", databases = {"players_hub"})
public class HubSystem extends VeranyProject {

    public static HubSystem INSTANCE;

    public HubSystem() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        Verany.loadModule(this);

        IngameConfig.PLAYER_COLLISION.setValue(false);
        IngameConfig.TAB_LIST_FORMAT.setValue("{0}{1} §8▏ §7"); // 0 ist die Farbe, 1 ist der Rang und zwei ist
        IngameConfig.CHAT_FORMAT.setValue("§8◗§7◗ {0}{1} §8▏ §7{2} §8• §f{3}"); // 0 ist die Farbe, 1 ist der Rang, 2 ist der Name und 3 ist die Nachricht
        // §8◗§7◗ §b§lVerany§3§lCloud §8▏ §7

        init();
    }

    @Override
    public void init() {
        super.init();
        registerCommands();
        registerEvents();
    }

    private void registerCommands() {


    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new QuitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new InteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new DamageEvents(), this);
        Bukkit.getPluginManager().registerEvents(new WorldEvents(), this);
    }


}