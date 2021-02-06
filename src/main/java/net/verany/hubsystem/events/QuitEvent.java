package net.verany.hubsystem.events;

import net.verany.api.Verany;
import net.verany.api.locationmanager.VeranyLocation;
import net.verany.hubsystem.utils.player.HubPlayer;
import net.verany.hubsystem.utils.player.jump.JumpAndRun;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(null);

        HubPlayer hubPlayer = Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class);
        hubPlayer.getData(HubPlayer.PlayerData.class).setLastLocation(VeranyLocation.toVeranyLocation(player.getLocation()));
        hubPlayer.update();
        Verany.removePlayer(player.getUniqueId().toString(), HubPlayer.class);

        for (Entity entity : player.getWorld().getEntities()) {
            if (entity instanceof Trident) {
                Trident trident = (Trident) entity;
                if (trident.getShooter() instanceof Player) {
                    Player shooter = (Player) trident.getShooter();
                    if (shooter.getName().equals(player.getName()))
                        trident.remove();
                }
            }
        }

        if (player.hasMetadata("jump_and_run")) {
            JumpAndRun jumpAndRun = (JumpAndRun) player.getMetadata("jump_and_run").get(0).value();
            jumpAndRun.stop(player);
        }

        /* if(!HubConfig.BEES_SPAWNED.getValue()) {
            if(Bukkit.getOnlinePlayers().size()) {

            }
        } */

    }


}
