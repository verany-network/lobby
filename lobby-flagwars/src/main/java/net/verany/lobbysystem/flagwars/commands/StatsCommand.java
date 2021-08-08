package net.verany.lobbysystem.flagwars.commands;

import com.google.common.collect.ImmutableList;
import net.verany.api.Verany;
import net.verany.api.command.AbstractCommand;
import net.verany.api.command.CommandEntry;
import net.verany.api.module.VeranyPlugin;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.player.permission.duration.AbstractGroupTime;
import net.verany.api.player.permission.duration.GroupTime;
import net.verany.api.player.stats.IStatsObject;
import net.verany.api.player.stats.StatsObject;
import net.verany.api.region.GameRegion;
import net.verany.lobbysystem.flagwars.LobbyFlagWars;
import net.verany.lobbysystem.flagwars.map.data.MapData;
import net.verany.lobbysystem.flagwars.player.IFlagWarsPlayer;
import net.verany.lobbysystem.flagwars.player.stats.FlagWarsStats;
import net.verany.lobbysystem.flagwars.player.stats.RoundInfo;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class StatsCommand extends AbstractCommand implements TabCompleter {

    private final GameRegion gameRegion;

    public StatsCommand(VeranyPlugin plugin, GameRegion flagWarsRegion) {
        super(plugin);
        this.gameRegion = flagWarsRegion;

        Verany.registerCommand(plugin, new CommandEntry("stats", null, this), (playerInfo, strings) -> {
            Player player = playerInfo.getPlayer();

            if (!flagWarsRegion.isInRegion(player.getLocation())) {
                player.sendMessage("yo bro du musst in der fw region sein");
                return;
            }

            if (strings.length == 0) {
                sendStats(player, playerInfo, IStatsObject.StatsTime.ALL_TIME.getTime());
            } else if (strings.length == 1) {
                if (isType(strings[0].toUpperCase())) {
                    AbstractGroupTime.GroupDuration statsTime = getStatsTime(strings[0]);
                    if (statsTime == null) {
                        player.sendMessage("enter a real number!");
                        return;
                    }
                    sendStats(player, playerInfo, statsTime);
                    return;
                }
                Verany.PROFILE_OBJECT.getPlayer(strings[0], IPlayerInfo.class).ifPresentOrElse(iPlayerInfo -> {
                    sendStats(player, iPlayerInfo, IStatsObject.StatsTime.ALL_TIME.getTime());
                }, () -> {
                    playerInfo.sendKey(playerInfo.getPrefix(plugin.getModule()), "core.rank.player_not_found", new Placeholder("%player%", strings[0]));
                });
            } else if (strings.length == 2) {
                if (strings[0].equalsIgnoreCase("map")) {
                    String map = strings[1];
                    MapData mapData = LobbyFlagWars.INSTANCE.getMapObject().getMap(map);
                    if (mapData == null) {
                        playerInfo.sendKey(playerInfo.getPrefix(plugin.getModule()), "core.rank.map_not_found", new Placeholder("%map%", map));
                        return;
                    }
                    sendStats(player, playerInfo, IStatsObject.StatsTime.ALL_TIME.getTime(), mapData);
                    return;
                }
                Verany.PROFILE_OBJECT.getPlayer(strings[0], IPlayerInfo.class).ifPresentOrElse(iPlayerInfo -> {
                    if (!isType(strings[1].toUpperCase())) {
                        String[] help = playerInfo.getKeyArray("bingo.stats.help", '~', new Placeholder("%prefix%", playerInfo.getPrefix(plugin.getModule())));
                        player.sendMessage(help);
                        return;
                    }
                    AbstractGroupTime.GroupDuration statsTime = getStatsTime(strings[1]);
                    if (statsTime == null) {
                        player.sendMessage("enter a real number!");
                        return;
                    }
                    sendStats(player, iPlayerInfo, statsTime);
                }, () -> {
                    playerInfo.sendKey(playerInfo.getPrefix(plugin.getModule()), "core.rank.player_not_found", new Placeholder("%player%", strings[1]));
                });
            } else if (strings.length == 3) {
                if (strings[0].equalsIgnoreCase("map")) {
                    String map = strings[1];
                    MapData mapData = LobbyFlagWars.INSTANCE.getMapObject().getMap(map);
                    if (mapData == null) {
                        playerInfo.sendKey(playerInfo.getPrefix(plugin.getModule()), "core.rank.map_not_found", new Placeholder("%map%", map));
                        return;
                    }
                    AbstractGroupTime.GroupDuration statsTime = getStatsTime(strings[2]);
                    if (statsTime == null) {
                        player.sendMessage("enter a real number!");
                        return;
                    }
                    sendStats(player, playerInfo, statsTime, mapData);
                }
            } else {
                String[] help = playerInfo.getKeyArray("flagwars.stats.help", '~', new Placeholder("%prefix%", playerInfo.getPrefix(plugin.getModule())));
                player.sendMessage(help);
            }
        });
    }

    private void sendStats(Player player, IPlayerInfo target, AbstractGroupTime.GroupDuration duration, MapData mapData) {
        IPlayerInfo playerInfo = Verany.getPlayer(player);

        IStatsObject statsObject;
        if (target.getPlayer(IFlagWarsPlayer.class) != null) {
            statsObject = target.getPlayer(IFlagWarsPlayer.class).getStatsObject();
        } else {
            statsObject = new StatsObject(getProject(), "flagwars");
            statsObject.load(target.getUniqueId());
        }

        long statsTime = duration.getMillis();

        int kills = 0;
        int deaths = 0;
        int wins = 0;
        int playedGames = 0;
        int grabbedFlags = 0;
        for (RoundInfo statsDatum : statsObject.getStatsData(FlagWarsStats.FINISHED_GAME, statsTime)) {
            if (statsDatum.getPlayedMap().equals(mapData.getName())) {
                kills += statsDatum.getKills();
                deaths += statsDatum.getDeaths();
                grabbedFlags += statsDatum.getGrabbedBanner();
                playedGames++;
                if (statsDatum.isWinner(target.getUniqueId()))
                    wins++;
            }
        }

        Placeholder[] placeholders = new Placeholder[]{
                new Placeholder("%kills%", Verany.asDecimal(kills)),
                new Placeholder("%deaths%", Verany.asDecimal(deaths)),
                new Placeholder("%kd%", statsObject.getKd(kills, deaths)),
                new Placeholder("%ranking%", Verany.asDecimal(1)),
                new Placeholder("%wins%", Verany.asDecimal(wins)),
                new Placeholder("%played_games%", Verany.asDecimal(playedGames)),
                new Placeholder("%victoryPercentage%", statsObject.getVictoryChance(playedGames, wins)),
                new Placeholder("%grabbed_flags%", Verany.asDecimal(grabbedFlags)),
                new Placeholder("%points%", Verany.asDecimal(statsObject.getStatsValue(FlagWarsStats.POINTS, statsTime)))
        };

        if (playedGames == 0) {
            playerInfo.sendKey(playerInfo.getPrefix("FlagWars"), "flagwars.never.played.map", new Placeholder("%name%", target.getNameWithColor()), new Placeholder("%map%", mapData.getName()));
            return;
        }

        playerInfo.sendKey(playerInfo.getPrefix("FlagWars"), "flagwars.stats.info_" + (player.getUniqueId().equals(target.getUniqueId()) ? "self" : "other") + ".map", new Placeholder("%stats_time%", playerInfo.getKey("core.stats_time." + duration.getKey().toLowerCase())), new Placeholder("%map%", mapData.getName()), new Placeholder("%name%", target.getNameWithColor()));
        String[] message = playerInfo.getKeyArray("flagwars.stats.message", '~', placeholders);
        player.sendMessage(message);
    }

    private void sendStats(Player player, IPlayerInfo target, AbstractGroupTime.GroupDuration duration) {
        IPlayerInfo playerInfo = Verany.getPlayer(player);

        IStatsObject statsObject;
        if (target.getPlayer(IFlagWarsPlayer.class) != null) {
            statsObject = target.getPlayer(IFlagWarsPlayer.class).getStatsObject();
        } else {
            statsObject = new StatsObject(getProject(), "flagwars");
            statsObject.load(target.getUniqueId());
        }

        long statsTime = System.currentTimeMillis() - duration.getMillis();
        System.out.println(statsTime + " time");

        int kills = statsObject.getStatsValue(FlagWarsStats.KILLS, statsTime);
        int deaths = statsObject.getStatsValue(FlagWarsStats.DEATHS, statsTime);
        int wins = statsObject.getStatsValue(FlagWarsStats.WINS, statsTime);
        int playedGames = statsObject.getStatsValue(FlagWarsStats.PLAYED_GAMES, statsTime);
        int rank = statsObject.getRanking(FlagWarsStats.POINTS, statsTime, IFlagWarsPlayer.class);
        int grabbedFlags = statsObject.getStatsValue(FlagWarsStats.GRABBED_FLAGS, statsTime);

        Placeholder[] placeholders = new Placeholder[]{
                new Placeholder("%kills%", Verany.asDecimal(kills)),
                new Placeholder("%deaths%", Verany.asDecimal(deaths)),
                new Placeholder("%kd%", statsObject.getKd(kills, deaths)),
                new Placeholder("%ranking%", Verany.asDecimal(rank)),
                new Placeholder("%wins%", Verany.asDecimal(wins)),
                new Placeholder("%played_games%", Verany.asDecimal(playedGames)),
                new Placeholder("%victoryPercentage%", statsObject.getVictoryChance(playedGames, wins)),
                new Placeholder("%grabbed_flags%", Verany.asDecimal(grabbedFlags)),
                new Placeholder("%points%", Verany.asDecimal(statsObject.getStatsValue(FlagWarsStats.POINTS, statsTime)))
        };

        if (playedGames == 0) {
            playerInfo.sendKey(playerInfo.getPrefix("FlagWars"), "flagwars.never.played.round", new Placeholder("%name%", target.getNameWithColor()));
            return;
        }

        playerInfo.sendKey(playerInfo.getPrefix("FlagWars"), "flagwars.stats.info_" + (player.getUniqueId().equals(target.getUniqueId()) ? "self" : "other"), new Placeholder("%stats_time%", playerInfo.getKey("core.stats_time." + duration.getKey().toLowerCase(), new Placeholder("%amount%", Verany.asDecimal(duration.getAmount())))), new Placeholder("%name%", target.getNameWithColor()));
        String[] message = playerInfo.getKeyArray("flagwars.stats.message", '~', placeholders);
        player.sendMessage(message);
    }

    private boolean isType(String s) {
        boolean toReturn = true;
        try {
            String[] split = isSplit(s);
            IStatsObject.StatsTime.valueOf(split != null ? split[0] : s);
        } catch (Exception e) {
            toReturn = false;
        }
        return toReturn;
    }

    private AbstractGroupTime.GroupDuration getStatsTime(String s) {
        String[] split = isSplit(s);
        AbstractGroupTime.GroupDuration statsTime;
        if (split != null) {
            statsTime = IStatsObject.StatsTime.valueOf(split[0].toUpperCase()).getTime();
            try {
                int amount = Integer.parseInt(split[1]);
                statsTime = new AbstractGroupTime.GroupDuration(amount, statsTime.getMillis() * amount, statsTime.getKey());
            } catch (NumberFormatException e) {
                return null;
            }
        } else {
            statsTime = IStatsObject.StatsTime.valueOf(s.toUpperCase()).getTime();
        }
        return statsTime;
    }

    private String[] isSplit(String s) {
        String[] split = s.split(":");
        if (split.length == 2)
            return split;
        return null;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String string, @NotNull String[] strings) {
        if (!(commandSender instanceof Player sender)) return ImmutableList.of();
        List<String> types = new ArrayList<>();

        if (!this.gameRegion.isInRegion(sender.getLocation())) return ImmutableList.of();

        for (IStatsObject.StatsTime value : IStatsObject.StatsTime.values())
            types.add(value.name().toLowerCase());

        List<String> players = new ArrayList<>();
        for (IPlayerInfo registeredPlayer : Verany.PROFILE_OBJECT.getRegisteredPlayers(IPlayerInfo.class))
            if (!registeredPlayer.getName().equals(commandSender.getName()))
                players.add(registeredPlayer.getName());

        if (strings.length == 1) {
            types.addAll(players);
            types.add("map");
            return StringUtil.copyPartialMatches(strings[0], types, new ArrayList<>(types.size()));
        } else if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("map")) {
                List<String> maps = LobbyFlagWars.INSTANCE.getMapObject().getMapsWithoutDuplicate().stream().map(MapData::getName).collect(Collectors.toList());
                return StringUtil.copyPartialMatches(strings[1], maps, new ArrayList<>(maps.size()));
            }
            if (!isType(strings[0].toUpperCase()))
                return StringUtil.copyPartialMatches(strings[1], types, new ArrayList<>(types.size()));
        } else if (strings.length == 3) {
            if (strings[0].equalsIgnoreCase("map"))
                if (!isType(strings[2].toUpperCase()))
                    return StringUtil.copyPartialMatches(strings[2], types, new ArrayList<>(types.size()));
        }
        return ImmutableList.of();
    }
}
