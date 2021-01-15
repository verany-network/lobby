package net.verany.hubsystem.utils.player;

import lombok.Getter;
import lombok.Setter;
import net.verany.api.Verany;
import net.verany.api.interfaces.IDefault;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.loader.database.DatabaseLoadObject;
import net.verany.api.loader.database.DatabaseLoader;
import net.verany.api.module.VeranyProject;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.skull.SkullBuilder;
import net.verany.hubsystem.HubSystem;
import net.verany.hubsystem.utils.location.HubLocation;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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
        player.getInventory().setItem(0, new ItemBuilder(Material.FIREWORK_ROCKET).setAmount(1).setDisplayName("§8◗§7◗ §b§lTeleporter").build());
        player.getInventory().setItem(1, new ItemBuilder(Material.COMPASS).setAmount(1).setDisplayName("§8◗§7◗ §b§lLoot Compass").build());
        player.getInventory().setItem(2, new ItemBuilder(Material.NAME_TAG).setAmount(1).setDisplayName("§8◗§7◗ §b§lNick").build());
        player.getInventory().setItem(4, new ItemBuilder(Material.TRIDENT).setAmount(1).setDisplayName("§8◗§7◗ §b§lTrident").build());
        player.getInventory().setItem(6, new ItemBuilder(Material.BOOK).setAmount(1).setDisplayName("§8◗§7◗ §b§lInbox").build());
        player.getInventory().setItem(7, new ItemBuilder(Material.CLOCK).setAmount(1).setDisplayName("§8◗§7◗ §b§lHub Switcher").build());
        player.getInventory().setItem(8, new SkullBuilder(playerInfo.getSkinData()).setDisplayName("§8◗§7◗ §b§lProfile").build());
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
