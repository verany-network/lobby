package net.verany.hubsystem.game;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.verany.api.Verany;
import net.verany.api.gamemode.VeranyGameMode;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

@Getter
@RequiredArgsConstructor
public enum VeranyGame {

    FLAGWARS("FW-Hub"),
    BINGO("Bingo");

    @Setter
    private Location location;
    private ArmorStand armorStand;
    private final String taskName;

    public void setArmorStand() {
        if (armorStand == null) armorStand = spawnArmorStand();

        switch (this) {
            case FLAGWARS -> {
                int playing = Verany.GAME_MODE_OBJECT.getOnlinePlayers(VeranyGameMode.FLAG_WARS);
                armorStand.setCustomName("§7Currently playing§8: §b" + playing);
            }
            case BINGO -> {
                int playing = Verany.GAME_MODE_OBJECT.getOnlinePlayers(VeranyGameMode.ARCADE);
                armorStand.setCustomName("§7Currently playing§8: §b" + playing);
            }
        }
    }

    private ArmorStand spawnArmorStand() {
        ArmorStand armorStand = location.getWorld().spawn(location.clone().add(0, 0.3, 0), ArmorStand.class);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
        return armorStand;
    }

}
