package net.verany.hubsystem.utils.player;

import lombok.Getter;
import lombok.Setter;
import net.verany.api.Verany;
import net.verany.api.hotbar.HotbarItem;
import net.verany.api.interfaces.IDefault;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.loader.database.DatabaseLoadObject;
import net.verany.api.loader.database.DatabaseLoader;
import net.verany.api.module.VeranyProject;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.skull.SkullBuilder;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.utils.inventories.ProfileInventory;
import net.verany.hubsystem.utils.inventories.TeleporterInventory;
import net.verany.hubsystem.utils.location.HubLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;

@Getter
public class HubPlayer extends DatabaseLoader implements IDefault<UUID> {

    private UUID uniqueId;
    private Player player;
    private IPlayerInfo playerInfo;

    public HubPlayer(VeranyProject project) {
        super(project, "player_hub");
    }

    @Override
    public void load(UUID uuid) {
        uniqueId = uuid;
        player = Bukkit.getPlayer(uuid);
        playerInfo = Verany.PROFILE_OBJECT.getPlayer(uuid).get();

        load(new LoadInfo<>("user_hub", PlayerData.class, new PlayerData(uuid, HubLocation.toHubLocation(HubSystem.INSTANCE.getLocationManager().getLocation("spawn")))));
    }

    @Override
    public void update() {
        save("user_hub");
    }

    public void setItems() {
        playerInfo.setItem(0, new HotbarItem(new ItemBuilder(Material.FIREWORK_ROCKET).setDisplayName(playerInfo.getKey("hub.item.teleporter")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                    new TeleporterInventory(player).setItems(TeleporterInventory.TeleporterCategory.GAMES);
            }
        });
        playerInfo.setItem(1, new HotbarItem(new ItemBuilder(Material.COMPASS).setDisplayName(playerInfo.getKey("hub.item.lootcompass")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {}
        });
        playerInfo.setItem(2, new HotbarItem(new ItemBuilder(Material.NAME_TAG).setDisplayName(playerInfo.getKey("hub.item.nick")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {}
        });
        playerInfo.setItem(4, new HotbarItem(new ItemBuilder(Material.TRIDENT).setDisplayName(playerInfo.getKey("hub.item.trident")).addEnchantment(Enchantment.RIPTIDE, 3).setUnbreakable(true), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {}
        });
        playerInfo.setItem(6, new HotbarItem(new ItemBuilder(Material.BOOK).setDisplayName(playerInfo.getKey("hub.item.inbox")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {}
        });
        playerInfo.setItem(7, new HotbarItem(new ItemBuilder(Material.CLOCK).setDisplayName(playerInfo.getKey("hub.item.hubswitcher")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {}
        });
        playerInfo.setItem(8, new HotbarItem(new SkullBuilder(playerInfo.getSkinData()).setDisplayName(playerInfo.getKey("hub.item.profile")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    if (!player.hasMetadata("profile.category"))
                        HubSystem.INSTANCE.setMetadata(player, "profile.category", ProfileInventory.ProfileCategory.FRIENDS);
                    new ProfileInventory(player).setItems((ProfileInventory.ProfileCategory) player.getMetadata("profile.category").get(0).value()).setCategoryItems();
                }
            }
        });
    }

    @Getter
    @Setter
    public static class PlayerData extends DatabaseLoadObject {

        private HubLocation lastLocation;

        public PlayerData(UUID uuid, HubLocation lastLocation) {
            super(uuid.toString());
            this.lastLocation = lastLocation;
        }
    }

}
