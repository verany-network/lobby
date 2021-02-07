package net.verany.hubsystem.utils.inventories;

import net.verany.api.Verany;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.player.IPlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class HubSwitcherInventory {

    private final Player player;
    private final IPlayerInfo playerInfo;

    public HubSwitcherInventory(Player player) {
        this.player = player;
        this.playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
    }

    public void setItems() {
        Inventory inventory = InventoryBuilder.builder().size(9).title(playerInfo.getKey("hub.switcher.title")).event(event -> {

        }).build().buildAndOpen(player);


    }
}
