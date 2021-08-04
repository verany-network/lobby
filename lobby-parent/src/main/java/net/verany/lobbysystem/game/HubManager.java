package net.verany.lobbysystem.game;

import net.verany.lobbysystem.LobbySystem;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;

public class HubManager implements IHubManager {

    @Override
    public void registerArmorStands() {
        for (ArmorStand world : LobbySystem.INSTANCE.getLocationManager().getLocation("spawn").getWorld().getEntitiesByClass(ArmorStand.class)) {
            for (LobbyGame value : LobbyGame.values()) {
                Location location = LobbySystem.INSTANCE.getLocationManager().getLocation(value.name().toLowerCase());
                if (location == null) continue;
                if (world.getLocation().distance(location) <= 1.5) {
                    world.setMetadata("hubGame", new FixedMetadataValue(LobbySystem.INSTANCE, value));
                }
            }
            if (!world.hasMetadata("hubGame") && world.getCustomName() != null) {
                for (VeranyGame value : VeranyGame.values())
                    if (ChatColor.stripColor(world.getCustomName()).equalsIgnoreCase(value.name())) {
                        value.setLocation(world.getLocation());
                        value.setArmorStand();
                        world.setMetadata("veranyGame", new FixedMetadataValue(LobbySystem.INSTANCE, value));
                    }
            }
        }
    }
}
