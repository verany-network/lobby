package net.verany.hubsystem.utils.scoreboard;

import io.papermc.paper.world.MoonPhase;
import net.verany.api.Verany;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.scoreboard.IScoreboardBuilder;
import net.verany.api.scoreboard.ScoreboardBuilder;
import net.verany.hubsystem.HubSystem;
import org.bukkit.entity.Player;

public class HubScoreboard {

    private final String[] displayName = {"§b§lVerany", "§3§lV§b§lerany", "§b§lV§3§le§b§lrany", "§b§lVe§3§lr§b§lany", "§b§lVer§3§la§b§lny", "§b§lVera§3§ln§b§ly", "§b§lVeran§3§ly"};
    private final Player player;
    private final IPlayerInfo playerInfo;
    private IScoreboardBuilder scoreboardBuilder;

    public HubScoreboard(Player player) {
        this.player = player;
        this.playerInfo = Verany.PROFILE_OBJECT.getPlayer(player.getUniqueId()).get();
        HubSystem.INSTANCE.setMetadata(player, "scoreboard", this);
        load();
    }

    private void load() {
        scoreboardBuilder = new ScoreboardBuilder(player);
        HubSystem.INSTANCE.setMetadata(player, "displayNamePosition", 0);
        update();
    }

    public void update() {
        setScores();
    }

    private void setScores() {
        int currentSlot = player.getMetadata("displayNamePosition").get(0).asInt();
        currentSlot++;
        if (currentSlot >= displayName.length)
            currentSlot = 0;
        scoreboardBuilder.setTitle(displayName[currentSlot]);
        HubSystem.INSTANCE.setMetadata(player, "displayNamePosition", currentSlot);

        String[] scores = playerInfo.getKeyArray("hub_scoreboard_scores", "~", new Placeholder("%rank%", playerInfo.getGroupWithColor()), new Placeholder("%credits%", playerInfo.getCreditsObject().getCreditsAsDecimal()));
        int id = scores.length;
        for (int i = 0; i < scores.length; i++) {
            scoreboardBuilder.setSlot(i, scores[id - 1]);
            id--;
        }

    }

}
