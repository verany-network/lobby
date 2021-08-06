package net.verany.lobbysystem.flagwars;

import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.command.AbstractCommand;
import net.verany.api.cuboid.Cuboid;
import net.verany.api.locationmanager.AbstractLocationManager;
import net.verany.api.module.VeranyPlugin;
import net.verany.api.region.GameRegion;
import net.verany.lobbysystem.flagwars.commands.StatsCommand;
import net.verany.lobbysystem.flagwars.map.IMapObject;
import net.verany.lobbysystem.flagwars.map.MapObject;
import net.verany.lobbysystem.flagwars.queue.IQueueObject;
import net.verany.lobbysystem.flagwars.queue.QueueObject;
import net.verany.lobbysystem.flagwars.region.FlagWarsRegion;
import net.verany.lobbysystem.flagwars.round.AbstractRound;
import net.verany.lobbysystem.game.AbstractLobbySystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

@Getter
public class LobbyFlagWars implements IFlagWarsManager {

    public static LobbyFlagWars INSTANCE;

    private final AbstractLobbySystem lobbySystem;
    private final AbstractLocationManager locationManager;
    private final IMapObject mapObject;
    private final IQueueObject queueObject;
    private final Consumer<IFlagWarsManager> initCommands;
    private final List<AbstractRound> preparingRounds = new CopyOnWriteArrayList<>();
    private GameRegion region;

    public LobbyFlagWars(AbstractLobbySystem lobbySystem, Consumer<IFlagWarsManager> initCommands) {
        INSTANCE = this;
        this.lobbySystem = lobbySystem;
        this.locationManager = lobbySystem.getLocationManager();
        this.mapObject = new MapObject(lobbySystem);
        this.queueObject = new QueueObject(lobbySystem);
        this.initCommands = initCommands;
    }

    @Override
    public void onEnable() {
        if (locationManager.existLocation("flagwars_1") && locationManager.existLocation("flagwars_2")) {
            Location first = locationManager.getLocation("flagwars_1");
            Location second = locationManager.getLocation("flagwars_2");
            region = new FlagWarsRegion(new Cuboid(first, second));
            Verany.registerRegion(region);
        }
        initCommands.accept(this);
    }

    @Override
    public String getCurrentSeasonFormatted() {
        return new SimpleDateFormat("dd.MM.yyyy").format(getCurrentSeasonStart());
    }

}
