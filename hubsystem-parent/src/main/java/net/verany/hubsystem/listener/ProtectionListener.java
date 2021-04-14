package net.verany.hubsystem.listener;

import net.verany.api.Verany;
import net.verany.api.event.AbstractListener;
import net.verany.api.event.events.PlayerAfkEvent;
import net.verany.api.event.events.PlayerLanguageUpdateEvent;
import net.verany.api.event.events.PlayerPrefixUpdateEvent;
import net.verany.api.module.VeranyProject;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.player.afk.IAFKObject;
import net.verany.api.sound.VeranySound;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.game.jumpandrun.JumpAndRun;
import net.verany.hubsystem.game.player.HubPlayer;
import net.verany.hubsystem.game.player.IHubPlayer;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.util.Vector;

public class ProtectionListener extends AbstractListener {

    public ProtectionListener(VeranyProject project) {
        super(project);

        Verany.registerListener(project, BlockBreakEvent.class, event -> {
            event.setCancelled(true);
        });

        Verany.registerListener(project, BlockPlaceEvent.class, event -> {
            event.setCancelled(true);
        });

        Verany.registerListener(project, PlayerSwapHandItemsEvent.class, event -> {
            event.setCancelled(true);
        });

        Verany.registerListener(project, InventoryClickEvent.class, event -> {
            event.setCancelled(true);
        });

        Verany.registerListener(project, FoodLevelChangeEvent.class, event -> {
            event.setCancelled(true);
        });

        Verany.registerListener(project, EntityDamageEvent.class, event -> {
            event.setCancelled(true);
        });

        Verany.registerListener(project, PlayerInteractAtEntityEvent.class, event -> {
            event.setCancelled(true);
        });

        Verany.registerListener(project, PlayerInteractEvent.class, event -> {
            Player player = event.getPlayer();

            event.setCancelled(true);

            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) && player.getInventory().getItemInMainHand().getType().equals(Material.TRIDENT))
                event.setCancelled(false);
        });

        Verany.registerListener(project, PlayerDropItemEvent.class, event -> {
            event.setCancelled(true);
        });

        Verany.registerListener(project, WeatherChangeEvent.class, event -> {
            event.setCancelled(true);
        });

        Verany.registerListener(project, WeatherChangeEvent.class, event -> {
            event.setCancelled(true);
        });

        Verany.registerListener(project, PlayerLanguageUpdateEvent.class, event -> {
            Player player = event.getPlayer();
            if (!player.hasMetadata("profile.category"))
                Verany.getPlayer(player.getUniqueId().toString(), IHubPlayer.class).setItems();
        });

        Verany.registerListener(project, PlayerPrefixUpdateEvent.class, event -> {
            Player player = event.getPlayer();
            IPlayerInfo playerInfo = Verany.getPlayer(player);
            BossBar bar = Bukkit.getBossBar(new NamespacedKey(HubSystem.INSTANCE, "bossbar_" + player.getName()));
            bar.setColor(toBarColor(playerInfo.getPrefixPattern().getColor().getFirstColor()));
            if (!player.hasMetadata("profile.category"))
                Verany.getPlayer(player.getUniqueId().toString(), IHubPlayer.class).setItems();
        });

        Verany.registerListener(project, PlayerAfkEvent.class, event -> {
            Player player = event.getPlayer();
            IPlayerInfo playerInfo = event.getPlayerInfo();

            if(playerInfo.getAfkObject().isAfk()){
                if (player.hasMetadata("jump_and_run")) {
                    playerInfo.getAfkObject().disableAfkCheck(IAFKObject.CheckType.MOVE);
                    JumpAndRun jumpAndRun = (JumpAndRun) player.getMetadata("jump_and_run").get(0).value();
                    jumpAndRun.stop(player);
                    playerInfo.getAfkObject().enableAfkCheck(IAFKObject.CheckType.MOVE);
                }
            }
        });

        Verany.registerListener(project, InventoryCloseEvent.class, event -> {
            Player player = (Player) event.getPlayer();
            if (player.hasMetadata("profile.category.")) {
                player.getInventory().clear();
                Verany.getPlayer(player.getUniqueId().toString(), IHubPlayer.class).setItems();
                HubSystem.INSTANCE.removeMetadata(player, "profile.category.");
                Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get().playSound(VeranySound.INVENTORY_CLOSE);
            }
        });

        Verany.registerListener(project, PlayerMoveEvent.class, event -> {
            Player player = event.getPlayer();

            if(player.getLocation().getBlockY() <= 0)
                player.teleport(HubSystem.INSTANCE.getLocationManager().getLocation("spawn"));

            if (player.getGameMode().equals(GameMode.ADVENTURE)) {
                if (player.getLocation().add(0, -1, 0).getBlock().getType() != Material.AIR) {
                    player.setAllowFlight(true);
                    player.setFlying(false);
                }
            }
            if (player.getLocation().getBlock().isLiquid()) {
                if (!player.hasMetadata("liquid") && player.getInventory().contains(Material.TRIDENT)) {
                    HubSystem.INSTANCE.setMetadata(player, "liquid", true);
                    Verany.getPlayer(player.getUniqueId().toString(), IHubPlayer.class).setItems();
                }
            } else {
                if (player.hasMetadata("liquid") && player.getInventory().contains(Material.TRIDENT)) {
                    HubSystem.INSTANCE.removeMetadata(player, "liquid");
                    Verany.getPlayer(player.getUniqueId().toString(), IHubPlayer.class).setItems();
                }
            }
        });

        Verany.registerListener(project, ProjectileHitEvent.class, event -> {
            if (event.getEntity() instanceof Trident) {
                if (event.getEntity().getShooter() instanceof Player) {
                    Player shooter = (Player) event.getEntity().getShooter();
                    Location location = event.getEntity().getLocation();
                    location.setPitch(shooter.getLocation().getPitch());
                    location.setYaw(shooter.getLocation().getYaw());
                    event.getEntity().remove();
                    shooter.teleport(location.clone().add(0, 0.2, 0));
                    Verany.getPlayer(shooter.getUniqueId().toString(), IHubPlayer.class).setItems();
                    Verany.PROFILE_OBJECT.getPlayer(shooter.getUniqueId()).get().playSound(Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                }
            }
        });

        Verany.registerListener(project, PlayerToggleFlightEvent.class, event -> {
            Player player = event.getPlayer();
            if (player.getGameMode().equals(GameMode.ADVENTURE)) {
                event.setCancelled(true);
                player.setAllowFlight(false);
                player.setFlying(false);
                Vector vector = player.getLocation().getDirection().multiply(1.3).setY(1);
                player.playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, 3, 1);
                player.setVelocity(vector);
            }
        });
    }

    private BarColor toBarColor(ChatColor color) {
        try {
            return BarColor.valueOf(color.name().replace("DARK_", "").replace("LIGHT_", ""));
        } catch (IllegalArgumentException e) {
            return BarColor.BLUE;
        }
    }
}
