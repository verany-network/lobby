package net.verany.hubsystem.events;

import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.event.events.PlayerLanguageUpdateEvent;
import net.verany.api.event.events.PlayerPrefixUpdateEvent;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.utils.player.HubPlayer;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.util.Vector;

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

    @EventHandler
    public void onDoubleJump(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.ADVENTURE)) {
            event.setCancelled(true);
            player.setAllowFlight(false);
            player.setFlying(false);
            Vector vector = player.getLocation().getDirection().multiply(1.3).setY(1);
            player.playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, 3, 1);
            player.setVelocity(vector);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode().equals(GameMode.ADVENTURE)) {
            if (player.getLocation().add(0, -1, 0).getBlock().getType() != Material.AIR) {
                player.setAllowFlight(true);
                player.setFlying(false);
            }
        }

        if (player.hasMetadata("elytra") && (System.currentTimeMillis() > player.getMetadata("elytra").get(0).asLong()))
            if (player.isOnGround()) {
                Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class).resetElytra();
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1.5F);
            }

        if (player.hasMetadata("jumping"))
            if (player.isOnGround()) {
                HubSystem.INSTANCE.removeMetadata(player, "jumping");
                player.setAllowFlight(true);
            }
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (player.hasMetadata("profile.category.")) {
            player.getInventory().clear();
            Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class).setItems();
            HubSystem.INSTANCE.removeMetadata(player, "profile.category.");
        }
    }

    @EventHandler
    public void handleLanguageUpdate(PlayerLanguageUpdateEvent event) {
        Player player = event.getPlayer();
        if (!player.hasMetadata("profile.category"))
            Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class).setItems();
    }

    @EventHandler
    public void handlePrefixUpdate(PlayerPrefixUpdateEvent event) {
        Player player = event.getPlayer();
        if (!player.hasMetadata("profile.category"))
            Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class).setItems();
    }

}
