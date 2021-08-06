package net.verany.lobbysystem.flagwars.region;

import com.destroystokyo.paper.ParticleBuilder;
import net.minecraft.world.level.block.SoundEffectType;
import net.verany.api.Verany;
import net.verany.api.actionbar.DefaultActionbar;
import net.verany.api.cuboid.Cuboid;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.region.GameRegion;
import net.verany.lobbysystem.flagwars.player.IFlagWarsPlayer;
import net.verany.lobbysystem.flagwars.scoreboard.GameScoreboard;
import net.verany.lobbysystem.game.BossBarSetting;
import net.verany.lobbysystem.game.player.IHubPlayer;
import net.verany.lobbysystem.game.scoreboard.AbstractHubScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class FlagWarsRegion extends GameRegion {

    public FlagWarsRegion(Cuboid cuboid) {
        super(cuboid);
    }

    @Override
    public void onEnter(Player player) {
        IHubPlayer hubPlayer = Verany.getPlayer(player.getUniqueId(), IHubPlayer.class);
        IFlagWarsPlayer flagWarsPlayer = Verany.getPlayer(player.getUniqueId(), IFlagWarsPlayer.class);
        flagWarsPlayer.setItems();

        for (int i = 0; i < 15; i++) {
            player.getScoreboard().resetScores(ChatColor.values()[i].toString());
        }

        AbstractHubScoreboard scoreboard = new GameScoreboard(player);
        scoreboard.load();
        hubPlayer.setScoreboard(scoreboard);

        IPlayerInfo playerInfo = Verany.getPlayer(player);
        playerInfo.setActionbar(new DefaultActionbar(playerInfo.getKey("hub.flagwars.entered"), 2 * 1000));
        playerInfo.playSound(Sound.BLOCK_AMETHYST_BLOCK_HIT, 1, 0.4F);

        //playerInfo.setTempSetting(BossBarSetting.MESSAGES, new String[]{"Playing players", "Servers"});

        displayBorder(new ParticleBuilder(Particle.REDSTONE).color(Color.AQUA).count(5).offset(0.2, 0, 0.2).receivers(player));
    }

    @Override
    public void onLeave(Player player) {
        IHubPlayer hubPlayer = Verany.getPlayer(player.getUniqueId(), IHubPlayer.class);
        hubPlayer.setItems();
        hubPlayer.setScoreboard();
        hubPlayer.setDefaultBossBar();

        IPlayerInfo playerInfo = Verany.getPlayer(player);
        playerInfo.setActionbar(new DefaultActionbar(playerInfo.getKey("hub.flagwars.left"), 2 * 1000));
        playerInfo.playSound(Sound.BLOCK_AMETHYST_BLOCK_PLACE, 1, 1.7F);
    }
}
