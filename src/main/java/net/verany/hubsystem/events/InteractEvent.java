package net.verany.hubsystem.events;

import net.verany.api.Verany;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.utils.inventories.NickInventory;
import net.verany.hubsystem.utils.inventories.TeleporterInventory;
import org.bukkit.*;
import org.bukkit.block.Block;
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


        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
            switch (player.getInventory().getItemInMainHand().getType()) {

                case FIREWORK_ROCKET:
                    new TeleporterInventory(player).setItems(TeleporterInventory.TeleporterCategory.GAMES);
                    break;

                case COMPASS:
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 2F, 2F);
                    break;

                case NAME_TAG:
                    new NickInventory(player);
                    break;

                case TRIDENT:
                    event.setCancelled(event.getClickedBlock() != null && !event.getClickedBlock().isLiquid());
                    break;

                case BOOK:
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 2F, 2F);
                    break;

                case CLOCK:
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 2F, 2F);
                    break;

                case PLAYER_HEAD:
                    player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_BREAK, 2F, 2F);
                    break;
            }

        }

        Block targetBlock = player.getTargetBlock(5);
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
