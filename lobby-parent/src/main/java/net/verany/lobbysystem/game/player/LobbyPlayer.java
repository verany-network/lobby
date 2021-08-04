package net.verany.lobbysystem.game.player;

import lombok.Getter;
import lombok.Setter;
import net.verany.api.Verany;
import net.verany.api.hotbar.HotbarItem;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.loader.database.DatabaseLoadObject;
import net.verany.api.loader.database.DatabaseLoader;
import net.verany.api.locationmanager.VeranyLocation;
import net.verany.api.module.VeranyProject;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.skull.SkullBuilder;
import net.verany.lobbysystem.LobbyKey;
import net.verany.lobbysystem.LobbySystem;
import net.verany.lobbysystem.game.inventory.HubSwitcherInventory;
import net.verany.lobbysystem.game.inventory.ProfileInventory;
import net.verany.lobbysystem.game.inventory.TeleporterInventory;
import net.verany.lobbysystem.game.scoreboard.HubScoreboard;
import net.verany.lobbysystem.game.scoreboard.IHubScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.boss.BossBar;
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
public class LobbyPlayer extends DatabaseLoader implements IHubPlayer {

    private UUID uniqueId;
    private IPlayerInfo playerInfo;
    private Player player;

    private BossBar bossBar;
    private IHubScoreboard scoreboard;

    public LobbyPlayer(VeranyProject project) {
        super(project, "players");
    }

    @Override
    public void load(UUID uuid) {
        this.uniqueId = uuid;
        this.playerInfo = Verany.getPlayer(uuid);
        this.player = Bukkit.getPlayer(uuid);

        load(new LoadInfo<>("user_lobby", PlayerData.class, new PlayerData(uuid, VeranyLocation.toVeranyLocation(LobbySystem.INSTANCE.getLocationManager().getLocation("spawn")))));
    }

    @Override
    public void update() {
        save("user_lobby");
    }

    @Override
    public void setItems() {
        player.getInventory().clear();

        playerInfo.setItem(0, new HotbarItem(new ItemBuilder(Material.FIREWORK_ROCKET).addLoreArray(playerInfo.getKeyArray("hub.item.teleporter.lore", '~')).setDisplayName(playerInfo.getKey(LobbyKey.TELEPORTER_NAME.build())), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    new TeleporterInventory(player).setItems();
                }
            }
        });
        playerInfo.setItem(1, new HotbarItem(new ItemBuilder(Material.COMPASS).addLoreArray(playerInfo.getKeyArray("hub.item.loot_compass.lore", '~')).setDisplayName(playerInfo.getKey("hub.item.lootcompass")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {
            }
        });
        playerInfo.setItem(2, new HotbarItem(new ItemBuilder(Material.NAME_TAG).addLoreArray(playerInfo.getKeyArray("hub.item.nick.lore", '~')).setDisplayName(playerInfo.getKey("hub.item.nick")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {
            }
        });
        if (player.hasMetadata("liquid"))
            playerInfo.setItem(4, new HotbarItem(new ItemBuilder(Material.TRIDENT).addLoreArray(playerInfo.getKeyArray("hub.item.trident_water.lore", '~')).addItemFlag(ItemFlag.values()).setDisplayName(playerInfo.getKey("hub.item.trident")).addEnchantment(Enchantment.RIPTIDE, 3).setUnbreakable(true), player) {
                @Override
                public void onInteract(PlayerInteractEvent event) {
                }
            });
        else
            playerInfo.setItem(4, new HotbarItem(new ItemBuilder(Material.TRIDENT).addLoreArray(playerInfo.getKeyArray("hub.item.trident_land.lore", '~')).addItemFlag(ItemFlag.values()).setDisplayName(playerInfo.getKey("hub.item.trident")).setUnbreakable(true), player) {
                @Override
                public void onInteract(PlayerInteractEvent event) {
                }
            });
        playerInfo.setItem(6, new HotbarItem(new ItemBuilder(Material.CHEST).addLoreArray(playerInfo.getKeyArray("hub.item.loot.lore", '~')).setDisplayName(playerInfo.getKey("hub.item.loot")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {
            }
        });
        playerInfo.setItem(7, new HotbarItem(new ItemBuilder(Material.CLOCK).addLoreArray(playerInfo.getKeyArray("hub.item.hub_switcher.lore", '~')).setDisplayName(playerInfo.getKey("hub.item.hubswitcher")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
                    new HubSwitcherInventory(player).setItems();
            }
        });
        playerInfo.setItem(8, new HotbarItem(new SkullBuilder(playerInfo.getSkinData()).addLoreArray(playerInfo.getKeyArray("hub.item.profile.lore", '~')).setDisplayName(playerInfo.getKey("hub.item.profile")), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {
                if (event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                    if (!player.hasMetadata("profile.category"))
                        LobbySystem.INSTANCE.setMetadata(player, "profile.category", ProfileInventory.ProfileCategory.FRIENDS);
                    new ProfileInventory(player).setItems((ProfileInventory.ProfileCategory) player.getMetadata("profile.category").get(0).value()).setCategoryItems();
                }
            }
        });
    }

    @Override
    public void startElytra() {
        player.getInventory().clear();
        LobbySystem.INSTANCE.setMetadata(player, "elytra", System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(2));
        LobbySystem.INSTANCE.setMetadata(player, "jumping", true);
        player.getInventory().setChestplate(new ItemBuilder(Material.ELYTRA).setUnbreakable(true).setNoName().build());
        player.getInventory().setHeldItemSlot(4);
        player.teleport(player.getLocation().add(0, 70, 0));

        Bukkit.getScheduler().runTaskLater(LobbySystem.INSTANCE, () -> player.setGliding(true), 3);

        setFirework(true);
    }

    @Override
    public void resetElytra() {
        player.getInventory().setChestplate(null);
        setItems();
        LobbySystem.INSTANCE.removeMetadata(player, "elytra");
        LobbySystem.INSTANCE.removeMetadata(player, "jumping");
    }

    @Override
    public void setFirework(boolean first) {
        if (!first) {
            player.getInventory().setItem(4, new ItemBuilder(Material.FIREWORK_STAR).setDisplayName(playerInfo.getKey("hub.item.wait")).build());
            Bukkit.getScheduler().runTaskLater(LobbySystem.INSTANCE, () -> {
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

    @Override
    public int getJumpAndRunHighScore() {
        return getData(PlayerData.class).getJumpAndRunHighScore();
    }

    @Override
    public void setJumpAndRunHighScore(int highScore) {
        getData(PlayerData.class).setJumpAndRunHighScore(highScore);
    }

    @Override
    public void addStatistics(String key) {
        getData(PlayerData.class).addTime(key);
    }

    @Override
    public void setLastLocation() {
        getData(PlayerData.class).setLastLocation(VeranyLocation.toVeranyLocation(player.getLocation()));
    }

    @Override
    public void setScoreboard() {
        IHubScoreboard scoreboard = new HubScoreboard(player);
        scoreboard.load();
        setScoreboard(scoreboard);
    }

    @Override
    public VeranyLocation getLastLocation() {
        return getData(PlayerData.class).getLastLocation();
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
