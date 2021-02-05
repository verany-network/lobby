package net.verany.hubsystem.utils.bees;

import net.verany.hubsystem.HubSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

public class OrbTask implements Runnable {
    @Override
    public void run() {
        for (Entity entity : HubSystem.INSTANCE.getLocationManager().getLocation("spawn").getWorld().getEntities()) {
            if(entity instanceof ArmorStand) {
                if (entity.hasMetadata("elytra_start")) {
                    float pitch = entity.getLocation().getPitch();
                    pitch += 2;
                    if(pitch >= 180)
                        pitch = -180;
                    Location location = entity.getLocation();
                    location.setPitch(pitch);
                    entity.setRotation(entity.getLocation().getYaw(), pitch);
                }
            }
        }
    }
}
