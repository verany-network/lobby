package net.verany.hubsystem.events;

import net.verany.api.Verany;
import net.verany.api.actionbar.DefaultActionbar;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.utils.inventories.NewTeleporterInventory;
import net.verany.hubsystem.utils.inventories.NickInventory;
import net.verany.hubsystem.utils.inventories.TeleporterInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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


        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
            switch (player.getInventory().getItemInMainHand().getType()) {

                case FIREWORK_ROCKET:
                    new NewTeleporterInventory(player).setItems(NewTeleporterInventory.TeleporterCategory.GAMES);
                    break;

                case COMPASS:
                    playerInfo.addActionbar(new DefaultActionbar("§cDerzeit gibt es keinen Loot auf der Hub§8.", 1000));
                    break;

                case NAME_TAG:
                    playerInfo.addActionbar(new DefaultActionbar("§cDas Nick Tool ist derzeit deaktiviert§8.", 1000));
                    break;

                case TRIDENT:
                    playerInfo.addActionbar(new DefaultActionbar("§cDer Trident ist derzeit deaktiviert§8.", 1000));
                    event.setCancelled(false);
                    break;

                case BOOK:
                    playerInfo.addActionbar(new DefaultActionbar("§cDie Inbox ist derzeit deaktiviert§8.", 1000));
                    break;

                case CLOCK:
                    playerInfo.addActionbar(new DefaultActionbar("§cDer Hub Switcher ist derzeit deaktiviert§8.", 1000));
                    break;

                case PLAYER_HEAD:
                    playerInfo.addActionbar(new DefaultActionbar("§cDas Profil ist derzeit deaktiviert§8.", 1000));
                    break;
            }

        }

        Block targetBlock = player.getTargetBlock(5);
        if(targetBlock.getType().equals(Material.END_PORTAL_FRAME)) {
            if(targetBlock.getLocation().getBlockX() == 47) {
                if(targetBlock.getLocation().getBlockZ() == 24) {
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
