package com.grupo6.votingapp.dtos.votings;

import com.grupo6.votingapp.models.Voting;

public class VotingNoRelationsVotesCountDTO extends VotingWithNoRelationsDTO {
    private Long votes;

    public VotingNoRelationsVotesCountDTO(Voting voting, Long votes) {
        super(voting);
        this.votes = votes;
    }

    public Long getVotes() {
        return votes;
    }

    public void setVotes(Long votescount) {
        this.votes = votescount;
    }
}