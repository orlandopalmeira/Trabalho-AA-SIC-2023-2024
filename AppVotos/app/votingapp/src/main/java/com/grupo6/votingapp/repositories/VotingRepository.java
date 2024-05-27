package com.grupo6.votingapp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.grupo6.votingapp.models.Voting;

public interface VotingRepository extends JpaRepository<Voting, Long>{
    @Query("SELECT v FROM Voting v WHERE v.creator.id = :userId AND v.id = :votingId")
    Optional<Voting> findByUserIdAndVotingId(String userId, String votingId); //* Parece funcionar

    @Query("SELECT v FROM Voting v WHERE v.creator.id = :userId")
    List<Voting> findByUserId(String userId); //* Parece funcionar

    @Query("SELECT v FROM Voting v WHERE v.creator.id = :userId OR v.privatevoting = false OR :userId IN (SELECT u.id FROM v.privatevoters u)")
    List<Voting> findAccessibleVotingsToUser(String userId); //* Parece funcionar
}
