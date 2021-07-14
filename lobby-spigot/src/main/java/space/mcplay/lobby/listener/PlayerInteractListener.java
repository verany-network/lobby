package space.mcplay.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.plugin.spigot.listener.SpigotListener;

public class PlayerInteractListener extends SpigotListener {

  public PlayerInteractListener(SpigotPlugin spigotPlugin) {
    super(spigotPlugin);
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void handle(PlayerInteractEvent event) {
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(InventoryClickEvent event) {
    event.setCancelled(true);
  }
}
