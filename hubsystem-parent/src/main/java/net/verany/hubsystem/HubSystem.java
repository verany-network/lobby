package net.verany.hubsystem;

import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.config.IngameConfig;
import net.verany.api.locationmanager.AbstractLocationManager;
import net.verany.api.locationmanager.LocationManager;
import net.verany.api.module.VeranyModule;
import net.verany.api.module.VeranyProject;
import net.verany.hubsystem.commands.BuildCommand;
import net.verany.hubsystem.commands.SetupCommand;
import net.verany.hubsystem.game.HubManager;
import net.verany.hubsystem.game.IHubManager;
import net.verany.hubsystem.game.actionbar.ActionbarTask;
import net.verany.hubsystem.game.bossbar.BossBarTask;
import net.verany.hubsystem.game.inventory.task.InventoryTask;
import net.verany.hubsystem.game.level.LevelTask;
import net.verany.hubsystem.game.orb.OrbTask;
import net.verany.hubsystem.game.scoreboard.ScoreboardTask;
import net.verany.hubsystem.listener.PlayerJoinListener;
import net.verany.hubsystem.listener.PlayerQuitListener;
import net.verany.hubsystem.listener.ProtectionListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Boss;

import java.util.concurrent.TimeUnit;

@Getter
@VeranyModule(
        name = "HubSystem",
        prefix = "HubSystem",
        version = "1.1",
        authors = {"tylix"},
        user = "NicoVRNY",
        host = "159.69.63.105",
        password = "8Vu0T5MFd9KGTE1t",
        databases = {"hubsystem"}
)
public class HubSystem extends VeranyProject {

    public static HubSystem INSTANCE;

    private AbstractLocationManager locationManager;
    private IHubManager hubManager;

    public int playersInSupport = 0;

    public HubSystem() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        Verany.loadModule(this);

        init();
    }

    @Override
    public void onDisable() {
        Verany.shutdown(this);
    }

    @Override
    public void init() {
        super.init();

        initListener();
        initCommands();

        locationManager = new LocationManager(this, "locations2", "hubsystem");
        hubManager = new HubManager();
        Bukkit.getScheduler().runTaskLater(this, () -> hubManager.registerArmorStands(), 20);

        Verany.addTask(new ActionbarTask(750), new InventoryTask(1000), new OrbTask(50),new LevelTask(10 * 1000), new BossBarTask(50), new ScoreboardTask(1000), new ScoreboardTask.ScoreboardDisplayNameTask(150));

        IngameConfig.PLAYER_COLLISION.setValue(false);
        IngameConfig.TAB_LIST.setValue(true);
        IngameConfig.TAB_LIST_FORMAT.setValue("{0}{1} §8▏ §7");
        IngameConfig.TAB_LIST_CLAN.setValue(true);
        IngameConfig.CHAT_FORMAT.setValue(" §8◗§7◗ {0}{1} §8▏ §7{2} §8• §f{3}");
        IngameConfig.AFK_TIME.setValue(TimeUnit.MINUTES.toMillis(10));
        IngameConfig.AFK.setValue(true);
        IngameConfig.COLORED_CHAT.setValue(true);
    }

    private void initCommands() {
        new BuildCommand(this);
        new SetupCommand(this);
    }

    private void initListener() {
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new ProtectionListener(this);
    }
}
