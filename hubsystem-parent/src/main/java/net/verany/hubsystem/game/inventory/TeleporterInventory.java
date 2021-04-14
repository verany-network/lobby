package net.verany.hubsystem.game.inventory;

import net.verany.api.Verany;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.player.IPlayerInfo;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public class TeleporterInventory {

    private final Player player;
    private final IPlayerInfo playerInfo;

    public TeleporterInventory(Player player) {
        this.player = player;
        this.playerInfo = Verany.getPlayer(player);
    }

    public void setItems() {
        Inventory inventory = InventoryBuilder.builder().inventoryType(InventoryType.HOPPER).title(playerInfo.getKey("hub.teleporter.title")).onClick(event -> {

        }).build().createAndOpen(player);
    }
}
