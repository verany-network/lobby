package net.verany.lobbysystem.flagwars.round;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.service.*;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.ext.bridge.player.IPlayerManager;
import de.dytanic.cloudnet.wrapper.Wrapper;
import lombok.Getter;
import lombok.SneakyThrows;
import net.verany.api.Verany;
import net.verany.api.module.VeranyPlugin;
import net.verany.api.placeholder.Placeholder;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.team.AbstractGameTeam;
import net.verany.api.team.TeamObject;
import net.verany.lobbysystem.flagwars.LobbyFlagWars;
import net.verany.lobbysystem.flagwars.Variant;
import net.verany.lobbysystem.flagwars.map.data.MapData;
import net.verany.lobbysystem.flagwars.player.FlagWarsPlayer;
import net.verany.lobbysystem.flagwars.player.IFlagWarsPlayer;
import net.verany.lobbysystem.flagwars.voting.FlagWarsVoting;
import net.verany.lobbysystem.flagwars.voting.VotingInventory;
import net.verany.volcano.round.ServerRoundData;
import org.bson.Document;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Getter
public class FlagWarsRound extends AbstractRound {

    private final VeranyPlugin plugin;

    private ServiceInfoSnapshot targetService;

    private BukkitTask task;
    private int waitingInt = 0;

    public FlagWarsRound(List<UUID> players, Variant variant, VeranyPlugin plugin) {
        super(players, variant, new TeamObject<>(variant.getTeams(), variant.getMaxPlayersInTeam()));
        this.plugin = plugin;
        targetService = null;

        mapVoting.prepare(LobbyFlagWars.INSTANCE.getMapObject().getMapsOfTheDay());
        diamondVoting.prepare(false, true);
        cobwebVoting.prepare(true, false);
        bowVoting.prepare(true, false);
        itemDropVoting.prepare(true, false);
        cooldownVoting.prepare(true, false);

        for (UUID player : getPlayers()) {
            IPlayerInfo playerInfo = Verany.getPlayer(player);
            ICloudPlayer cloudPlayer = playerInfo.getCloudPlayer();
            cloudPlayer.getProperties().remove("rejoin-time");
            CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).updateOnlinePlayer(cloudPlayer);
            playerInfo.playSound(Sound.MUSIC_NETHER_BASALT_DELTAS);
        }

        getTeamObject().loadDefaultTeams();

