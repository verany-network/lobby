package net.verany.hubsystem.events;

import net.verany.api.inventory.InventoryBuilder;
import net.verany.hubsystem.utils.inventories.NewTeleporterInventory;
import net.verany.hubsystem.utils.inventories.TeleporterInventory;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractEvent implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();


        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            switch (player.getInventory().getItemInMainHand().getType()){
                case FIREWORK_ROCKET:
                    new NewTeleporterInventory(player).setItems(NewTeleporterInventory.TeleporterCategory.GAMES);
                    break;
                case STONE:

                    break;
                case DIRT:

                    break;
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onInteractt(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
    }
}
