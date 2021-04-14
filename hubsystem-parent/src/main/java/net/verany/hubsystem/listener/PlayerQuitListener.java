package net.verany.hubsystem.listener;

import net.verany.api.Verany;
import net.verany.api.event.AbstractListener;
import net.verany.api.module.VeranyProject;
import net.verany.api.player.IPlayerInfo;
import net.verany.hubsystem.game.player.IHubPlayer;
import org.bukkit.boss.BarFlag;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener extends AbstractListener {

    public PlayerQuitListener(VeranyProject project) {
        super(project);

        Verany.registerListener(project, PlayerQuitEvent.class, event -> {
            Player player = event.getPlayer();
            IPlayerInfo playerInfo = Verany.getPlayer(player);

            IHubPlayer hubPlayer = playerInfo.getPlayer(IHubPlayer.class);
            hubPlayer.setLastLocation();
            hubPlayer.getBossBar().removePlayer(player);
            hubPlayer.setBossBar(null);
            hubPlayer.update();
            Verany.removePlayer(player.getUniqueId().toString(), IHubPlayer.class);
        });
    }
}
