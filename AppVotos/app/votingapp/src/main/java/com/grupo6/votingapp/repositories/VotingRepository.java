package com.grupo6.votingapp.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.grupo6.votingapp.models.Voting;

public interface VotingRepository extends JpaRepository<Voting, Long>{
    //UNTESTED: Ver se este método funciona
    @Query("SELECT v FROM Voting v WHERE v.creator.id = :userId AND v.id = :votingId")
    Optional<Voting> findByUserIdAndVotingId(String userId, String votingId);   
}
