package space.mcplay.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.plugin.spigot.listener.SpigotListener;

public class PlayerQuitListener extends SpigotListener {

    public PlayerQuitListener(SpigotPlugin spigotPlugin) {
        super(spigotPlugin);
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
}
