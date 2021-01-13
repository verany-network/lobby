package net.verany.hubsystem.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickEvent implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
}
