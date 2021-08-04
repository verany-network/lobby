package net.verany.lobbysystem.flagwars.queue;

import net.verany.api.player.IPlayerInfo;

import java.util.List;
import java.util.Queue;
import java.util.UUID;

public interface IQueueObject {

    void updateQueue();

    Queue<QueueEntry> getPlayersInQueue();

    boolean isInQueue(UUID uuid);

    QueueEntry getQueueEntry(UUID uuid);

    void joinQueue(QueueEntry entry);

    void leaveQueue(UUID uuid);

    void request(IPlayerInfo player, IPlayerInfo target);

    void startTask();

}
