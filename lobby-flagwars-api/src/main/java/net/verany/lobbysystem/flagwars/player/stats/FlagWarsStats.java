package net.verany.lobbysystem.flagwars.player.stats;

import net.verany.api.player.stats.AbstractStatsType;
import net.verany.api.player.stats.StatsType;

public class FlagWarsStats {

    public static final AbstractStatsType<Integer> KILLS = new StatsType<>("kills", Integer.class);
    public static final AbstractStatsType<Integer> DEATHS = new StatsType<>("deaths", Integer.class);
    public static final AbstractStatsType<Integer> WINS = new StatsType<>("wins", Integer.class);
    public static final AbstractStatsType<Integer> PLAYED_GAMES = new StatsType<>("played_games", Integer.class);
    public static final AbstractStatsType<RoundInfo> FINISHED_GAME = new StatsType<>("finished_games", RoundInfo.class);
    public static final AbstractStatsType<Integer> GRABBED_FLAGS = new StatsType<>("grabbed_flags", Integer.class);
    public static final AbstractStatsType<Integer> POINTS = new StatsType<>("points", Integer.class);
    public static final AbstractStatsType<Integer> ELO = new StatsType<>("elo", Integer.class);
    public static final AbstractStatsType<Integer> ROUNDS_LEFT = new StatsType<>("rounds_left", Integer.class);

}
