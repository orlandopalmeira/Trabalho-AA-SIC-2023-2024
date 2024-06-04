package com.grupo6.votingapp.repositories;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

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

    public Map<Long,List<OptionStats>> getOptionStats(Long votingId) {
        String query = "select o.id, o.question_id, o.description, o.image, coalesce(count(vqo.option_id),0) from options o left join votes_questions_options vqo on o.id = vqo.option_id where o.id in (select o.id from questions q join options o on q.id= o.question_id where q.voting_id=?1) group by o.id";
        var result = entityManager.createNativeQuery(query)
                                  .setParameter(1, votingId)
                                  .getResultList();

        Map<Long, List<OptionStats>> optionStats = new HashMap<>(); //* formato: { questionId: [OptionStats] }
        if (!result.isEmpty()) {
            for (Object row : result) {
                Object[] r = (Object[]) row;
                Long questionId = (Long) r[1];
                optionStats.computeIfAbsent(questionId, k -> new ArrayList<>()); // Cria uma lista para a questionId caso ela não exista
                optionStats.get(questionId).add(new OptionStats((Long) r[0], (String) r[2], (String) r[3], (Long) r[4]));
            }
        }
        return optionStats;
    }

    public Map<Long,List<OptionStats>> getOptionStats(String votingId) {
        return getOptionStats(Long.parseLong(votingId));
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
