package net.verany.hubsystem.utils.bees;

import net.verany.api.task.AbstractTask;
import net.verany.hubsystem.HubSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class OrbTask extends AbstractTask {

    public OrbTask(long waitTime) {
        super(waitTime);
    }

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().size() == 0) return;
        for (Entity entity : HubSystem.INSTANCE.getLocationManager().getLocation("spawn").getWorld().getEntities()) {
            if (entity instanceof ArmorStand) {
                if (entity.hasMetadata("elytra_start") || entity.hasMetadata("jump_and_run_start")) {
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
        /*for (AbstractSetupCategory.LocationData teleportLocations : HubSystem.INSTANCE.getSetupObject().getCategory("teleportLocations").getLocations()) {
            Location location = teleportLocations.getLocation().toLocation();

            for (IPlayerInfo onlinePlayer : Verany.getOnlinePlayers()) {
                Player player = onlinePlayer.getPlayer();
                new ParticleManager(Particles.WITCH, location, true, 0, 0, 0, 1, 1).sendPlayer(player);
            }
        }*/
    }
}
