package space.mcplay.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.plugin.spigot.listener.SpigotListener;

public class WorldListener extends SpigotListener {

  public WorldListener(SpigotPlugin spigotPlugin) {
    super(spigotPlugin);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(WeatherChangeEvent event) {
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(BlockPhysicsEvent event) {
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(LeavesDecayEvent event) {
    event.setCancelled(true);
  }
}
