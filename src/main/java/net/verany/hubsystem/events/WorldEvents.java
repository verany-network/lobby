package net.verany.hubsystem.events;

import lombok.Getter;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
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
        Player player = event.getPlayer();
        event.setCancelled(!player.getGameMode().equals(GameMode.CREATIVE));
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        Player player = (Player) event.getEntity();
        event.setCancelled(!player.getGameMode().equals(GameMode.CREATIVE));
    }

    @EventHandler
    public void onPlayerPickupArrow(PlayerPickupArrowEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(!player.getGameMode().equals(GameMode.CREATIVE));
    }

    @EventHandler
    public void onBlockPlace(PlayerHarvestBlockEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(!player.getGameMode().equals(GameMode.CREATIVE));
    }

    @EventHandler
    public void onBlockPreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(!player.getGameMode().equals(GameMode.CREATIVE));
        event.setDropItems(false);
        event.setExpToDrop(0);
    }

    @EventHandler
    public void onItemSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(!player.getGameMode().equals(GameMode.CREATIVE));
    }

    @EventHandler
    public void onItemSwap(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Trident) {
            System.out.println("trident");
            event.setCancelled(true);
        }
    }

}
