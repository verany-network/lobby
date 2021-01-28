package net.verany.hubsystem.utils.inventories;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.server.v1_16_R3.Particles;
import net.verany.api.Verany;
import net.verany.api.actionbar.DefaultActionbar;
import net.verany.api.enumhelper.EnumHelper;
import net.verany.api.enumhelper.VeranyEnum;
import net.verany.api.inventory.InventoryBuilder;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.particle.ParticleManager;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.prefix.PrefixPattern;
import net.verany.hubsystem.HubSystem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class TeleporterInventory {

    private final Player player;
    private final Integer[] locationSlots = {10, 11, 15, 16, 20, 22, 24};
    private final Integer[] categorySlots = {37, 49, 43};

    public void setItems(TeleporterCategory category) {
        IPlayerInfo playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();

        Inventory inventory = InventoryBuilder.builder().size(9 * 6).title("§8◗§7◗ §b§lTeleporter").event(event -> {
            event.setCancelled(true);

            TeleporterCategory teleporterCategory = EnumHelper.INSTANCE.valueOf(event.getCurrentItem().getType(), TeleporterCategory.values());
            if (teleporterCategory != null) {
                setItems(teleporterCategory);
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                return;
            }

            TeleportLocations locations = EnumHelper.INSTANCE.valueOf(event.getCurrentItem().getType(), TeleportLocations.values());
            if (locations != null) {
                boolean exist = HubSystem.INSTANCE.getLocationManager().existLocation(locations.getLocationName());
                if (!exist)
                    player.teleport(HubSystem.INSTANCE.getLocationManager().getLocation("spawn"));
                else
                    player.teleport(HubSystem.INSTANCE.getLocationManager().getLocation(locations.getLocationName()));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                String actionbar = playerInfo.getKey("hub.teleporter.actionbar", new Placeholder("%locationName%", getName(locations.name()).substring(8)));
                playerInfo.setActionbar(new DefaultActionbar(actionbar, 2000));
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    onlinePlayer.playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 1);
                }
            }
        }).build().fillInventory(new ItemBuilder(Material.valueOf(playerInfo.getFirstColor().name() + "_STAINED_GLASS_PANE")).setNoName().build()).fillInventory(null, locationSlots).buildAndOpen(player);

        for (int i = 0; i < getLocations(category).size(); i++) {
            TeleportLocations locations = getLocations(category).get(i);


            String[] lore = playerInfo.getKeyArray("hub.teleporter.lore." + locations.getLocationName().toLowerCase(), "~", new Placeholder("%online%", 0), new Placeholder("%rating%", "★★★★☆"));

            inventory.setItem(locationSlots[i], new ItemBuilder(locations.getMaterial()).addLoreArray(lore).setDisplayName(getName(locations.name())).build());
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
        FLAG_WARS(Material.BLUE_BANNER, TeleporterCategory.GAMES, "flagwars"),
        SNOW_WARS(Material.SNOWBALL, TeleporterCategory.GAMES, "snowwars"),
        SURVIVAL(Material.ENCHANTING_TABLE, TeleporterCategory.GAMES, "survival"),
        RPG(Material.TOTEM_OF_UNDYING, TeleporterCategory.GAMES, "rpg"),
        DUELS(Material.STICK, TeleporterCategory.GAMES, "duels"),
        SPAWN(Material.BEACON, TeleporterCategory.GAMES, "spawn"),
        ARCADE(Material.MINECART, TeleporterCategory.GAMES, "arcade"),
        HALL_OF_PAIN(Material.OAK_SIGN, TeleporterCategory.LOBBY_LOCATIONS, "hall_of_pain"),
        TEAM_HALL(Material.DIAMOND, TeleporterCategory.LOBBY_LOCATIONS, "team_hall"),
        DAILY_REWARD(Material.GOLD_INGOT, TeleporterCategory.LOBBY_LOCATIONS, "daily_reward"),
        LOOT_BOXES(Material.BLUE_SHULKER_BOX, TeleporterCategory.LOBBY_LOCATIONS, "loot_boxes"),
        JUKEBOX(Material.JUKEBOX, TeleporterCategory.LOBBY_LOCATIONS, "jukebox"),
        SPAWN_(Material.BEACON, TeleporterCategory.LOBBY_LOCATIONS, "spawn"),
        COMING_SOON4(Material.BARRIER, TeleporterCategory.LOBBY_LOCATIONS, "spawn"),
        ELYTRA(Material.ELYTRA, TeleporterCategory.LOBBY_GAMES, "elytra"),
        TIC_TAC_TOE(Material.NOTE_BLOCK, TeleporterCategory.LOBBY_GAMES, "tic_tac_toe"),
        JUMP_AND_RUN(Material.GOLDEN_BOOTS, TeleporterCategory.LOBBY_GAMES, "jump_and_run"),
        COMING_SOON1(Material.BARRIER, TeleporterCategory.LOBBY_GAMES, "spawn"),
        COMING_SOON2(Material.BARRIER, TeleporterCategory.LOBBY_GAMES, "spawn"),
        SPAWN__(Material.BEACON, TeleporterCategory.LOBBY_GAMES, "spawn"),
        COMING_SOON3(Material.BARRIER, TeleporterCategory.LOBBY_GAMES, "spawn");

        private final Material material;
        private final TeleporterCategory category;
        private final String locationName;
    }

    private List<TeleportLocations> getLocations(TeleporterCategory category) {
        List<TeleportLocations> toReturn = new ArrayList<>();
        for (TeleportLocations value : TeleportLocations.values())
            if (value.getCategory() == null || value.getCategory().equals(category))
                toReturn.add(value);
        return toReturn;
    }

}
