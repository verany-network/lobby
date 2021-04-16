package net.verany.hubsystem.game.orb;

import net.verany.api.task.AbstractTask;
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
        if (Bukkit.getOnlinePlayers().size() == 0) return;
        for (Entity entity : HubSystem.INSTANCE.getLocationManager().getLocation("spawn").getWorld().getEntities()) {
            if (entity instanceof ArmorStand) {
                if (entity.hasMetadata("hubGame")) {
                    float pitch = entity.getLocation().getPitch();
                    pitch += 4;
                    if (pitch >= 180)
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
