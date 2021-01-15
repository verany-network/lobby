package net.verany.hubsystem.events;

import net.verany.api.Verany;
import net.verany.hubsystem.utils.location.HubLocation;
import net.verany.hubsystem.utils.player.HubPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class QuitEvent implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(null);

        HubPlayer hubPlayer =Verany.getPlayer(player.getUniqueId().toString(), HubPlayer.class);
        hubPlayer.getData(HubPlayer.PlayerData.class).setLastLocation(HubLocation.toHubLocation(player.getLocation()));
        hubPlayer.update();
        Verany.removePlayer(player.getUniqueId().toString(), HubPlayer.class);
    }
}
