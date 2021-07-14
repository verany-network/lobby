package space.mcplay.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.plugin.spigot.listener.SpigotListener;

public class EntityListener extends SpigotListener {

  public EntityListener(SpigotPlugin spigotPlugin) {
    super(spigotPlugin);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(PlayerArmorStandManipulateEvent event) {
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(EntityDamageEvent event) {
    event.setCancelled(true);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(CreatureSpawnEvent event) {
    if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM)
      event.setCancelled(true);
  }
}
