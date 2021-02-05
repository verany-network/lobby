package net.verany.hubsystem;

import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.config.IngameConfig;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.locationmanager.AbstractLocationManager;
import net.verany.api.locationmanager.LocationManager;
import net.verany.api.module.VeranyModule;
import net.verany.api.module.VeranyProject;
import net.verany.api.setting.Settings;
import net.verany.api.settings.AbstractSetting;
import net.verany.api.skull.SkullBuilder;
import net.verany.hubsystem.commands.BuildServerCommand;
import net.verany.hubsystem.commands.SetupCommand;
import net.verany.hubsystem.commands.ToggleRankCommand;
import net.verany.hubsystem.events.*;
import net.verany.hubsystem.utils.bees.BeeTimeTask;
import net.verany.hubsystem.utils.bees.OrbTask;
import net.verany.hubsystem.utils.player.LevelTask;
import net.verany.hubsystem.utils.scoreboard.ScoreboardTask;
import net.verany.hubsystem.utils.settings.HubSetting;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Bee;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.TimeUnit;

@Getter
@VeranyModule(name = "HubSystem", prefix = "HubSystem", version = "1.1", authors = {"NicoVRNY", "tylix"}, user = "NicoVRNY", host = "159.69.63.105", password = "", databases = {"hubsystem"})
public class HubSystem extends VeranyProject {

    public static HubSystem INSTANCE;

    private AbstractLocationManager locationManager;

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

        locationManager = new LocationManager(this, "locations", "hubsystem");

        spawnArmorStands();

        Bukkit.getScheduler().runTaskTimer(this, new BeeTimeTask(), 0, 15);
        Bukkit.getScheduler().runTaskTimer(this, new OrbTask(), 0, 5);

        for (AbstractSetting<?> value : Settings.VALUES)
            if (!value.getCategory().equals("hubsystem"))
                HubSetting.toHubSetting(value).getKey();

        Verany.addTask(new LevelTask(10 * 1000), new ScoreboardTask(1000), new ScoreboardTask.ScoreboardDisplayNameTask(100));
    }

    private void spawnArmorStands() {
        if (locationManager.existLocation("elytra_start")) {
            Location location = locationManager.getLocation("elytra_start");

            location.setPitch(0);
            ArmorStand itemArmorStand = location.getWorld().spawn(location.clone().subtract(0, 1.2, 0), ArmorStand.class);
            itemArmorStand.setMetadata("elytra_start", new FixedMetadataValue(this, true));
            itemArmorStand.getEquipment().setHelmet(Items.RED_ORB.clone());
            itemArmorStand.setVisible(false);
            itemArmorStand.setGravity(false);
        }
    }

    @Override
    public void onDisable() {
        locationManager.save();
    }

    private void registerCommands() {
        getCommand("setup").setExecutor(new SetupCommand());
        getCommand("togglerank").setExecutor(new ToggleRankCommand());
        getCommand("build").setExecutor(new BuildServerCommand());
    }

    private void registerEvents() {
        Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new QuitEvent(), this);
        Bukkit.getPluginManager().registerEvents(new ClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new InteractEvent(), this);
        Bukkit.getPluginManager().registerEvents(new DamageEvents(), this);
        Bukkit.getPluginManager().registerEvents(new WorldEvents(), this);
    }


    public static class Items {
        public static ItemStack RED_ORB =new SkullBuilder("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVkNTNlZjQyOGIzNjlmZDVjY2U5NGNlMjA1ZDBkMmQ3YjA5NWZhZDY3NmE5YjM4Mzk3MWVlMTA0OWUzNjdhZCJ9fX0=").build();
    }

}
