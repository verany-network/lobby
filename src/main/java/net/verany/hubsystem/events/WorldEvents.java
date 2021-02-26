package net.verany.hubsystem.events;

import lombok.Getter;
import net.verany.api.AbstractVerany;
import net.verany.api.Verany;
import net.verany.api.event.events.PlayerAfkEvent;
import net.verany.api.event.events.PlayerLanguageUpdateEvent;
import net.verany.api.event.events.PlayerPrefixUpdateEvent;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.player.afk.IAFKObject;
import net.verany.api.sound.VeranySound;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.utils.player.HubPlayer;
import net.verany.hubsystem.utils.player.jump.JumpAndRun;
import net.verany.hubsystem.utils.settings.HubSetting;
import org.bukkit.*;
import org.bukkit.block.Block;
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
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity() instanceof Trident) {
            event.setCancelled(false);
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Trident) {
            if (event.getEntity().getShooter() instanceof Player) {
                Player shooter = (Player) event.getEntity().getShooter();
                Location location = event.getEntity().getLocation();
                location.setPitch(shooter.getLocation().getPitch());
                location.setYaw(shooter.getLocation().getYaw());
                event.getEntity().remove();
                shooter.teleport(location.clone().add(0, 0.2, 0));
                Verany.getPlayer(shooter.getUniqueId().toString(), HubPlayer.class).setItems();
                Verany.PROFILE_OBJECT.getPlayer(shooter.getUniqueId()).get().playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
            }
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

        if (player.hasMetadata("jump_and_run")) {
            Block underBlock = player.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
            JumpAndRun jumpAndRun = (JumpAndRun) player.getMetadata("jump_and_run").get(0).value();
            if (jumpAndRun.isFreeze()) {
                int movX = event.getFrom().getBlockX() - event.getTo().getBlockX();
                int movZ = event.getFrom().getBlockZ() - event.getTo().getBlockZ();
                if ((Math.abs(movX) > 0.5) || (Math.abs(movZ) > 0.5))
                    player.teleport(event.getFrom());
                return;
            }
            if (underBlock.getLocation().getBlockX() == jumpAndRun.getNextLocation().getBlockX() && underBlock.getLocation().getBlockY() == jumpAndRun.getNextLocation().getBlockY() && underBlock.getLocation().getBlockZ() == jumpAndRun.getNextLocation().getBlockZ())
                jumpAndRun.nextBlock(player, false);
            if (player.getLocation().getY() < jumpAndRun.getCurrentLocation().getY() - 1) {
                jumpAndRun.stop(player);
                player.setWalkSpeed(0.3F);
                player.setAllowFlight(true);
            }
            return;
        }

        if (player.getGameMode().equals(GameMode.ADVENTURE)) {
            if (player.getLocation().add(0, -1, 0).getBlock().getType() != Material.AIR) {
                player.setAllowFlight(true);
                player.setFlying(false);
            }
        }

        if (player.hasMetadata("elytra") && (System.currentTimeMillis() > player.getMetadata("elytra").get(0).asLong()))
            if (player.isOnGround() || player.getLocation().getBlock().isLiquid()) {
                Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class).resetElytra();
                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1, 1.5F);
            }

        if (player.hasMetadata("jumping"))
            if (player.isOnGround()) {
                HubSystem.INSTANCE.removeMetadata(player, "jumping");
                player.setAllowFlight(true);
            }
        if (player.getLocation().getBlock().isLiquid()) {
            if (!player.hasMetadata("liquid") && player.getInventory().contains(Material.TRIDENT)) {
                HubSystem.INSTANCE.setMetadata(player, "liquid", true);
                Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class).setItems();
            }
        } else {
            if (player.hasMetadata("liquid") && player.getInventory().contains(Material.TRIDENT)) {
                HubSystem.INSTANCE.removeMetadata(player, "liquid");
                Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class).setItems();
            }
        }
    }

    @EventHandler
    public void handleClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        if (player.hasMetadata("profile.category.")) {
            player.getInventory().clear();
            Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class).setItems();
            HubSystem.INSTANCE.removeMetadata(player, "profile.category.");
            Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get().playSound(VeranySound.INVENTORY_CLOSE);
        } else if (player.hasMetadata("hub_switcher")) {
            HubSystem.INSTANCE.removeMetadata(player, "hub_switcher");
        } else if (player.hasMetadata("teleporter")) {
            HubSystem.INSTANCE.removeMetadata(player, "teleporter");
        } else if (player.hasMetadata("arcade")) {
            HubSystem.INSTANCE.removeMetadata(player, "arcade");
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

    @EventHandler
    public void handlePrefixUpdate(PlayerAfkEvent event) {
        Player player = event.getPlayer();
        if (event.getPlayerInfo().getAfkObject().isAfk()) {
            if (player.hasMetadata("jump_and_run")) {
                event.getPlayerInfo().getAfkObject().disableAfkCheck(IAFKObject.CheckType.MOVE);
                JumpAndRun jumpAndRun = (JumpAndRun) player.getMetadata("jump_and_run").get(0).value();
                jumpAndRun.stop(player);
                event.getPlayerInfo().getAfkObject().enableAfkCheck(IAFKObject.CheckType.MOVE);
            }
        }
    }

}
