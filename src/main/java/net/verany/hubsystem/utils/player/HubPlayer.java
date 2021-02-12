package net.verany.hubsystem.utils.player;

import lombok.Getter;
import lombok.Setter;
import net.verany.api.Verany;
import net.verany.api.hotbar.HotbarItem;
import net.verany.api.interfaces.IDefault;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.loader.database.DatabaseLoadObject;
import net.verany.api.loader.database.DatabaseLoader;
import net.verany.api.locationmanager.VeranyLocation;
import net.verany.api.module.VeranyProject;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.skull.SkullBuilder;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.utils.inventories.HubSwitcherInventory;
import net.verany.hubsystem.utils.inventories.ProfileInventory;
import net.verany.hubsystem.utils.inventories.TeleporterInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
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

        load(new LoadInfo<>("user_hub", PlayerData.class, new PlayerData(uuid, VeranyLocation.toVeranyLocation(HubSystem.INSTANCE.getLocationManager().getLocation("spawn")))));
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
                    new TeleporterInventory(player).setItems();
            }
        });
        playerInfo.setItem(1, new HotbarItem(new ItemBuilder(Material.COMPASS).setDisplayName(playerInfo.getKey("hub.item.lootcompass")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {
            }
        });
        playerInfo.setItem(2, new HotbarItem(new ItemBuilder(Material.NAME_TAG).setDisplayName(playerInfo.getKey("hub.item.nick")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {
            }
        });
        if (player.hasMetadata("liquid"))
            playerInfo.setItem(4, new HotbarItem(new ItemBuilder(Material.TRIDENT).addItemFlag(ItemFlag.values()).setDisplayName(playerInfo.getKey("hub.item.trident")).addEnchantment(Enchantment.RIPTIDE, 3).setUnbreakable(true), player) {
                @Override
                public void onInteract(PlayerInteractEvent event) {
                }
            });
        else
            playerInfo.setItem(4, new HotbarItem(new ItemBuilder(Material.TRIDENT).addItemFlag(ItemFlag.values()).setDisplayName(playerInfo.getKey("hub.item.trident")).setUnbreakable(true), player) {
                @Override
                public void onInteract(PlayerInteractEvent event) {
                }
            });
        playerInfo.setItem(6, new HotbarItem(new ItemBuilder(Material.CHEST).setDisplayName(playerInfo.getKey("hub.item.loot")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {
            }
        });
        playerInfo.setItem(7, new HotbarItem(new ItemBuilder(Material.CLOCK).setDisplayName(playerInfo.getKey("hub.item.hubswitcher")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                    new HubSwitcherInventory(player).setItems();
            }
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

    public void startElytra() {
        player.getInventory().clear();
        HubSystem.INSTANCE.setMetadata(player, "elytra", System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
        HubSystem.INSTANCE.setMetadata(player, "jumping", true);
        player.getInventory().setChestplate(new ItemBuilder(Material.ELYTRA).setUnbreakable(true).setNoName().build());
        player.getInventory().setHeldItemSlot(4);
        player.teleport(player.getLocation().add(0, 70, 0));

        Bukkit.getScheduler().runTaskLater(HubSystem.INSTANCE, () -> player.setGliding(true), 3);

        setFirework(true);
    }

    public void resetElytra() {
        player.getInventory().setChestplate(null);
        setItems();
        HubSystem.INSTANCE.removeMetadata(player, "elytra");
        HubSystem.INSTANCE.removeMetadata(player, "jumping");
    }

    public void setFirework(boolean first) {
        if (!first) {
            player.getInventory().setItem(4, new ItemBuilder(Material.FIREWORK_STAR).setDisplayName(playerInfo.getKey("hub.item.wait")).build());
            Bukkit.getScheduler().runTaskLater(HubSystem.INSTANCE, () -> {
                if (player.hasMetadata("elytra"))
                    setFirework(true);
            }, 20);
            return;
        }
        ItemStack firework = new ItemStack(Material.FIREWORK_ROCKET);
        FireworkMeta fireworkMeta = (FireworkMeta) firework.getItemMeta();
        fireworkMeta.setPower(3);
        firework.setItemMeta(fireworkMeta);
        player.getInventory().setItem(4, new ItemBuilder(firework).setDisplayName(playerInfo.getKey("hub.item.power")).addItemFlag(ItemFlag.values()).build());
    }

    public int getJumpAndRunHighScore() {
        return getData(PlayerData.class).getJumpAndRunHighScore();
    }

    public void setJumpAndRunHighScore(int highScore) {
        getData(PlayerData.class).setJumpAndRunHighScore(highScore);
    }

    public void addStatistics(String key) {
        getData(PlayerData.class).addTime(key);
    }

    @Getter
    @Setter
    public static class PlayerData extends DatabaseLoadObject {

        private VeranyLocation lastLocation;
        private int jumpAndRunHighScore;
        private final Map<String, Long> time = new HashMap<>();

        public PlayerData(UUID uuid, VeranyLocation lastLocation) {
            super(uuid.toString());
            this.lastLocation = lastLocation;
        }

        public void addTime(String key) {
            if (time.containsKey(key))
                time.put(key, time.get(key) + 1);
            else
                time.put(key, 0L);
        }
    }

}
