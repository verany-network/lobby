package net.verany.lobbysystem.flagwars.queue;

import com.google.common.collect.Lists;
import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.actionbar.DefaultActionbar;
import net.verany.api.interfaces.IDefault;
import net.verany.api.loader.database.DatabaseLoadObject;
import net.verany.api.loader.database.DatabaseLoader;
import net.verany.api.module.VeranyPlugin;
import net.verany.api.module.VeranyProject;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.task.AbstractTask;
import net.verany.lobbysystem.flagwars.LobbyFlagWars;
import net.verany.lobbysystem.flagwars.Variant;
import net.verany.lobbysystem.flagwars.VariantType;
import net.verany.lobbysystem.flagwars.player.FlagWarsPlayer;
import net.verany.lobbysystem.flagwars.player.IFlagWarsPlayer;
import net.verany.lobbysystem.flagwars.round.AbstractRound;
import net.verany.lobbysystem.flagwars.round.FlagWarsRound;
import net.verany.lobbysystem.flagwars.scoreboard.GameScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class QueueObject extends DatabaseLoader implements IQueueObject {

    private final VeranyPlugin plugin;

    public QueueObject(VeranyPlugin project) {
        super(project, "queue");
        this.plugin = project;

        load(new LoadInfo<>("queue", QueueData.class, new QueueData("queue")));

        startTask();
    }

    private int calculateSearchTime(VariantType type) {
        final int defaultTime = 15;
        int playersInQueue = getPlayersInQueue(type).size();
        if (playersInQueue <= 1)
            return Integer.MAX_VALUE;
        int percentage = playersInQueue * defaultTime / 100;
        return defaultTime + percentage;
    }

    @Override
    public void updateQueue() {
        save("queue");
    }

    @Override
    public Queue<QueueEntry> getPlayersInQueue() {
        if (getDataOptional(QueueData.class).isEmpty()) return new LinkedList<>();
        return getDataOptional(QueueData.class).get().getQueue();
    }

    @Override
    public boolean isInQueue(UUID uuid) {
        return getPlayersInQueue().stream().anyMatch(queueEntry -> queueEntry.getUuid().equals(uuid));
    }

    @Override
    public QueueEntry getQueueEntry(UUID uuid) {
        return getPlayersInQueue().stream().filter(queueEntry -> queueEntry.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    @Override
    public void joinQueue(QueueEntry entry) {
        getPlayersInQueue().add(entry);
        updateQueue();
    }

    @Override
    public void leaveQueue(UUID uuid) {
        getPlayersInQueue().remove(getQueueEntry(uuid));
        updateQueue();
        Bukkit.getPlayer(uuid).removeMetadata("queue", plugin);
        Verany.getPlayer(uuid).setDefaultActionbar(null);
    }

    @Override
    public void request(IPlayerInfo playerInfo, IPlayerInfo targetInfo) {
        AbstractRound requestedRound = playerInfo.getPlayer(IFlagWarsPlayer.class).getRequestedRound();

        if (requestedRound != null) return;

        if (playerInfo.getPlayer(IFlagWarsPlayer.class).hasReceivedRequest(targetInfo.getUniqueId())) {
            playerInfo.setActionbar(new DefaultActionbar(playerInfo.getKey("flagwars.challenge.accepted", new Placeholder("%target%", targetInfo.getNameWithColor())), 2000));
            targetInfo.setActionbar(new DefaultActionbar(playerInfo.getKey("flagwars.challenge.accepted_target", new Placeholder("%target%", playerInfo.getNameWithColor())), 2000));

            targetInfo.playSound(Sound.ENTITY_PLAYER_LEVELUP);
            playerInfo.playSound(Sound.ENTITY_PLAYER_LEVELUP);

            ((FlagWarsPlayer) targetInfo.getPlayer(IFlagWarsPlayer.class)).setRequest(null);

            requestedRound = new FlagWarsRound(Lists.newArrayList(playerInfo.getUniqueId(), targetInfo.getUniqueId()), Variant.TWOTIMESONE, plugin);
            requestedRound.start();
            ((FlagWarsPlayer) targetInfo.getPlayer(IFlagWarsPlayer.class)).setRequestedRound(requestedRound);
            return;
        }

        if (playerInfo.getPlayer(IFlagWarsPlayer.class).hasSentRequest(targetInfo.getUniqueId())) {
            targetInfo.playSound(Sound.ENTITY_EVOKER_FANGS_ATTACK, 1, 1.9F);
            playerInfo.playSound(Sound.ENTITY_EVOKER_FANGS_ATTACK, 1, 1.9F);
            playerInfo.getPlayer(IFlagWarsPlayer.class).retractRequest(targetInfo.getUniqueId());
            playerInfo.setActionbar(new DefaultActionbar(playerInfo.getKey("flagwars.challenge.retracted", new Placeholder("%target%", targetInfo.getNameWithColor())), 2000));
            targetInfo.setActionbar(new DefaultActionbar(targetInfo.sendKey("flagwars.challenge.retracted_target", new Placeholder("%from%", playerInfo.getNameWithColor())), 2000));
        } else {
            targetInfo.playSound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 1.7F);
            playerInfo.playSound(Sound.BLOCK_NOTE_BLOCK_XYLOPHONE, 1, 1.7F);
            playerInfo.getPlayer(IFlagWarsPlayer.class).sendRequest(targetInfo.getUniqueId());
            playerInfo.setActionbar(new DefaultActionbar(playerInfo.getKey("flagwars.challenge.sent", new Placeholder("%target%", targetInfo.getNameWithColor())), 2000));
            targetInfo.setActionbar(new DefaultActionbar(targetInfo.getKey("flagwars.challenge.received", new Placeholder("%sender%", playerInfo.getNameWithColor())), 2000));
        }
    }

    @Override
    public void startTask() {
        Verany.addTask(new AbstractTask(1000) {
            @Override
            public void run() {
                if (Bukkit.getOnlinePlayers().isEmpty()) return;

                for (VariantType value : VariantType.values()) {
                    int searchTime = calculateSearchTime(value);
                    if (getLastQueueJoin(value) + TimeUnit.SECONDS.toMillis(searchTime) > System.currentTimeMillis()) {
                        for (IFlagWarsPlayer iLobbyPlayer : getPlayersInQueue(value)) {
                            IPlayerInfo playerInfo = Verany.getPlayer(iLobbyPlayer.getUniqueId());
                            Player player = playerInfo.getPlayer();
                            if (!player.hasMetadata("queue")) {
                                plugin.setMetadata(player, "queue", 0);
                            }
                            int waitingTime = player.getMetadata("queue").get(0).asInt();
                            if (TimeUnit.SECONDS.toMinutes(waitingTime) == 6) {
                                leaveQueue(iLobbyPlayer.getUniqueId());
                                continue;
                            }
                            playerInfo.setDefaultActionbar("§7Matchmaking §3" + GameScoreboard.getNameOfEnum(value.name(), "") + "Queue§8... §8(§b" + Verany.formatSeconds(waitingTime).replaceFirst("00:", "") + "s§8) §7Estimated time§8: §b" + (searchTime == Integer.MAX_VALUE ? "Never" : searchTime + "s"));
                            waitingTime++;
                            plugin.setMetadata(player, "queue", waitingTime);
                        }
                        continue;
                    }

                    List<IFlagWarsPlayer> lobbyPlayers = getPlayersInQueue(value);
                    if (lobbyPlayers.isEmpty()) continue;
                    List<Variant> list = Variant.getVariants(value);

                    int players = lobbyPlayers.size();
                    Variant possible = list.stream().min(Comparator.comparingInt(i -> Math.abs(i.getMaxPlayers() - players))).orElseThrow(() -> new NoSuchElementException("No value present"));
                    if (lobbyPlayers.size() < possible.getMaxPlayers()) {
                        QueueEntry lastEntry = getLastQueueJoinEntry(value);
                        if (lastEntry != null)
                            if (lastEntry.getTimestamp() + TimeUnit.SECONDS.toMillis(searchTime) < System.currentTimeMillis())
                                lastEntry.setTimestamp(System.currentTimeMillis());
                        continue;
                    }
                    List<IFlagWarsPlayer> roundPlayers = lobbyPlayers.stream().limit(Math.min(lobbyPlayers.size(), possible.getMaxOnlinePlayers())).collect(Collectors.toList());

                    AbstractRound requestedRound = new FlagWarsRound(roundPlayers.stream().map(IDefault::getUniqueId).collect(Collectors.toList()), possible, plugin);
                    Verany.sync(plugin, requestedRound::start);

                    for (IFlagWarsPlayer roundPlayer : roundPlayers) {
                        leaveQueue(roundPlayer.getUniqueId());
                        ((FlagWarsPlayer) roundPlayer).setRequestedRound(requestedRound);
                    }
                }

            }
        });
    }

    private List<IFlagWarsPlayer> getPlayersInQueue(VariantType variantType) {
        return getPlayersInQueue().stream().filter(entry -> entry.getVariantType().equals(variantType)).map(entry -> Verany.getPlayer(entry.getUuid()).getPlayer(IFlagWarsPlayer.class)).collect(Collectors.toList());
    }

    private long getLastQueueJoin(VariantType type) {
        if (getDataOptional(QueueData.class).isEmpty()) return -1;
        QueueEntry lastEntry = getLastQueueJoinEntry(type);
        if (lastEntry == null) return -1;
        return lastEntry.getTimestamp();
    }

    private QueueEntry getLastQueueJoinEntry(VariantType type) {
        if (getDataOptional(QueueData.class).isEmpty() || getPlayersInQueue(type).isEmpty()) return null;
        List<Verany.SortData<QueueEntry>> sortData = new ArrayList<>();
        for (QueueEntry queueEntry : getDataOptional(QueueData.class).get().getMap().get(type))
            sortData.add(new Verany.SortData<>(String.valueOf(queueEntry.getTimestamp()), queueEntry));
        return Verany.sortList(sortData, true).get(0);
    }

    @Getter
    public static class QueueData extends DatabaseLoadObject {

        private final Queue<QueueEntry> queue = new LinkedList<>();

        public QueueData(String uuid) {
            super(uuid);
        }

        public Map<VariantType, List<QueueEntry>> getMap() {
            Map<VariantType, List<QueueEntry>> toReturn = new HashMap<>();
            for (VariantType value : VariantType.values())
                toReturn.put(value, new ArrayList<>());
            for (QueueEntry queueEntry : queue) {
                toReturn.get(queueEntry.getVariantType()).add(queueEntry);
            }
            return toReturn;
        }
    }
}