package com.grupo6.votingapp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.grupo6.votingapp.models.Voting;

public interface VotingRepository extends JpaRepository<Voting, Long>{
    @Query("SELECT v FROM Voting v WHERE v.creator.id = :userId")
    List<Voting> findByUserId(String userId); //* Parece funcionar

    @Query("SELECT v FROM Voting v WHERE v.creator.id = :userId OR v.privatevoting = false OR :userId IN (SELECT u.id FROM v.privatevoters u)")
    List<Voting> findAccessibleVotingsToUser(String userId); //* Parece funcionar

    @Query("SELECT v FROM Voting v WHERE v.id = :votingId AND (v.creator.id = :userId OR v.privatevoting = false OR :userId IN (SELECT u.id FROM v.privatevoters u))")
    Optional<Voting> findAccessibleVotingToUser(String userId, Long votingId);

    @Query("SELECT DISTINCT v.id FROM Voting v LEFT JOIN Question q ON v.id = q.voting.id LEFT JOIN Option o ON q.id = o.question.id WHERE (v.image = :imageName OR o.image = :imageName) AND (v.creator.id = :userId OR v.privatevoting = false OR :userId IN (SELECT u.id FROM v.privatevoters u))")
    Optional<Long> votingWithImage(String userId, String imageName);
}
