package net.verany.lobbysystem.flagwars.voting;

import lombok.Getter;
import net.verany.api.voting.VotingWrapper;
import net.verany.lobbysystem.flagwars.round.AbstractRound;

@Getter
public class FlagWarsVoting<T> extends VotingWrapper<T> {

    private final String key;

    public FlagWarsVoting(String key, AbstractRound round) {
        this.key = key;
        round.getVotings().add(this);
    }
}
