package net.verany.hubsystem.game;

import net.verany.hubsystem.HubSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Locale;

public class HubManager implements IHubManager {

    @Override
    public void registerArmorStands() {
        for (ArmorStand world : HubSystem.INSTANCE.getLocationManager().getLocation("spawn").getWorld().getEntitiesByClass(ArmorStand.class)) {
            for (HubGame value : HubGame.values()) {
                Location location = HubSystem.INSTANCE.getLocationManager().getLocation(value.name().toLowerCase());
                if (location == null) continue;
                if (world.getLocation().distance(location) <= 1.5) {
                    world.setMetadata("hubGame", new FixedMetadataValue(HubSystem.INSTANCE, value));
                }
            }
        }
    }
}