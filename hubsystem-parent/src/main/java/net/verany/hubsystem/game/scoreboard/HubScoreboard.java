package net.verany.hubsystem.game.scoreboard;

import net.verany.api.Verany;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.scoreboard.IScoreboardBuilder;
import net.verany.api.scoreboard.ScoreboardBuilder;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class HubScoreboard implements IHubScoreboard {

    private final String[] displayName = {"$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$s§lVerany", "$f§lV$s§lerany", "$f§lVe$s§lrany", "$f§lVer$s§lany", "$f§lVera$s§lny", "$f§lVeran$s§ly", "$f§lVerany", "$f§lVerany", "$f§lVerany", "$s§lVerany", "$s§lVerany", "$f§lVerany", "$f§lVerany", "$s§lVerany", "$s§lVerany", "$f§lVerany", "$f§lVerany"};

    private final Player player;
    private final IPlayerInfo playerInfo;
    private IScoreboardBuilder scoreboardBuilder;
    private int currentSlot = 0;

    public HubScoreboard(Player player) {
        this.player = player;
        this.playerInfo = Verany.getPlayer(player);
    }

    @Override
    public void load() {
        scoreboardBuilder = new ScoreboardBuilder(player);
        setScores();
        scoreboardBuilder.setTitle(displayName[0]);
    }

    @Override
    public void setScores() {
        if (!player.isOnline()) return;

        String playedTime = "";
        long seconds = TimeUnit.MILLISECONDS.toSeconds(playerInfo.getPlayTime());
        long minutes = TimeUnit.MILLISECONDS.toMinutes(playerInfo.getPlayTime());
        long hours = TimeUnit.MILLISECONDS.toHours(playerInfo.getPlayTime());
        long days = TimeUnit.MILLISECONDS.toDays(playerInfo.getPlayTime());
        if (days == 0) {
            if (hours == 0)
                if (minutes == 0)
                    playedTime = seconds + "s";
                else
                    playedTime = minutes + "m";
            else
                playedTime = hours + "h";
        } else {
            hours = seconds / 60 / 60 % 24;
            playedTime = days + "d" + (hours != 0 ? " " + hours + "h" : "");
        }

        String[] scores = playerInfo.getKeyArray("hub_scoreboard_scores", '~', new Placeholder("%rank%", playerInfo.getGroupWithColor()), new Placeholder("%credits%", playerInfo.getCreditsObject().getCreditsAsDecimal()), new Placeholder("%playtime%", playedTime), new Placeholder("%global_rank%", Verany.asDecimal(playerInfo.getGlobalRank())));
        int id = scores.length;
        for (int i = 0; i < scores.length; i++) {
            scoreboardBuilder.setSlot(i, scores[id - 1]);
            id--;
        }
    }

    @Override
    public void setDisplayName() {
        currentSlot++;
        if (currentSlot >= displayName.length)
            currentSlot = 0;
        String title = displayName[currentSlot];
        title = title.replace("$f", playerInfo.getPrefixPattern().getColor().getFirstColor().toString());
        title = title.replace("$s", playerInfo.getPrefixPattern().getColor().getSecondColor().toString());
        if (scoreboardBuilder != null)
            scoreboardBuilder.setTitle(title);
    }
}