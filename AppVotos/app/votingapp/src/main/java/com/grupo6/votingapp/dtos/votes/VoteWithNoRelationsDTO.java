package com.grupo6.votingapp.dtos.votes;

import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;
import com.grupo6.votingapp.dtos.votings.VotingWithNoRelationsDTO;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.models.Vote;
import com.grupo6.votingapp.models.Voting;

public class VoteWithNoRelationsDTO {
    private Long id;
    private UsersWithNoRelationsDTO voter;
    private VotingWithNoRelationsDTO voting;

    public VoteWithNoRelationsDTO(Vote vote) {
        this.id = vote.getId();
        this.voter = new UsersWithNoRelationsDTO(vote.getVoter());
        this.voting = new VotingWithNoRelationsDTO(vote.getVoting());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsersWithNoRelationsDTO getVoter() {
        return voter;
    }

    public void setVoter(User voter) {
        this.voter = new UsersWithNoRelationsDTO(voter);
    }

    public VotingWithNoRelationsDTO getVoting() {
        return voting;
    }

    public void setVoting(Voting voting) {
        this.voting = new VotingWithNoRelationsDTO(voting);
    }
}
