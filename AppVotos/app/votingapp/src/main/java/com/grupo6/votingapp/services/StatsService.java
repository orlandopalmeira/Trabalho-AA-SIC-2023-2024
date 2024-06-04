package com.grupo6.votingapp.services;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.grupo6.votingapp.dtos.stats.OptionStats;
import com.grupo6.votingapp.dtos.stats.VotingStatsDTO;
import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;
import com.grupo6.votingapp.repositories.StatsRepository;

@Service
public class StatsService {
    private StatsRepository statsRepository;

    public StatsService(StatsRepository statsRepository) {
        this.statsRepository = statsRepository;
    }

    public VotingStatsDTO getVotingStats(Long votingId) {
        Long countVotesOfVoting = statsRepository.getCountVotesOfVoting(votingId);
        Map<Long, List<OptionStats>> optionStats = statsRepository.getOptionStats(votingId);
        List<UsersWithNoRelationsDTO> users = statsRepository.getUsersOfVoting(votingId).orElse(null);
        return new VotingStatsDTO(countVotesOfVoting, optionStats, users);
    }

    public VotingStatsDTO getVotingStats(String votingId) {
        return getVotingStats(Long.parseLong(votingId));
    }

    public Long getVotesCount(Long votingId) {
        return statsRepository.getCountVotesOfVoting(votingId);
    }

    public Map<Long,Long> getVotesCount(List<Long> votingIds) {
        return statsRepository.getCountVotesOfVotings(votingIds);
    }

}
