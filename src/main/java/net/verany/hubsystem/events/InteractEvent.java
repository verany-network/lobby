package net.verany.hubsystem.events;

import net.verany.api.Verany;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.utils.inventories.NickInventory;
import net.verany.hubsystem.utils.inventories.TeleporterInventory;
import net.verany.hubsystem.utils.inventories.games.ArcadeInventory;
import net.verany.hubsystem.utils.inventories.games.FlagWarsInventory;
import net.verany.hubsystem.utils.player.HubPlayer;
import net.verany.hubsystem.utils.player.jump.JumpAndRun;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
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

        if (event.getAction().equals(Action.RIGHT_CLICK_AIR))
            if (event.getItem() != null && player.hasMetadata("elytra") && event.getItem().getType().equals(Material.FIREWORK_ROCKET)) {
                Bukkit.getScheduler().runTaskLater(HubSystem.INSTANCE, () -> Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class).setFirework(false), 2);
                event.setCancelled(false);
                return;
            }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
            switch (player.getInventory().getItemInMainHand().getType()) {
                case COMPASS:
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 2F, 2F);
                    break;
                case NAME_TAG:
                    new NickInventory(player);
                    break;
                case BOOK:
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 2F, 2F);
                    break;
                case CLOCK:
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 2F, 2F);
                    break;
            }
            if (event.getAction().equals(Action.RIGHT_CLICK_AIR) && player.getInventory().getItemInMainHand().getType().equals(Material.TRIDENT))
                event.setCancelled(false);

        }

        /*Block targetBlock = player.getTargetBlock(5);
        if (targetBlock.getType().equals(Material.END_PORTAL_FRAME)) {
            if (targetBlock.getLocation().getBlockX() == 47) {
                if (targetBlock.getLocation().getBlockZ() == 24) {
                    player.sendMessage("jo war die richtige location");
                    World world = player.getWorld();
                    Location lootBoxLocation = new Location(Bukkit.getWorld("world"), 47.5, 62, 24.5);
                    world.strikeLightning(lootBoxLocation);
                    targetBlock.setType(Material.END_PORTAL_FRAME, true);
                }
            }
        }*/
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        event.setCancelled(true);
        if (event.getRightClicked() instanceof ArmorStand) {
            if (event.getRightClicked().getCustomName() != null && event.getRightClicked().getCustomName().contains("§b§lBINGO")) {
                new ArcadeInventory(event.getPlayer(), ArcadeInventory.Category.BINGO).setItems();
                return;
            }
            if (event.getRightClicked().getCustomName() != null && event.getRightClicked().getCustomName().contains("§b§lSOLO")) {
                new FlagWarsInventory(event.getPlayer()).setItems(FlagWarsInventory.FlagWarsVariant.SOLO);
                return;
            }
            if (event.getRightClicked().getCustomName() != null && event.getRightClicked().getCustomName().contains("§b§lDUO")) {
                new FlagWarsInventory(event.getPlayer()).setItems(FlagWarsInventory.FlagWarsVariant.DUO);
                return;
            }
            if (event.getRightClicked().getLocation().distance(HubSystem.INSTANCE.getLocationManager().getLocation("elytra_start")) <= 1.5) {
                Verany.getPlayer(event.getPlayer().getUniqueId().toString(), HubPlayer.class).startElytra();
            } else if (event.getRightClicked().getLocation().distance(HubSystem.INSTANCE.getLocationManager().getLocation("jump_and_run_start")) <= 1.5) {
                Player player = event.getPlayer();
                JumpAndRun jumpAndRun = new JumpAndRun();
                jumpAndRun.start(player);
                HubSystem.INSTANCE.setMetadata(player, "jump_and_run", jumpAndRun);
                player.getInventory().clear();
                player.setWalkSpeed(0.2F);
                player.setAllowFlight(false);
            }
        }
    }

    @EventHandler
    public void onInteractt(PlayerInteractEntityEvent event) {
        event.setCancelled(true);
    }
}
