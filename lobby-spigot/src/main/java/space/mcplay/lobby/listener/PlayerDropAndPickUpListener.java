package space.mcplay.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDropItemEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.plugin.spigot.listener.SpigotListener;

public class PlayerDropAndPickUpListener extends SpigotListener {

  public PlayerDropAndPickUpListener(SpigotPlugin spigotPlugin) {
    super(spigotPlugin);
  }

  @EventHandler
  public void handle(PlayerAttemptPickupItemEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void handle(EntityPickupItemEvent event) {
    event.setCancelled(true);
  }

  @EventHandler
  public void handle(PlayerDropItemEvent event) {
    event.setCancelled(true);
  }

}
