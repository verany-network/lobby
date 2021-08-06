package net.verany.lobbysystem.flagwars.scoreboard;

import lombok.Getter;
import lombok.Setter;
import net.verany.api.Verany;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.player.permission.group.PlaytimeGroup;
import net.verany.api.player.stats.IStatsObject;
import net.verany.api.scoreboard.IScoreboardBuilder;
import net.verany.api.scoreboard.ScoreboardBuilder;
import net.verany.api.season.Season;
import net.verany.lobbysystem.flagwars.LobbyFlagWars;
import net.verany.lobbysystem.flagwars.player.IFlagWarsPlayer;
import net.verany.lobbysystem.flagwars.player.stats.FlagWarsStats;
import net.verany.lobbysystem.game.scoreboard.AbstractHubScoreboard;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;

@Getter
public class GameScoreboard extends AbstractHubScoreboard {

    private final Player player;
    private final IPlayerInfo playerInfo;
    private IScoreboardBuilder scoreboardBuilder;
    private int currentSide;

    public GameScoreboard(Player player) {
        this.player = player;
        this.playerInfo = Verany.getPlayer(player);
    }

    @Override
    public void load() {
        scoreboardBuilder = new ScoreboardBuilder(player);
        setScores();
        setDisplayName(0);
    }

    @Override
    public void setScores() {
        IFlagWarsPlayer lobbyPlayer = playerInfo.getPlayer(IFlagWarsPlayer.class);

        String rank = lobbyPlayer.getRankName();
        String challenged = "§8-";
        String challengedBy = "§8-";
        if (lobbyPlayer.getRequest() != null) {
            challenged = Verany.getPlayer(lobbyPlayer.getRequest().getUuid()).getNameWithColor();
        }
        for (IFlagWarsPlayer iLobbyPlayer : Verany.getPlayers(IFlagWarsPlayer.class)) {
            if (iLobbyPlayer.getRequest() == null) continue;
            if (iLobbyPlayer.hasSentRequest(player.getUniqueId()))
                challengedBy = Verany.getPlayer(iLobbyPlayer.getUniqueId()).getNameWithColor();
        }

        long season = LobbyFlagWars.INSTANCE.getCurrentSeasonStart();

        int allTimeKills = lobbyPlayer.getStatsObject().getStatsValue(FlagWarsStats.KILLS, IStatsObject.StatsTime.ALL_TIME);
        int allTimeWins = lobbyPlayer.getStatsObject().getStatsValue(FlagWarsStats.WINS, IStatsObject.StatsTime.ALL_TIME);

        int monthlyTimeKills = lobbyPlayer.getStatsObject().getStatsValue(FlagWarsStats.KILLS, IStatsObject.StatsTime.MONTHLY);
        int monthlyTimeWins = lobbyPlayer.getStatsObject().getStatsValue(FlagWarsStats.WINS, IStatsObject.StatsTime.MONTHLY);

        int seasonTimeKills = lobbyPlayer.getStatsObject().getStatsValue(FlagWarsStats.KILLS, season);
        int seasonTimeWins = lobbyPlayer.getStatsObject().getStatsValue(FlagWarsStats.WINS, season);

        String matchedLeftText = "";
        if (lobbyPlayer.getRanking().equals(PlaytimeGroup.UNRANKED)) {
            int matchedLeft = 5 - lobbyPlayer.getStatsObject().getStatsValue(FlagWarsStats.PLAYED_GAMES, season);
            matchedLeftText = playerInfo.getKey("flagwars.matches.left", new Placeholder("%amount%", matchedLeft));
        }

        String[] side = playerInfo.getKeyArray("flagwars.lobby.scoreboard", '_', new Placeholder("%rank%", rank), new Placeholder("%challenged%", challenged), new Placeholder("%challengedBy%", challengedBy), new Placeholder("%kills%", Verany.asDecimal(allTimeKills)), new Placeholder("%wins%", Verany.asDecimal(allTimeWins)), new Placeholder("%monthlyKills%", Verany.asDecimal(monthlyTimeKills)), new Placeholder("%monthlyWins%", Verany.asDecimal(monthlyTimeWins)), new Placeholder("%season%", LobbyFlagWars.INSTANCE.getCurrentSeasonFormatted()), new Placeholder("%matchesLeft%", matchedLeftText), new Placeholder("%seasonKills%", Verany.asDecimal(seasonTimeKills)), new Placeholder("%seasonWins%", Verany.asDecimal(seasonTimeWins)));
        if (currentSide >= side.length)
            currentSide = 0;

        String currentScoreboard = side[currentSide];
        String[] scores = currentScoreboard.split("~");
        int id = scores.length;
        for (int i = 0; i < scores.length; i++) {
            scoreboardBuilder.setSlot(i, scores[id - 1]);
            id--;
        }
    }

    @Override
    public void setDisplayName(int currentSlot) {
        String title = AbstractHubScoreboard.DISPLAY_NAME[currentSlot];
        title = title.replace("$f", ChatColor.valueOf(playerInfo.getPrefixPattern().getColor().firstColor()).toString());
        title = title.replace("$s", ChatColor.valueOf(playerInfo.getPrefixPattern().getColor().secondColor()).toString());
        if (scoreboardBuilder != null) {
            String season = getNameOfEnum(Season.getCurrentSeason().name(), "");
            String year = new SimpleDateFormat("yyyy").format(System.currentTimeMillis());
            scoreboardBuilder.setTitle(playerInfo.getKey("flagwars.scoreboard.title", new Placeholder("%title%", title), new Placeholder("%season%", season), new Placeholder("%year%", year)));
        }
    }

    @Override
    public void addCurrentSide() {
        currentSide++;
    }

    public static String getNameOfEnum(String enumName, String color) {
        String name;
        StringBuilder nameBuilder = new StringBuilder();
        for (String s : enumName.split("_"))
            nameBuilder.append(color).append(s.split("")[0].toUpperCase()).append(s.substring(1).toLowerCase()).append(" ");
        name = nameBuilder.toString();
        return name;
    }

}
