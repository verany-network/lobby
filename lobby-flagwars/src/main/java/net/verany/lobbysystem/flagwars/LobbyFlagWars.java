package net.verany.lobbysystem.flagwars;

import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.cuboid.Cuboid;
import net.verany.api.locationmanager.AbstractLocationManager;
import net.verany.lobbysystem.flagwars.region.FlagWarsRegion;
import net.verany.lobbysystem.game.ILobbySystem;
import org.bukkit.Location;

import java.text.SimpleDateFormat;

@Getter
public class LobbyFlagWars implements IFlagWarsManager {

    public static LobbyFlagWars INSTANCE;

    private final ILobbySystem lobbySystem;
    private final AbstractLocationManager locationManager;

    public LobbyFlagWars(ILobbySystem lobbySystem) {
        INSTANCE = this;
        this.lobbySystem = lobbySystem;
        this.locationManager = lobbySystem.getLocationManager();
    }

    @Override
    public void onEnable() {
        if (locationManager.existLocation("flagwars_1") && locationManager.existLocation("flagwars_2")) {
            Location first = locationManager.getLocation("flagwars_1");
            Location second = locationManager.getLocation("flagwars_2");
            Verany.registerRegion(new FlagWarsRegion(new Cuboid(first, second)));
        }
    }

    @Override
    public String getCurrentSeasonFormatted() {
        return new SimpleDateFormat("dd.MM.yyyy").format(getCurrentSeasonStart());
    }
}
