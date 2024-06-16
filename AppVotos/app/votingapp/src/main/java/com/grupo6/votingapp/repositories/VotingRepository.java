package com.grupo6.votingapp.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.grupo6.votingapp.models.Voting;

public interface VotingRepository extends JpaRepository<Voting, Long>{
    @Query("SELECT v FROM Voting v WHERE v.creator.id = :userId")
    List<Voting> findByUserId(String userId, Sort sort); //* Parece funcionar

    @Query("SELECT v FROM Voting v WHERE (v.creator.id = :userId OR v.privatevoting = false OR :userId IN (SELECT u.id FROM v.privatevoters u)) AND v.title LIKE %:term%")
    Page<Voting> findAccessibleVotingsToUser(String userId, String term, Pageable pageable); //* Parece funcionar

    @Query("SELECT v FROM Voting v WHERE (v.creator.id = :userId OR v.privatevoting = false OR :userId IN (SELECT u.id FROM v.privatevoters u)) AND v.title LIKE %:term%")
    List<Voting> findAccessibleVotingsToUser(String userId, String term, Sort sort); //* Parece funcionar

    @Query("SELECT v FROM Voting v LEFT JOIN Vote vt ON vt.voting.id = v.id WHERE (v.creator.id = :userId OR v.privatevoting = false OR :userId IN (SELECT u.id FROM v.privatevoters u)) AND v.title LIKE %:term% GROUP BY v.id ORDER BY COUNT(vt.id) DESC")
    Page<Voting> findAccessibleVotingsToUserOrderByVotesDesc(String userId, String term, Pageable pageable); //* Parece funcionar

    @Query("SELECT v FROM Voting v LEFT JOIN Vote vt ON vt.voting.id = v.id WHERE (v.creator.id = :userId OR v.privatevoting = false OR :userId IN (SELECT u.id FROM v.privatevoters u)) AND v.title LIKE %:term% GROUP BY v.id ORDER BY COUNT(vt.id) ASC")
    Page<Voting> findAccessibleVotingsToUserOrderByVotesAsc(String userId, String term, Pageable pageable); //* Parece funcionar

    @Query("SELECT v FROM Voting v JOIN Vote vt ON vt.voting.id = v.id WHERE vt.voter.id = :userId")
    List<Voting> findUserVotingHistory(String userId, Sort sort); //* Parece funcionar    

    @Query("SELECT v FROM Voting v WHERE v.id = :votingId AND (v.creator.id = :userId OR v.privatevoting = false OR :userId IN (SELECT u.id FROM v.privatevoters u))")
    Optional<Voting> findAccessibleVotingToUser(String userId, Long votingId);

    @Query("SELECT v FROM Voting v WHERE v.id = :votingId AND v.creator.id = :userId")
    Optional<Voting> findVotingByCreatorId(Long votingId, String userId);

    @Query("SELECT DISTINCT v.id FROM Voting v LEFT JOIN Question q ON v.id = q.voting.id LEFT JOIN Option o ON q.id = o.question.id WHERE (v.image = :imageName OR o.image = :imageName) AND (v.creator.id = :userId OR v.privatevoting = false OR :userId IN (SELECT u.id FROM v.privatevoters u))")
    Optional<Long> votingWithImage(String userId, String imageName);
}
