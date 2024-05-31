package com.grupo6.votingapp.dtos.stats;

import java.util.List;

import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;

public class VotingStatsDTO {
    private Long numvotes;
    private List<OptionStats> optionStats;
    private List<UsersWithNoRelationsDTO> voters;

    public VotingStatsDTO(CountVotesOfVoting countVotesOfVoting, List<OptionStats> optionStats, List<UsersWithNoRelationsDTO> voters) {
        this.numvotes = countVotesOfVoting.getNumvotes();
        this.optionStats = optionStats;
        this.voters = voters;
    }

    public Long getNumvotes() {
        return numvotes;
    }

    public void setCountVotesOfVoting(Long numvotes) {
        this.numvotes = numvotes;
    }

    public List<OptionStats> getOptionStats() {
        return optionStats;
    }

    public void setOptionStats(List<OptionStats> optionStats) {
        this.optionStats = optionStats;
    }

    public List<UsersWithNoRelationsDTO> getVoters() {
        return voters;
    }

    public void setVoters(List<UsersWithNoRelationsDTO> voters) {
        this.voters = voters;
    }
}
