package com.grupo6.votingapp.dtos.stats;

import java.util.List;
import java.util.Map;

import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VotingStatsDTO {
    private Long numvotes;
    private Map<Long, List<OptionStats>> optionStats;
    private List<UsersWithNoRelationsDTO> voters;

    public VotingStatsDTO(Long numVotes, Map<Long, List<OptionStats>> optionStats, List<UsersWithNoRelationsDTO> voters) {
        this.numvotes = numVotes;
        this.optionStats = optionStats;
        this.voters = voters;
    }

}
