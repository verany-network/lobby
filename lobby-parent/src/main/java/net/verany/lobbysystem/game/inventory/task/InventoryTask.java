package net.verany.lobbysystem.game.inventory.task;

import net.verany.api.Verany;
import net.verany.api.task.AbstractTask;
import net.verany.lobbysystem.LobbySystem;
import net.verany.lobbysystem.game.inventory.IHubInventory;
import net.verany.lobbysystem.game.player.IHubPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;

public class InventoryTask extends AbstractTask {

    public InventoryTask(long waitTime) {
        super(waitTime);
    }

    @Override
    public void run() {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (onlinePlayer.hasMetadata("inventory")) {
                IHubInventory hubInventory = (IHubInventory) onlinePlayer.getMetadata("inventory").get(0).value();
                hubInventory.setItems();
            }
        }
        Verany.sync(LobbySystem.INSTANCE, () -> {
            for (Trident world : Bukkit.getWorld("world").getEntitiesByClass(Trident.class)) {
                if (world.getShooter() instanceof Player && world.getLocation().getBlockY() <= 0) {
                    Player shooter = (Player) world.getShooter();
                    Verany.sync(LobbySystem.INSTANCE, world::remove);
                    Verany.getPlayer(shooter.getUniqueId(), IHubPlayer.class).setItems();
                }
            }
        });
    }
}
