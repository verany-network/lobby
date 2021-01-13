package net.verany.hubsystem.events;

import lombok.Getter;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

public class WorldEvents implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(PlayerHarvestBlockEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPreak(BlockBreakEvent event) {
        event.setCancelled(true);
        event.setDropItems(false);
        event.setExpToDrop(0);
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onItemSwap(ProjectileLaunchEvent event) {
        if(event.getEntity() instanceof Trident) {
            System.out.println("trident");
            event.setCancelled(true);
        }
    }

}
