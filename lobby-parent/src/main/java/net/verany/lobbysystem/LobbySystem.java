package net.verany.lobbysystem;

import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.config.IngameConfig;
import net.verany.api.cuboid.Cuboid;
import net.verany.api.location.LocationManager;
import net.verany.api.locationmanager.AbstractLocationManager;
import net.verany.api.module.VeranyModule;
import net.verany.api.module.VeranyPlugin;
import net.verany.lobbysystem.commands.BuildCommand;
import net.verany.lobbysystem.commands.SetupCommand;
import net.verany.lobbysystem.flagwars.LobbyFlagWars;
import net.verany.lobbysystem.flagwars.commands.StatsCommand;
import net.verany.lobbysystem.flagwars.region.FlagWarsRegion;
import net.verany.lobbysystem.game.AbstractLobbySystem;
import net.verany.lobbysystem.game.HubManager;
import net.verany.lobbysystem.game.IHubManager;
import net.verany.lobbysystem.game.actionbar.ActionbarTask;
import net.verany.lobbysystem.game.bossbar.BossBarTask;
import net.verany.lobbysystem.game.inventory.task.InventoryTask;
import net.verany.lobbysystem.game.level.LevelTask;
import net.verany.lobbysystem.game.orb.OrbTask;
import net.verany.lobbysystem.game.scoreboard.ScoreboardTask;
import net.verany.lobbysystem.game.time.DaytimeTask;
import net.verany.lobbysystem.listener.PlayerJoinListener;
import net.verany.lobbysystem.listener.PlayerQuitListener;
import net.verany.lobbysystem.listener.ProtectionListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.concurrent.TimeUnit;

@Getter
@VeranyModule(
        name = "LobbySystem",
        prefix = "LobbySystem",
        version = "2021.7.1",
        authors = {"tylix"}
)
public class LobbySystem extends AbstractLobbySystem {

    public static LobbySystem INSTANCE;

    private AbstractLocationManager locationManager;
    private IHubManager hubManager;

    public int playersInSupport = 0;

    public LobbySystem() {
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        Verany.loadModule(this, this::init);
    }

    @Override
    public void onDisable() {
        Verany.shutdown(this);
    }

    @Override
    public void init() {

        initListener();
        initCommands();

        locationManager = new LocationManager(this, "locations", "lobbysystem");
        hubManager = new HubManager();
        Bukkit.getScheduler().runTaskLater(this, () -> hubManager.registerArmorStands(), 20);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new DaytimeTask().buildTask(this), 10, 15);
        Verany.addTask(new ActionbarTask(750), new ScoreboardTask.ScoreboardSideNameTask(10 * 1000), new InventoryTask(1000), new OrbTask(50), new LevelTask(10 * 1000), new BossBarTask(50), new ScoreboardTask(1000), new ScoreboardTask.ScoreboardDisplayNameTask(150));

        LobbyFlagWars lobbyFlagWars = new LobbyFlagWars(this, iFlagWarsManager -> new StatsCommand(this, iFlagWarsManager.getRegion()));
        lobbyFlagWars.onEnable();

        IngameConfig.PLAYER_COLLISION.setValue(false);
        IngameConfig.TAB_LIST.setValue(true);
        IngameConfig.TAB_LIST_FORMAT.setValue("{0}{1} §8▏ §7");
        IngameConfig.TAB_LIST_CLAN.setValue(true);
        IngameConfig.CHAT.setValue(true);
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
