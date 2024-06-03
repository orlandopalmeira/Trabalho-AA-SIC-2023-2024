package com.grupo6.votingapp.dtos.votes;

import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;
import com.grupo6.votingapp.dtos.votings.VotingWithNoRelationsDTO;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.models.Vote;
import com.grupo6.votingapp.models.Voting;

import lombok.Getter;

@Getter
public class VoteWithNoRelationsDTO {
    private Long id;
    private UsersWithNoRelationsDTO voter;
    private VotingWithNoRelationsDTO voting;

    public VoteWithNoRelationsDTO(Vote vote) {
        this.id = vote.getId();
        this.voter = new UsersWithNoRelationsDTO(vote.getVoter());
        this.voting = new VotingWithNoRelationsDTO(vote.getVoting());
    }
    
    public void setId(Long id) {
        this.id = id;
    }

    public void setVoter(User voter) {
        this.voter = new UsersWithNoRelationsDTO(voter);
    }

    public void setVoting(Voting voting) {
        this.voting = new VotingWithNoRelationsDTO(voting);
    }
}
