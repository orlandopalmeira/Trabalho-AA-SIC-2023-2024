package com.grupo6.votingapp.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public Long getVotesCount(Long votingId) {
        Optional<CountVotesOfVoting> count = statsRepository.getCountVotesOfVoting(votingId);
        return count.map(CountVotesOfVoting::getNumvotes).orElse(0L);
    }

    public Map<Long,Long> getVotesCount(List<Long> votingIds) {
        List<CountVotesOfVoting> counts = statsRepository.getCountVotesOfVotings(votingIds).orElse(List.of());
        Map<Long, Long> result = new HashMap<>();
        for (CountVotesOfVoting count : counts) {
            result.put(count.getId(), count.getNumvotes());
        }
        return result;
    }

}
