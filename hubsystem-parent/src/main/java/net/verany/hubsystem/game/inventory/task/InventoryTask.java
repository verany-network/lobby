package net.verany.hubsystem.game.inventory.task;

import net.verany.api.Verany;
import net.verany.api.task.AbstractTask;
import net.verany.hubsystem.game.inventory.IHubInventory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

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
    }
}
