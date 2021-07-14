package space.mcplay.lobby.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import space.mcplay.plugin.spigot.SpigotPlugin;
import space.mcplay.plugin.spigot.listener.SpigotListener;

public class EntityFoodListener extends SpigotListener {

  public EntityFoodListener(SpigotPlugin spigotPlugin) {
    super(spigotPlugin);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void handle(FoodLevelChangeEvent event) {
    event.setCancelled(true);
  }

}
