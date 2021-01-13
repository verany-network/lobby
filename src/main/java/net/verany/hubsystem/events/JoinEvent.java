package net.verany.hubsystem.events;

import net.verany.api.itembuilder.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinEvent implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        Location spawnLocation = new Location(Bukkit.getWorld("world"), 6, 67, 13);
        Player player = event.getPlayer();

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.setExp(0);
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(spawnLocation);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setHealthScale(2);
        player.setFlying(false);
        player.setLevel(0);
        player.getInventory().setItem(0, new ItemBuilder(Material.FIREWORK_ROCKET).setAmount(1).setDisplayName("§8◗§7◗ §b§lTeleporter").build());
        player.getInventory().setItem(1, new ItemBuilder(Material.COMPASS).setAmount(1).setDisplayName("§8◗§7◗ §b§lLoot Compass").build());
        player.getInventory().setItem(2, new ItemBuilder(Material.NAME_TAG).setAmount(1).setDisplayName("§8◗§7◗ §b§lNick").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.TRIDENT).setAmount(1).setDisplayName("§8◗§7◗ §b§lTrident").build());
        player.getInventory().setItem(6, new ItemBuilder(Material.BOOK).setAmount(1).setDisplayName("§8◗§7◗ §b§lInbox").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.CLOCK).setAmount(1).setDisplayName("§8◗§7◗ §b§lHub Switcher").build());
        player.getInventory().setItem(8, new ItemBuilder(Material.COMPARATOR).setAmount(1).setDisplayName("§8◗§7◗ §b§lProfile").build());

    }
}
