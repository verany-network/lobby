package space.mcplay.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import space.mcplay.lobby.LobbyPlugin;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.plugin.spigot.listener.SpigotListener;

public class PlayerMoveListener extends SpigotListener {

  public PlayerMoveListener(SpigotPlugin spigotPlugin) {
    super(spigotPlugin);
  }

  @EventHandler
  public void handle(PlayerMoveEvent event) {
    if (event.getTo().getY() < 0.0)
      event.getPlayer().teleport(LobbyPlugin.getInstance().getLocationPool().get("spawn"));
  }
}
