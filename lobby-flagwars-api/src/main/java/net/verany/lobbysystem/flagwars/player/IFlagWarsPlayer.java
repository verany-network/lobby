package net.verany.lobbysystem.flagwars.player;

import lombok.Getter;
import lombok.Setter;
import net.verany.api.interfaces.IDefault;
import net.verany.api.loader.database.DatabaseLoadObject;
import net.verany.api.player.permission.group.AbstractPermissionGroup;
import net.verany.api.player.stats.IStatsObject;
import net.verany.lobbysystem.flagwars.VariantType;
import net.verany.lobbysystem.flagwars.queue.QueueEntry;
import net.verany.lobbysystem.flagwars.round.AbstractRound;
import net.verany.lobbysystem.game.scoreboard.IHubScoreboard;

import java.util.Map;
import java.util.UUID;

public interface IFlagWarsPlayer extends IDefault<UUID> {

    void setItems();

    void sendRequest(UUID target);

    boolean hasSentRequest(UUID uuid);

    boolean hasReceivedRequest(UUID uuid);

    void retractRequest(UUID uuid);

    QueueEntry getRequest();

    AbstractPermissionGroup getRanking();

    String getRankName();

    IStatsObject getStatsObject();

    void checkRanking();

    AbstractRound getRequestedRound();

    <T> T getVotingValue(String key);

    <T> void setVotingValue(String key, T value);

    float getAverageWinChance(String map);

    @Getter
    @Setter
    class PlayerData extends DatabaseLoadObject {

        private float flySpeed = 0.1F;
        private VariantType queueType = VariantType.SOLO;
        private Map<String, Object> autoVotingMap;

        public PlayerData(UUID uuid, Map<String, Object> map) {
            super(uuid);
            autoVotingMap = map;
        }

        public <T> T getVotingValue(String key) {
            if (!autoVotingMap.containsKey(key)) return null;
            return (T) autoVotingMap.get(key);
        }

        public <T> void setVotingValue(String key, T value) {
            autoVotingMap.put(key, value);
        }

    }

}
