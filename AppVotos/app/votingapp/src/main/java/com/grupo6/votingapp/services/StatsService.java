package com.grupo6.votingapp.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.grupo6.votingapp.dtos.stats.CountVotesOfVoting;
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
        CountVotesOfVoting countVotesOfVoting = statsRepository.getCountVotesOfVoting(votingId).orElse(null);
        List<OptionStats> optionStats = statsRepository.getOptionStats(votingId).orElse(null);
        List<UsersWithNoRelationsDTO> users = statsRepository.getUsersOfVoting(votingId).orElse(null);
        return new VotingStatsDTO(countVotesOfVoting, optionStats, users);
    }

    public VotingStatsDTO getVotingStats(String votingId) {
        CountVotesOfVoting countVotesOfVoting = statsRepository.getCountVotesOfVoting(votingId).orElse(null);
        List<OptionStats> optionStats = statsRepository.getOptionStats(votingId).orElse(null);
        List<UsersWithNoRelationsDTO> users = statsRepository.getUsersOfVoting(votingId).orElse(null);
        return new VotingStatsDTO(countVotesOfVoting, optionStats, users);
    }
}
