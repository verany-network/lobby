package net.verany.hubsystem.utils.inventories;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.actionbar.DefaultActionbar;
import net.verany.api.enumhelper.EnumHelper;
import net.verany.api.enumhelper.VeranyEnum;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.player.IPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class NewTeleporterInventory {

    private final Player player;
    private final Integer[] locationSlots = {10, 11, 12, 13, 14, 15, 16};
    private final Integer[] categorySlots = {30, 31, 32};

    public void setItems(TeleporterCategory category) {
        IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();

        Inventory inventory = InventoryBuilder.builder().size(9 * 4).title("Teleporter").event(event -> {
            event.setCancelled(true);

            TeleporterCategory teleporterCategory = EnumHelper.INSTANCE.valueOf(event.getCurrentItem().getType(), TeleporterCategory.values());
            if (teleporterCategory != null) {
                setItems(teleporterCategory);
                return;
            }

            TeleportLocations locations = EnumHelper.INSTANCE.valueOf(event.getCurrentItem().getType(), TeleportLocations.values());
            if (locations != null) {
                player.teleport(locations.getLocation());
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                playerInfo.addActionbar(new DefaultActionbar("§f§l§oDu wurdest zu §b§l§o" + locations.name().toLowerCase() + " §f§l§oteleportert", 2000));
            }
        }).build().fillInventory(new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setNoName().build()).fillInventory(null, locationSlots).buildAndOpen(player);

        for (int i = 0; i < getLocations(category).size(); i++) {
            TeleportLocations locations = getLocations(category).get(i);
            inventory.setItem(locationSlots[i], new ItemBuilder(locations.getMaterial()).build());
        }

        for (int i = 0; i < TeleporterCategory.values().length; i++) {
            TeleporterCategory categories = TeleporterCategory.values()[i];
            inventory.setItem(categorySlots[i], new ItemBuilder(categories.getMaterial()).build());
        }
    }

    @AllArgsConstructor
    @Getter
    public enum TeleporterCategory implements VeranyEnum {
        LOBBY_GAMES(Material.WOODEN_AXE),
        LOBBY_LOCATIONS(Material.NETHER_STAR),
        GAMES(Material.CROSSBOW);

        private final Material material;
    }

    @AllArgsConstructor
    @Getter
    public enum TeleportLocations implements VeranyEnum {
        SPAWN(Material.BEACON, null, Bukkit.getWorld("world").getSpawnLocation());

        private final Material material;
        private final TeleporterCategory category;
        private final Location location;
    }

    private List<TeleportLocations> getLocations(TeleporterCategory category) {
        List<TeleportLocations> toReturn = new ArrayList<>();
        for (TeleportLocations value : TeleportLocations.values())
            if (value.getCategory() == null || value.getCategory().equals(category))
                toReturn.add(value);
        return toReturn;
    }

}
