package com.grupo6.votingapp.repositories;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.grupo6.votingapp.dtos.questions.QuestionStats;
import com.grupo6.votingapp.dtos.stats.OptionStats;
import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;

import jakarta.persistence.EntityManager;

@Repository
public class StatsRepository {
    private EntityManager entityManager;

    public StatsRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Long getCountVotesOfVoting(Long votingId) {
        String query = "select count(*) from votes where voting_id = ?1;";
        var result = entityManager.createNativeQuery(query)
                                  .setParameter(1, votingId)
                                  .getResultList();
        return (Long) result.get(0);
    }

    public Long getCountVotesOfVoting(String votingId) {
        return getCountVotesOfVoting(Long.parseLong(votingId));
    }

    public Map<Long,Long> getCountVotesOfVotings(List<Long> votingsIds) {
        String query = "select v.voting_id, count(v.voting_id) from votes v where v.voting_id in ?1 group by v.voting_id;";   
        var result = entityManager.createNativeQuery(query)
                                  .setParameter(1, votingsIds)
                                  .getResultList();
        Map<Long, Long> countVotesOfVotings = new HashMap<>(); //* formato: { votingId: number of votes }
        if (!result.isEmpty()) {
            for (Object row : result) {
                Object[] r = (Object[]) row;
                countVotesOfVotings.put((Long) r[0], (Long) r[1]);
            }
        }
        return countVotesOfVotings;
    }

    public List<QuestionStats> getQuestionStats(Long votingId) {
        String query = "SELECT o.id, o.description, o.image, COALESCE(vote_count, 0) AS vote_count, o.question_id, q.description FROM options o JOIN questions q ON o.question_id = q.id LEFT JOIN (SELECT option_id, COUNT(*) AS vote_count FROM votes_questions_options GROUP BY option_id) vqo ON o.id = vqo.option_id WHERE q.voting_id = ?1;";
        var result = entityManager.createNativeQuery(query)
                                  .setParameter(1, votingId)
                                  .getResultList();
        Map<Long, QuestionStats> questionStatsMap = new HashMap<>(); //* formato: { questionId: QuestionStats }
        if (!result.isEmpty()) {
            for (Object row : result) {
                Object[] r = (Object[]) row;
                Long optionId = (Long) r[0];
                String optionDescription = (String) r[1];
                String optionImage = (String) r[2];
                Long voteCount = (Long) r[3];
                Long questionId = (Long) r[4];
                String questionDescription = (String) r[5];

                questionStatsMap.computeIfAbsent(questionId, k -> new QuestionStats(questionId, questionDescription, new ArrayList<>())); // Cria uma QuestionStats para a questionId caso ela não exista
                questionStatsMap.get(questionId).addOption(new OptionStats(optionId, optionDescription, optionImage, voteCount));
            }
        }
        return questionStatsMap.values().stream().toList();
    }

    public List<QuestionStats> getQuestionStats(String votingId){
        return getQuestionStats(Long.parseLong(votingId));
    }

    public Optional<List<UsersWithNoRelationsDTO>> getUsersOfVoting(Long votingId) {
        String query = "select u.* from users u join votes v on u.id = v.voter_id where v.voting_id = ?1";
        var result = entityManager.createNativeQuery(query)
                                  .setParameter(1, votingId)
                                  .getResultList();
        
        if (!result.isEmpty()) {
            List<UsersWithNoRelationsDTO> users = new ArrayList<>();
            for (Object row : result) {
                Object[] r = (Object[]) row;
                UsersWithNoRelationsDTO user = new UsersWithNoRelationsDTO();
                user.setId((Long) r[0]);
                Date birthdate = (Date) r[1];
                user.setBirthdate(birthdate.toLocalDate());
                user.setEmail((String) r[2]);
                user.setName((String) r[3]);
                user.setPassword((String) r[4]);
                users.add(user);
            }
            return Optional.of(users);
        }

        return Optional.of(List.of());
    }

    public Optional<List<UsersWithNoRelationsDTO>> getUsersOfVoting(String votingId) {
        return getUsersOfVoting(Long.parseLong(votingId));
    }
}
