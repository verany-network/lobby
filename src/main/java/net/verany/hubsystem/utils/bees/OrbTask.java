package net.verany.hubsystem.utils.bees;

import net.verany.api.task.AbstractTask;
import net.verany.hubsystem.HubSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

public class OrbTask extends AbstractTask {

    public OrbTask(long waitTime) {
        super(waitTime);
    }

    @Override
    public void run() {
        for (Entity entity : HubSystem.INSTANCE.getLocationManager().getLocation("spawn").getWorld().getEntities()) {
            if(entity instanceof ArmorStand) {
                if (entity.hasMetadata("elytra_start") || entity.hasMetadata("jump_and_run_start")) {
                    float pitch = entity.getLocation().getPitch();
                    pitch += 4;
                    if(pitch >= 180)
                        pitch = -180;
                    Location location = entity.getLocation();
                    location.setPitch(pitch);
                    location.setYaw(pitch);
                    entity.teleport(location);
                }
            }
        }
    }
}
