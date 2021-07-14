package space.mcplay.lobby.listener;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import space.mcplay.lobby.jump.DoubleJumpFactory;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.plugin.spigot.listener.SpigotListener;

public class PlayerDoubleJumpListener extends SpigotListener {

  public PlayerDoubleJumpListener(SpigotPlugin spigotPlugin) {
    super(spigotPlugin);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(PlayerToggleFlightEvent event) {
    final Player player = event.getPlayer();

    switch (player.getGameMode()) {
      case ADVENTURE:
      case SURVIVAL:
        event.setCancelled(true);
        DoubleJumpFactory.executeJump(player);
        break;
      default:
        break;
    }
  }

  @EventHandler
  public void handle(PlayerMoveEvent event) {
    DoubleJumpFactory.chargeJump(event.getPlayer());
  }
}
