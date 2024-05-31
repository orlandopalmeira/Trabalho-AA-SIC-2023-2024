package com.grupo6.votingapp.repositories;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.grupo6.votingapp.dtos.stats.CountVotesOfVoting;
import com.grupo6.votingapp.dtos.stats.OptionStats;
import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;

import jakarta.persistence.EntityManager;

@Repository
public class StatsRepository {
    private EntityManager entityManager;

    public StatsRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public Optional<CountVotesOfVoting> getCountVotesOfVoting(Long votingId) {
        String query = "select vs.id, coalesce(count(v.voting_id),0) as `numvotes` from votings vs left join votes v on vs.id = v.voting_id where vs.id = ?1 group by v.voting_id";
        var result = entityManager.createNativeQuery(query)
                                  .setParameter(1, votingId)
                                  .getResultList();
        if (!result.isEmpty()) {
            Object[] row = (Object[]) result.get(0);
            return Optional.of(new CountVotesOfVoting((Long) row[0], (Long) row[1]));
        }
        return Optional.empty();
    }

    public Optional<CountVotesOfVoting> getCountVotesOfVoting(String votingId) {
        return getCountVotesOfVoting(Long.parseLong(votingId));
    }

    public Optional<List<OptionStats>> getOptionStats(Long votingId) {
        String query = "select o.id, o.description, o.image, coalesce(count(vqo.option_id),0) as `count` from options o left join votes_questions_options vqo on o.id = vqo.option_id where o.id in (select o.id from questions q join options o on q.id= o.question_id where q.voting_id=?1) group by o.id";
        var result = entityManager.createNativeQuery(query)
                                  .setParameter(1, votingId)
                                  .getResultList();

        if (!result.isEmpty()) {
            List<OptionStats> optionStats = new ArrayList<>();
            for (Object row : result) {
                Object[] r = (Object[]) row;
                optionStats.add(new OptionStats((Long) r[0], (String) r[1], (String) r[2], (Long) r[3]));
            }
            return Optional.of(optionStats);
        }

        return Optional.of(List.of());
    }

    public Optional<List<OptionStats>> getOptionStats(String votingId) {
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
