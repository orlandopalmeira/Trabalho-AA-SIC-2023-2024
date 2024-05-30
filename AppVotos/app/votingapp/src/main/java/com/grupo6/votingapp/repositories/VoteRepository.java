package com.grupo6.votingapp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.grupo6.votingapp.models.Vote;

public interface VoteRepository extends JpaRepository<Vote, Long>{
    @Query("SELECT v FROM Vote v WHERE v.voter.id = :voterId AND v.voting.id = :votingId")
    Optional<Vote> findVoteByVoterIdAndVotingId(Long voterId, Long votingId);
}
