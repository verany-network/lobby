package net.verany.hubsystem;

import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.config.IngameConfig;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.module.VeranyModule;
import net.verany.api.module.VeranyProject;
import net.verany.hubsystem.commands.BuildServerCommand;
import net.verany.hubsystem.commands.SetupCommand;
import net.verany.hubsystem.commands.ToggleRankCommand;
import net.verany.hubsystem.events.*;
import net.verany.hubsystem.utils.bees.BeeTimeTask;
import net.verany.hubsystem.utils.location.LocationManager;
import net.verany.hubsystem.utils.player.LevelTask;
import net.verany.hubsystem.utils.scoreboard.ScoreboardTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Bee;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

@Getter
@VeranyModule(name = "HubSystem", prefix = "HubSystem", version = "1.1", authors = {"NicoVRNY", "tylix"}, user = "NicoVRNY", host = "159.69.63.105", password = "", databases = {"hubsystem"})
public class HubSystem extends VeranyProject {

    public static HubSystem INSTANCE;

    private LocationManager locationManager;

    public HubSystem() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        Verany.loadModule(this);

        IngameConfig.PLAYER_COLLISION.setValue(false);
        IngameConfig.TAB_LIST.setValue(true);
        IngameConfig.TAB_LIST_FORMAT.setValue("{0}{1} §8▏ §7"); // 0 ist die Farbe, 1 ist der Rang und zwei ist
        IngameConfig.TAB_LIST_CLAN.setValue(true);
        IngameConfig.CHAT_FORMAT.setValue(" §8◗§7◗ {0}{1} §8▏ §7{2} §8• §f{3}"); // 0 ist die Farbe, 1 ist der Rang, 2 ist der Name und 3 ist die Nachricht
        IngameConfig.AFK_TIME.setValue(TimeUnit.MINUTES.toMillis(10));
        IngameConfig.AFK.setValue(true);
        // §8◗§7◗ §b§lVerany§3§lCloud §8▏ §7

        init();

    }

    @Override
    public void init() {
        super.init();
        registerCommands();
        registerEvents();

        locationManager = new LocationManager(this);

        Bukkit.getScheduler().runTaskTimer(this, new BeeTimeTask(), 0, 15);

        Verany.addTask(new LevelTask(10 * 1000), new ScoreboardTask(1000), new ScoreboardTask.ScoreboardDisplayNameTask(100));
    }

    @Override
    public void onDisable() {
        locationManager.save();
    }

    private void registerCommands() {
        getCommand("setup").setExecutor(new SetupCommand(this));
        getCommand("togglerank").setExecutor(new ToggleRankCommand(this));
        getCommand("build").setExecutor(new BuildServerCommand(this));
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
