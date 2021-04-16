package net.verany.hubsystem.game;

import lombok.Getter;
import lombok.Setter;
import net.verany.api.Verany;
import net.verany.api.gamemode.VeranyGameMode;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;

@Getter
public enum VeranyGame {

    FLAGWARS,
    BINGO;

    @Setter
    private Location location;
    private ArmorStand armorStand;

    public void setArmorStand() {
        if (armorStand == null) armorStand = spawnArmorStand();

        switch (this) {
            case FLAGWARS: {
                int playing = Verany.GAME_MODE_OBJECT.getOnlinePlayers(VeranyGameMode.FLAG_WARS);
                armorStand.setCustomName("§7Currently playing§8: §b" + playing);
                break;
            }
            case BINGO: {
                int playing = Verany.GAME_MODE_OBJECT.getOnlinePlayers(VeranyGameMode.ARCADE);
                armorStand.setCustomName("§7Currently playing§8: §b" + playing);
                break;
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