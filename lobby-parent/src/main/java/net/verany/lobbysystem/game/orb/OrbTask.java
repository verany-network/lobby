package net.verany.lobbysystem.game.orb;

import net.verany.api.Verany;
import net.verany.api.task.AbstractTask;
import net.verany.lobbysystem.LobbySystem;
import net.verany.lobbysystem.game.VeranyGame;
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
        Verany.sync(LobbySystem.INSTANCE, () -> {
            for (Entity entity : LobbySystem.INSTANCE.getLocationManager().getLocation("spawn").getWorld().getEntities()) {
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
                    } else if (entity.hasMetadata("veranyGame")) {
                        VeranyGame veranyGame = (VeranyGame) entity.getMetadata("veranyGame").get(0).value();
                        if (veranyGame == null) return;
                        veranyGame.setArmorStand();
                    }
                }
            }
        });
    }
}
