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
import net.verany.api.prefix.PrefixPattern;
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

        Inventory inventory = InventoryBuilder.builder().size(9 * 4).title("§bTeleporter").event(event -> {
            event.setCancelled(true);

            TeleporterCategory teleporterCategory = EnumHelper.INSTANCE.valueOf(event.getCurrentItem().getType(), TeleporterCategory.values());
            if (teleporterCategory != null) {
                setItems(teleporterCategory);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                return;
            }

            TeleportLocations locations = EnumHelper.INSTANCE.valueOf(event.getCurrentItem().getType(), TeleportLocations.values());
            if (locations != null) {
                player.teleport(locations.getLocation());
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                playerInfo.setActionbar(new DefaultActionbar("§f§l§oDu wurdest zu §b§l§o" + getName(locations.name()).substring(8) + " §f§l§oteleportert", 2000));
            }
        }).build().fillInventory(new ItemBuilder(Material.LIGHT_BLUE_STAINED_GLASS_PANE).setNoName().build()).fillInventory(null, locationSlots).buildAndOpen(player);

        for (int i = 0; i < getLocations(category).size(); i++) {
            TeleportLocations locations = getLocations(category).get(i);
            inventory.setItem(locationSlots[i], new ItemBuilder(locations.getMaterial()).setDisplayName(getName(locations.name())).build());
        }

        for (int i = 0; i < TeleporterCategory.values().length; i++) {
            TeleporterCategory categories = TeleporterCategory.values()[i];
            inventory.setItem(categorySlots[i], new ItemBuilder(categories.getMaterial()).setGlow(categories.equals(category)).setDisplayName(getName(categories.name())).build());
        }
    }

    private String getName(String enumName) {
        IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
        String categoryName = Verany.getNameOfEnum(enumName, "");
        categoryName = categoryName.substring(0, categoryName.length() - 1);
        String name = Verany.getPrefix(categoryName, playerInfo.getPrefixPattern());
        name = name.substring(0, name.length() - 7);
        return name;
    }

    @AllArgsConstructor
    @Getter
    public enum TeleporterCategory implements VeranyEnum {
        GAMES(Material.CROSSBOW),
        LOBBY_GAMES(Material.WOODEN_AXE),
        LOBBY_LOCATIONS(Material.NETHER_STAR);

        private final Material material;
    }

    @AllArgsConstructor
    @Getter
    public enum TeleportLocations implements VeranyEnum {
        SPAWN(Material.BEACON, null, Bukkit.getWorld("world").getSpawnLocation()),
        FLAG_WARS(Material.BLUE_BANNER, TeleporterCategory.GAMES, new Location(Bukkit.getWorld("world"), 113.5, 64.2, -58.5, -137, 0)),
        SNOW_WARS(Material.SNOWBALL, TeleporterCategory.GAMES, new Location(Bukkit.getWorld("world"), 143.5, 66.2, 11.5, -90, 0)),
        SURVIVAL(Material.SNOWBALL, TeleporterCategory.GAMES, new Location(Bukkit.getWorld("world"), 143.5, 66.2, 11.5, -90, 0)),
        RPG(Material.SNOWBALL, TeleporterCategory.GAMES, new Location(Bukkit.getWorld("world"), 143.5, 66.2, 11.5, -90, 0)),
        DUELS(Material.SNOWBALL, TeleporterCategory.GAMES, new Location(Bukkit.getWorld("world"), 143.5, 66.2, 11.5, -90, 0)),
        ARCADE(Material.SNOWBALL, TeleporterCategory.GAMES, new Location(Bukkit.getWorld("world"), 143.5, 66.2, 11.5, -90, 0)),
        ELYTRA(Material.ELYTRA, TeleporterCategory.LOBBY_GAMES, new Location(Bukkit.getWorld("world"), 19.5, 66.2, 16.5, -71, 0)),
        JUMP_AND_RUN(Material.SNOWBALL, TeleporterCategory.LOBBY_GAMES, new Location(Bukkit.getWorld("world"), 19.5, 66.2, 16.5, -71, 0));
        

        /*
        Location spawnLocation = new Location(Bukkit.getWorld("world"), 6, 67, 13, 0, 0);
        Location flagWarsArea = new Location(Bukkit.getWorld("world"), 113.5, 64.2, -58.5, -137, 0);
        Location snowWarsArea = new Location(Bukkit.getWorld("world"), 143.5, 66.2, 11.5, -90, 0);
        Location duelsArea = new Location(Bukkit.getWorld("world"), 143.5, 66.2, -11.5, -87, 88);
        Location creativeLocation = new Location(Bukkit.getWorld("world"), -24.5, 65.2, 9.5, 160, 0);
        Location teamHallLocation = new Location(Bukkit.getWorld("world"), 85.5, 53.2, 110.5, -5, 0);
        Location dailyRewardLocation = new Location(Bukkit.getWorld("world"), 6.5, 66.2, 1.5, -145, 0);
        Location lootBoxesLocation = new Location(Bukkit.getWorld("world"), 44.5, 63.2, 21.5, -45, 0);
        Location hallOfPainLocation = new Location(Bukkit.getWorld("world"), -31.5, 47.2, -33.5, -16, 0);
        Location elytraLocation = new Location(Bukkit.getWorld("world"), 20.5, 66.2, 6.5, -71, 0);
        Location infinityJumpAndRunLocation = new Location(Bukkit.getWorld("world"), 19.5, 66.2, 16.5, -71, 0);
        */

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
