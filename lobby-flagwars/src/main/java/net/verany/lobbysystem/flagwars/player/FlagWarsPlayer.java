package net.verany.lobbysystem.flagwars.player;

import lombok.Getter;
import lombok.Setter;
import net.verany.api.Verany;
import net.verany.api.hotbar.HotbarItem;
import net.verany.api.itembuilder.ItemBuilder;
import net.verany.api.loader.database.DatabaseLoader;
import net.verany.api.module.VeranyProject;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.player.permission.group.AbstractPermissionGroup;
import net.verany.api.player.permission.group.PlaytimeGroup;
import net.verany.api.player.stats.IStatsObject;
import net.verany.api.player.stats.StatsObject;
import net.verany.lobbysystem.flagwars.LobbyFlagWars;
import net.verany.lobbysystem.flagwars.Variant;
import net.verany.lobbysystem.flagwars.VariantType;
import net.verany.lobbysystem.flagwars.player.stats.FlagWarsStats;
import net.verany.lobbysystem.flagwars.queue.QueueEntry;
import net.verany.lobbysystem.flagwars.round.AbstractRound;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.UUID;

@Getter
@Setter
public class FlagWarsPlayer extends DatabaseLoader implements IFlagWarsPlayer {

    private UUID uniqueId;

    private Player player;
    private IPlayerInfo playerInfo;
    private IStatsObject statsObject;

    private QueueEntry request;
    private AbstractRound requestedRound = null;
    private AbstractPermissionGroup ranking;

    public FlagWarsPlayer(VeranyProject project) {
        super(project, "players", "flagwars");
    }

    @Override
    public void load(UUID uuid) {
        this.uniqueId = uuid;

        this.player = Bukkit.getPlayer(uuid);
        this.playerInfo = Verany.getPlayer(player);

        statsObject = new StatsObject(getProject(), "flagwars");
        statsObject.load(uuid);

        load(new LoadInfo<>("users", PlayerData.class, new PlayerData(uuid, new HashMap<>())));

        checkRanking();
    }

    @Override
    public void update() {
        if (request != null)
            retractRequest(request.getUuid());
        if (requestedRound != null) {
            if (requestedRound.getVariant().equals(Variant.TWOTIMESONE)) requestedRound.getTask().cancel();
            requestedRound.getPlayers().remove(uniqueId);
        }

        save("users");
    }

    @Override
    public void setItems() {
        player.getInventory().clear();

        playerInfo.setItem(0, new HotbarItem(new ItemBuilder(Material.LIGHT_BLUE_BANNER).build(), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {

            }
        });

        playerInfo.setItem(8, new HotbarItem(new ItemBuilder(Material.COMPARATOR), player) {
            @Override
            public void onInteract(PlayerInteractEvent event) {

            }
        });
    }

    @Override
    public void sendRequest(UUID target) {
        if (request != null)
            retractRequest(request.getUuid());
        request = new QueueEntry(target, VariantType.SOLO);
    }

    @Override
    public boolean hasSentRequest(UUID uuid) {
        return request != null && request.getUuid().equals(uuid);
    }

    @Override
    public boolean hasReceivedRequest(UUID uuid) {
        IFlagWarsPlayer target = Verany.getPlayer(uuid, IFlagWarsPlayer.class);
        if (target == null) return false;
        return target.hasSentRequest(uniqueId);
    }

    @Override
    public void retractRequest(UUID uuid) {
        request = null;
    }

    @Override
    public String getRankName() {
        if (ranking.equals(PlaytimeGroup.UNRANKED))
            return ChatColor.valueOf(ranking.getColor()) + ranking.getName();
        String rank = ranking.getName();
        return ChatColor.valueOf(ranking.getColor()) + rank.split("-")[0] + " " + Verany.intToRoman(Integer.parseInt(rank.split("-")[1]));
    }

    @Override
    public void checkRanking() {
        long start = LobbyFlagWars.INSTANCE.getCurrentSeasonStart();
        if (statsObject.getStatsValue(FlagWarsStats.PLAYED_GAMES, start) < 5) {
            ranking = PlaytimeGroup.UNRANKED;
            return;
        }
        ranking = /*PlaytimeGroup.getGroupByTime(statsObject.getStatsValue(FlagWarsStats.ELO, start))*/PlaytimeGroup.COPPER_2;
    }

    @Override
    public <T> T getVotingValue(String key) {
        return null;
    }

    @Override
    public <T> void setVotingValue(String key, T value) {

    }

    @Override
    public float getAverageWinChance(String map) {
        return 0;
    }
}
