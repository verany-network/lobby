package net.verany.lobbysystem.flagwars.round;

import de.dytanic.cloudnet.driver.service.ServiceInfoSnapshot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.verany.api.Verany;
import net.verany.api.interfaces.IDefault;
import net.verany.api.player.IPlayerInfo;
import net.verany.api.team.AbstractGameTeam;
import net.verany.api.team.ITeamObject;
import net.verany.api.voting.AbstractVoting;
import net.verany.lobbysystem.flagwars.Variant;
import net.verany.lobbysystem.flagwars.map.data.MapData;
import net.verany.lobbysystem.flagwars.voting.FlagWarsVoting;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public abstract class AbstractRound {
    private final String id = Verany.generateString(10);
    private final List<UUID> players;
    private final Variant variant;
    public final List<FlagWarsVoting<?>> votings = new ArrayList<>();
    private final long timestamp = System.currentTimeMillis();
    private final ITeamObject<AbstractGameTeam> teamObject;

    public final AbstractVoting<MapData> mapVoting = new FlagWarsVoting<>("map", this);
    public final AbstractVoting<Boolean> diamondVoting = new FlagWarsVoting<>("diamond", this),
            cobwebVoting = new FlagWarsVoting<>("cobweb", this),
            bowVoting = new FlagWarsVoting<>("bow", this),
            itemDropVoting = new FlagWarsVoting<>("item_drops", this),
            cooldownVoting = new FlagWarsVoting<>("cooldown", this);

    public abstract void start();

    public abstract void send();

    public abstract ServiceInfoSnapshot getTargetService();

    public abstract BukkitTask getTask();

    public List<Player> getBukkitPlayers() {
        return players.stream().map(Bukkit::getPlayer).collect(Collectors.toList());
    }

    public List<IPlayerInfo> getPlayerInfo() {
        return players.stream().map(Verany::getPlayer).collect(Collectors.toList());
    }

    public <T extends IDefault<UUID>> List<T> getPlayers(Class<T> tClass) {
        return players.stream().map(uuid -> Verany.getPlayer(uuid, tClass)).collect(Collectors.toList());
    }

}
