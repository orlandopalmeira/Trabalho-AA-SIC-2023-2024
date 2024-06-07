package com.grupo6.votingapp.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.hibernate.internal.util.collections.IdentitySet;
import org.springframework.stereotype.Service;

import com.grupo6.votingapp.dtos.questions.QuestionStats;
import com.grupo6.votingapp.dtos.stats.VotingStatsDTO;
import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;
import com.grupo6.votingapp.dtos.votes.CreateVoteDTO;
import com.grupo6.votingapp.models.Option;
import com.grupo6.votingapp.models.Question;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.models.Vote;
import com.grupo6.votingapp.models.VotesQuestionsOptions;
import com.grupo6.votingapp.models.VotesQuestionsOptionsId;
import com.grupo6.votingapp.models.Voting;
import com.grupo6.votingapp.repositories.OptionRepository;
import com.grupo6.votingapp.repositories.QuestionRepository;
import com.grupo6.votingapp.repositories.StatsRepository;
import com.grupo6.votingapp.repositories.UserRepository;
import com.grupo6.votingapp.repositories.VoteRepository;
import com.grupo6.votingapp.repositories.VotingRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class VotingService {
    VotingRepository votingRepository;
    VoteRepository  voteRepository;
    QuestionRepository questionRepository;
    OptionRepository optionRepository;
    UserRepository userRepository;
    StatsRepository statsRepository;
    
    //* Entidade Voting
    public Voting getAccessibleVotingToUser(String votingId, String userId){ //* Parece funcionar
        return votingRepository.findAccessibleVotingToUser(userId, Long.parseLong(votingId)).orElse(null);
    }

    public List<Voting> getAccessibleVotingsToUser(String userId){ //* Parece funcionar
        return votingRepository.findAccessibleVotingsToUser(userId);
    }

    public Voting getVotingByCreatorId(String votingId, String userId){ //* Parece funcionar
        return votingRepository.findVotingByCreatorId(Long.parseLong(votingId), userId).orElse(null);
    }

    public Voting saveVoting(Voting voting){//* Parece funcionar
        return votingRepository.save(voting); //* Guarda a votação na base de dados
    }

    public Voting saveVoting(Voting voting, String creator_id) {//* Parece funcionar
        User dummyUser = new User(); //* Este dummyUser serve só para a nova votação ter informação do id do seu criador.
        dummyUser.setId(Long.parseLong(creator_id)); //* mete o id do criador no objecto dummyUser
        voting.setCreator(dummyUser); //* para ser associado ao seu user criador na base de dados
        return saveVoting(voting);//* Guarda a votação na base de dados
    }

    public List<Voting> getVotingsFromCreatorId(String userId){ //* Parece funcionar
        return votingRepository.findByUserId(userId);
    }
    
    //* Entidade Vote
    public Vote saveVote(CreateVoteDTO voteDto, String userId) {
        Vote vote = new Vote();
        //* Associa o voto ao user que vota e à votação 
        User voter = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        vote.setVoter(voter);
        Voting voting = votingRepository.findById(Long.parseLong(voteDto.getVotingid())).orElseThrow(() -> new RuntimeException("Voting not found"));
        vote.setVoting(voting);
        //* Obtém as opções selecionadas pelo user em cada pergunta
        Map<String,List<String>> selectedOptions = voteDto.getOptions(); //* {questionId: [optionId1, optionId2], ...}
        Set<VotesQuestionsOptions> votesQuestionsOptions = new IdentitySet<>();

        //* Para cada pergunta, associa as opções selecionadas à respectiva questão e ao voto. (relação ternária)
        for (Map.Entry<String,List<String>> entry : selectedOptions.entrySet()) {
            String questionId = entry.getKey();
            List<String> optionIds = entry.getValue();
            Question question = questionRepository.findById(Long.parseLong(questionId)).orElseThrow(() -> new RuntimeException("Question not found"));
            //* Para cada opção selecionada, associa a opção à questão e ao voto
            for (String optionId : optionIds) {
                VotesQuestionsOptions voteQuestionOption = new VotesQuestionsOptions();
                voteQuestionOption.setVote(vote);
                voteQuestionOption.setQuestion(question);
                Option option = optionRepository.findById(Long.parseLong(optionId)).orElseThrow(() -> new RuntimeException("Option not found"));
                voteQuestionOption.setOption(option);
                voteQuestionOption.setId(new VotesQuestionsOptionsId(vote.getId(), question.getId(), option.getId()));
                votesQuestionsOptions.add(voteQuestionOption);
            }
        }
        vote.setVotesQuestionsOptions(votesQuestionsOptions);
        return voteRepository.save(vote);
    }

    public boolean userAlreadyVoted(Long votingId, Long userId) {
        return voteRepository.findVoteByVoterIdAndVotingId(userId, votingId).isPresent();
    }

    //* Entidade Stats
    public VotingStatsDTO getVotingStats(Long votingId) {
        Long countVotesOfVoting = statsRepository.getCountVotesOfVoting(votingId);
        List<QuestionStats> questionsStats = statsRepository.getQuestionStats(votingId);
        List<UsersWithNoRelationsDTO> users = statsRepository.getUsersOfVoting(votingId).orElse(null);
        return new VotingStatsDTO(countVotesOfVoting, questionsStats, users);
    }

    public VotingStatsDTO getVotingStats(String votingId) {
        return getVotingStats(Long.parseLong(votingId));
    }

    public Long getVotesCount(Long votingId) {
        return statsRepository.getCountVotesOfVoting(votingId);
    }

    public Map<Long,Long> getVotesCount(List<Long> votingIds) {
        return statsRepository.getCountVotesOfVotings(votingIds);
    }

    public Long votingIdWithImage(String userId, String imageName) {
        return votingRepository.votingWithImage(userId, imageName).orElse(null);
    }
}