        LobbyFlagWars.INSTANCE.getPreparingRounds().add(this);
    }

    @Override
    public void start() {
        new VotingInventory(plugin, this, () -> {

            task = new BukkitRunnable() {
                @Override
                public void run() {
                    if (getTimestamp() + TimeUnit.MINUTES.toMillis(1) < System.currentTimeMillis()) {
                        task.cancel();
                        for (Player bukkitPlayer : getBukkitPlayers()) {
                            IPlayerInfo playerInfo = Verany.getPlayer(bukkitPlayer);
                            //bukkitPlayer.sendTitle("§7Round has been", "§astarted§8!", 0, 20 * 5, 10);
                            bukkitPlayer.stopSound(Sound.MUSIC_NETHER_BASALT_DELTAS);
                            bukkitPlayer.sendTitle(playerInfo.getKey("flagwars.round.timeout.title"), playerInfo.getKey("flagwars.round.timeout.subtitle"), 0, 20 * 5, 10);
                        }
                        LobbyFlagWars.INSTANCE.getPreparingRounds().remove(FlagWarsRound.this);
                        return;
                    }
                    StringBuilder dot = new StringBuilder();
                    dot.append("§8.".repeat(Math.max(0, waitingInt)));
                    waitingInt++;
                    if (waitingInt == 4)
                        waitingInt = 0;

                    for (Player bukkitPlayer : getBukkitPlayers()) {
                        if (bukkitPlayer == null) continue;
                        IPlayerInfo playerInfo = Verany.getPlayer(bukkitPlayer);
                        bukkitPlayer.sendTitle(playerInfo.getKey("flagwars.round.prepared.title"), playerInfo.getKey("flagwars.round.prepared.subtitle", new Placeholder("%dot%", dot.toString())), 0, 20 * 5, 10);
                    }
                }
            }.runTaskTimer(plugin, 0, 5);

            MapData map = getMapVoting().getResult();
            if (map == null)
                map = LobbyFlagWars.INSTANCE.getMapObject().getRandomMap(Variant.TWOTIMESONE);

            JsonDocument document = JsonDocument.newDocument();
            for (FlagWarsVoting<?> voting : getVotings())
                if (!voting.getKey().equals("map"))
                    document.append(voting.getKey() + "_voting", voting.getResult());
                else
                    document.append("map_voting", map);

            ServiceInfoSnapshot serviceInfoSnapshot = existRoundWithMap(map);
            if (serviceInfoSnapshot == null) {
                ServiceTask task = CloudNetDriver.getInstance().getServiceTaskProvider().getServiceTask("FW-" + getVariant().getName());
                if (task != null)
                    serviceInfoSnapshot = ServiceConfiguration.builder()
                            .task(task).autoDeleteOnStop().properties(document.append("id", getId())).build().createNewService();
            } else {
                targetService = serviceInfoSnapshot;
                send();
            }

            if (serviceInfoSnapshot != null) {
                if (!serviceInfoSnapshot.isConnected())
                    serviceInfoSnapshot.provider().start();
                this.targetService = serviceInfoSnapshot;
            }

            for (Player bukkitPlayer : getBukkitPlayers()) {
                bukkitPlayer.closeInventory();
            }
        }).run();
    }

    @Override
    public void send() {
        task.cancel();
        for (Player bukkitPlayer : getBukkitPlayers()) {
            IPlayerInfo playerInfo = Verany.getPlayer(bukkitPlayer);
            playerInfo.playSound(Sound.EVENT_RAID_HORN, 10, 1);
            bukkitPlayer.sendTitle(playerInfo.getKey("flagwars.round.started.title"), playerInfo.getKey("flagwars.round.started.subtitle"), 0, 20 * 5, 10);
        }
        CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServiceByNameAsync(targetService.getServiceId().getName()).onComplete(serviceInfoSnapshot -> {
            JsonDocument properties = serviceInfoSnapshot.getProperties();
            String roundData = properties.getString("round_data");

            List<Document> rounds = Verany.GSON.fromJson(roundData, ServerRoundData.class).documents();
            rounds.removeIf(document -> !document.containsKey("id"));
            rounds.removeIf(document -> !document.containsKey("gameState") || (document.containsKey("gameState") && !document.getString("gameState").equalsIgnoreCase("IN_GAME")));
            Document document = rounds.get(new Random().nextInt(rounds.size()));

            String roundId = document.getString("id");

            int players = getPlayers().size();

            for (int i = 0; i < players; i++) {
                IPlayerInfo veranyPlayer = Verany.getPlayer(getPlayers().get(i));

                AbstractGameTeam team = getTeamObject().getTeam(veranyPlayer.getUniqueId());
                if (team == null) {
                    team = getTeamObject().getRandomFreeTeam();
                    getTeamObject().addPlayerToTeam(veranyPlayer.getUniqueId(), team);
                }

                ICloudPlayer cloudPlayer = veranyPlayer.getCloudPlayer();
                cloudPlayer.getProperties().append("round-id", roundId);
                cloudPlayer.getProperties().append("team", team.getName());
                cloudPlayer.getProperties().append("players", players);
                cloudPlayer.getProperties().append("lastService", Wrapper.getInstance().getCurrentServiceInfoSnapshot().getServiceId().getName());
                CloudNetDriver.getInstance().getServicesRegistry().getFirstService(IPlayerManager.class).updateOnlinePlayer(cloudPlayer);

                getTeamObject().removePlayersTeam(veranyPlayer.getUniqueId());
                veranyPlayer.sendOnServer(targetService.getServiceId().getName());
                ((FlagWarsPlayer) veranyPlayer.getPlayer(IFlagWarsPlayer.class)).setRequestedRound(null);
            }
        });
    }

    @SneakyThrows
    private ServiceInfoSnapshot existRoundWithMap(MapData map) {
        Map<ServiceInfoSnapshot, List<Document>> rounds = new HashMap<>();

        for (ServiceInfoSnapshot cloudService : CloudNetDriver.getInstance().getCloudServiceProvider().getCloudServicesAsync("FW-" + map.getVariant().getName()).get()) {
            if (!cloudService.isConnected() || !cloudService.getProperties().contains("round_data")) continue;
            String roundData = cloudService.getProperties().getString("round_data");
            List<Document> documents = Verany.GSON.fromJson(roundData, ServerRoundData.class).documents();
            documents.removeIf(document -> !document.containsKey("gameState") || (document.containsKey("gameState") && !document.getString("gameState").equalsIgnoreCase("IN_GAME")));
            rounds.put(cloudService, documents);
        }

        rounds.forEach((serviceInfoSnapshot, documents) -> {
            List<Document> documentList = new ArrayList<>(documents);
            documentList.removeIf(document -> !document.containsKey("id"));
            documentList.removeIf(document -> {
                MapData mapResult = serviceInfoSnapshot.getProperties().get("map_voting", MapData.class);
                return !mapResult.getName().equals(map.getName());
            });
            rounds.put(serviceInfoSnapshot, documentList);
        });

        rounds.values().removeIf(List::isEmpty);

        return rounds.keySet().stream().findFirst().orElse(null);
    }
}