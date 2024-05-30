package com.grupo6.votingapp.services;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.internal.util.collections.IdentitySet;
import org.springframework.stereotype.Service;

import com.grupo6.votingapp.dtos.votings.CreateVoteDTO;
import com.grupo6.votingapp.models.Option;
import com.grupo6.votingapp.models.Question;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.models.Vote;
import com.grupo6.votingapp.models.VotesQuestionsOptions;
import com.grupo6.votingapp.models.VotesQuestionsOptionsId;
import com.grupo6.votingapp.models.Voting;
import com.grupo6.votingapp.repositories.OptionRepository;
import com.grupo6.votingapp.repositories.QuestionRepository;
import com.grupo6.votingapp.repositories.UserRepository;
import com.grupo6.votingapp.repositories.VoteRepository;
import com.grupo6.votingapp.repositories.VotingRepository;

@Service
public class VoteService {
    private VoteRepository voteRepository;
    private UserRepository userRepository;
    private VotingRepository votingRepository;
    private QuestionRepository questionRepository;
    private OptionRepository optionRepository;

    public VoteService(
        VoteRepository voteRepository,
        UserRepository userRepository,
        VotingRepository votingRepository,
        QuestionRepository questionRepository,
        OptionRepository optionRepository
    ) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.votingRepository = votingRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }

    public Vote save(CreateVoteDTO voteDto, String userId) {
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
}
