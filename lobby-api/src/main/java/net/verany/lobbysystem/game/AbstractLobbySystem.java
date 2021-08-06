package net.verany.lobbysystem.game;

import net.verany.api.locationmanager.AbstractLocationManager;
import net.verany.api.module.VeranyPlugin;
import net.verany.api.module.VeranyProject;
import org.bukkit.command.PluginCommand;

public abstract class AbstractLobbySystem extends VeranyPlugin {

    public abstract AbstractLocationManager getLocationManager();

}
