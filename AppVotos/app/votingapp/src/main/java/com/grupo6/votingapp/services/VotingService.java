package com.grupo6.votingapp.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.internal.util.collections.IdentitySet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6.votingapp.dtos.questions.QuestionStats;
import com.grupo6.votingapp.dtos.stats.UserSelectedOptions;
import com.grupo6.votingapp.dtos.stats.VotingStatsDTO;
import com.grupo6.votingapp.dtos.users.UserStats;
import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;
import com.grupo6.votingapp.dtos.votes.CreateVoteDTO;
import com.grupo6.votingapp.dtos.votings.RegisterVotingDTO;
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
    ImageService imageService;
    UserService userService;

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

    public Voting createVoting(RegisterVotingDTO newVoting, List<MultipartFile> images, String userId) throws Exception {
        newVoting.setCreationdate(new Date());
        Voting voting = newVoting.toEntity();

        Map<String, String> uploadedImages = new HashMap<>();

        List<String> privateVotersIds = newVoting.getPrivateSelectedUsers();
        if(newVoting.isPrivatevoting() && privateVotersIds != null && !privateVotersIds.isEmpty()) {
            List<Long> privateVotersIdsLong = privateVotersIds.stream().map(Long::parseLong).toList();
            List<User> privateVoters = userService.getUsersByIds(privateVotersIdsLong);
            voting.setPrivatevoters(privateVoters);
        }
            
        if(images != null && !images.isEmpty()){
            uploadedImages = imageService.uploadImages(images);
            voting.setImage(uploadedImages.getOrDefault(newVoting.getImage(), null));

            for (Question question : voting.getQuestions()) {
                for(Option option : question.getOptions()) {
                    option.setImage(uploadedImages.getOrDefault(option.getImage(), null));
                }
            }
        }
        // catch (Exception e) {
        //     for(String uploadedImageName : uploadedImages.values()) {
        //         imageService.deleteFile(uploadedImageName);
        //     }
        // }

        return this.saveVoting(voting, userId);

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

    public void deleteVoting(Voting voting) throws NullPointerException{
        if(voting != null) {
            //* Elimina os votantes privados associados à votação
            voting.setPrivatevoters(List.of());
            voting = votingRepository.save(voting);
            //* Elimina as imagens associadas à votação
            if (voting.getImage() != null){
                imageService.deleteFile(voting.getImage());
            }
            for (Question question : voting.getQuestions()) {
                for(Option option : question.getOptions()) {
                    if (option.getImage() != null){
                        imageService.deleteFile(option.getImage());
                    }
                }
            }
            //* Elimina os votos associados à votação
            voting.getVotes().forEach(voteRepository::delete);
            
            votingRepository.delete(voting);
        } else {
            throw new NullPointerException("Don't delete a null voting.");
        }
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

    //* Entidade VotingStatsDTO
    public VotingStatsDTO getVotingStats(Long votingId) {
        Long countVotesOfVoting = statsRepository.getCountVotesOfVoting(votingId);
        List<QuestionStats> questionsStats = statsRepository.getQuestionStats(votingId);
        List<UserStats> users = statsRepository.getUsersOfVoting(votingId);
        List<UserSelectedOptions> userSelectedOptions = statsRepository.getUsersSelectedOptions(votingId);
        return new VotingStatsDTO(countVotesOfVoting, questionsStats, users, userSelectedOptions);
    }

    public VotingStatsDTO getVotingStats(String votingId) {
        return getVotingStats(Long.parseLong(votingId));
    }

    public Map<Long,Long> getVotesCount(List<Long> votingIds) {
        return statsRepository.getCountVotesOfVotings(votingIds);
    }

    public Long votingIdWithImage(String userId, String imageName) {
        return votingRepository.votingWithImage(userId, imageName).orElse(null);
    }
}
