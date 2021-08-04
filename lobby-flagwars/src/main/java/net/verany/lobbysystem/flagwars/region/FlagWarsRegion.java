package net.verany.lobbysystem.flagwars.region;

import com.destroystokyo.paper.ParticleBuilder;
import net.verany.api.Verany;
import net.verany.api.actionbar.DefaultActionbar;
import net.verany.api.cuboid.Cuboid;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.region.GameRegion;
import net.verany.lobbysystem.flagwars.player.IFlagWarsPlayer;
import net.verany.lobbysystem.flagwars.scoreboard.GameScoreboard;
import net.verany.lobbysystem.game.player.IHubPlayer;
import net.verany.lobbysystem.game.scoreboard.IHubScoreboard;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;

public class FlagWarsRegion extends GameRegion {

    public FlagWarsRegion(Cuboid cuboid) {
        super(cuboid);
    }

    @Override
    public void onEnter(Player player) {
        IHubPlayer hubPlayer = Verany.getPlayer(player.getUniqueId(), IHubPlayer.class);
        IFlagWarsPlayer flagWarsPlayer = Verany.getPlayer(player.getUniqueId(), IFlagWarsPlayer.class);
        flagWarsPlayer.setItems();

        IHubScoreboard scoreboard = new GameScoreboard(player);
        scoreboard.load();
        hubPlayer.setScoreboard(scoreboard);

        IPlayerInfo playerInfo = Verany.getPlayer(player);
        playerInfo.setActionbar(new DefaultActionbar(playerInfo.getKey("hub.flagwars.entered"), 2 * 1000));

        displayBorder(new ParticleBuilder(Particle.REDSTONE).color(Color.fromBGR(51, 102, 153)).receivers(player));
    }

    @Override
    public void onLeave(Player player) {
        IHubPlayer hubPlayer = Verany.getPlayer(player.getUniqueId(), IHubPlayer.class);
        hubPlayer.setItems();
        hubPlayer.setScoreboard();

        IPlayerInfo playerInfo = Verany.getPlayer(player);
        playerInfo.setActionbar(new DefaultActionbar(playerInfo.getKey("hub.flagwars.left"), 2 * 1000));
    }
}
