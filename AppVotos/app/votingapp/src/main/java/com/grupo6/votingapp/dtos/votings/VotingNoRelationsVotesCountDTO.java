package com.grupo6.votingapp.dtos.votings;

import com.grupo6.votingapp.models.Voting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VotingNoRelationsVotesCountDTO extends VotingWithNoRelationsDTO {
    private Long votes;

    public VotingNoRelationsVotesCountDTO(Voting voting, Long votes) {
        super(voting);
        this.votes = votes;
    }

}