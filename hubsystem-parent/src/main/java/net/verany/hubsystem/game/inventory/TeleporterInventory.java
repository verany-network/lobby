package net.verany.hubsystem.game.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.actionbar.DefaultActionbar;
import net.verany.api.enumhelper.EnumHelper;
import net.verany.api.enumhelper.IdentifierType;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.HubSystem;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
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
            TeleportType teleportType = EnumHelper.INSTANCE.valueOf(event.getCurrentItem().getType(), TeleportType.values());
            if(teleportType != null) {
                String name = teleportType.name().toLowerCase().replace("_", "");
                boolean exist = HubSystem.INSTANCE.getLocationManager().existLocation(name);
                if (!exist)
                    player.teleport(HubSystem.INSTANCE.getLocationManager().getLocation("spawn").clone().add(0, 0.2, 0));
                else
                    player.teleport(HubSystem.INSTANCE.getLocationManager().getLocation(name).clone().add(0, 0.2, 0));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                String actionbar = playerInfo.getKey("hub.teleporter.actionbar", new Placeholder("%locationName%", Verany.getNameOfEnum(teleportType.name(), "").replace(" ", "")));
                playerInfo.setActionbar(new DefaultActionbar(actionbar, 2000));
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 1);
                }
            }
        }).build().createAndOpen(player);

        for (int i = 0; i < TeleportType.values().length; i++) {
            TeleportType type = TeleportType.values()[i];
            inventory.setItem(i, new ItemBuilder(type.getId()).setDisplayName(playerInfo.getKey("hub.teleporter." + type.name().toLowerCase())).build());
        }
    }

    @AllArgsConstructor
    @Getter
    public enum TeleportType implements IdentifierType<Material> {
        GAMES(Material.CROSSBOW),
        SPAWN(Material.BEACON),
        LOOT_BOXES(Material.GREEN_SHULKER_BOX),
        TEAM_HALL(Material.SHIELD),
        HALL_OF_FAME(Material.DIAMOND);

        private final Material id;
    }
}
