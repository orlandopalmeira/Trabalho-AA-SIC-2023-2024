package com.grupo6.votingapp.repositories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import com.grupo6.votingapp.dtos.stats.OptionStats;
import com.grupo6.votingapp.dtos.stats.QuestionStats;
import com.grupo6.votingapp.dtos.stats.UserSelectedOptions;
import com.grupo6.votingapp.dtos.stats.UserStats;

import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Repository
public class StatsRepository {
    private EntityManager entityManager;

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

    public List<UserStats> getUsersOfVoting(Long votingId) {
        String columns = "u.id, u.avatar, u.email, u.name";
        String query = "select " + columns + " from users u join votes v on u.id = v.voter_id where v.voting_id = ?1";
        var result = entityManager.createNativeQuery(query)
                                  .setParameter(1, votingId)
                                  .getResultList();
        
        List<UserStats> users = new ArrayList<>();
        if (!result.isEmpty()) {
            for (Object row : result) {
                Object[] r = (Object[]) row;
                UserStats user = new UserStats();
                user.setId((Long) r[0]);
                user.setAvatar((String) r[1]);
                user.setEmail((String) r[2]);
                user.setName((String) r[3]);
                users.add(user);
            }
        }

        return users;
    }

    public List<UserStats> getUsersOfVoting(String votingId) {
        return getUsersOfVoting(Long.parseLong(votingId));
    }

    public List<UserSelectedOptions> getUsersSelectedOptions(Long votingId) {
        String query = "SELECT u.id, u.name, u.email, vqo.question_id, GROUP_CONCAT(vqo.option_id) FROM users u JOIN votes v ON u.id = v.voter_id JOIN votes_questions_options vqo ON vqo.vote_id = v.id WHERE v.voting_id = ?1 GROUP BY u.id, vqo.question_id";
        String checkIfSecretQuery = "select exists (select * from votings where id=?1 and secretvotes=1)";
        var result = entityManager.createNativeQuery(query)
                                  .setParameter(1, votingId)
                                  .getResultList();
        
        boolean votingIsSecret = ((Long) entityManager.createNativeQuery(checkIfSecretQuery)
                                                        .setParameter(1, votingId)
                                                        .getSingleResult()) == 1;

        List<UserSelectedOptions> usersSelectedOptions = new ArrayList<>();
        
        if (!result.isEmpty()) {
            for (Object row : result) {
                Object[] r = (Object[]) row;
                Long userId = votingIsSecret ? null : (Long) r[0];
                String userName = votingIsSecret ? null : (String) r[1];
                String userEmail = votingIsSecret ? null : (String) r[2];
                Long questionId = (Long) r[3];
                String optionsIds = (String) r[4];

                usersSelectedOptions.add(new UserSelectedOptions(userId, userName, userEmail, questionId, parseOptionsIds(optionsIds)));
            }
        }

        return usersSelectedOptions;
    }

    public List<UserSelectedOptions> getUsersSelectedOptions(String votingId) {
        return getUsersSelectedOptions(Long.parseLong(votingId));
    }
    
    private List<Long> parseOptionsIds(String optionIds) {
        if (optionIds == null || optionIds.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(optionIds.split(","))
                     .map(Long::parseLong)
                     .collect(Collectors.toList());
    }
}
