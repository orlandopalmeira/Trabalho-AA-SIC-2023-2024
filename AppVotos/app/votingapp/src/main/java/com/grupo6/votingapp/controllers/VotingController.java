package com.grupo6.votingapp.controllers;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6.votingapp.dtos.votings.VotingWithNoRelationsDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;
import com.grupo6.votingapp.dtos.votings.RegisterVotingDTO;
import com.grupo6.votingapp.dtos.votings.VotingNoRelationsVotesCountDTO;
import com.grupo6.votingapp.dtos.votings.VotingWithNoCreatorDTO;
import com.grupo6.votingapp.models.Option;
import com.grupo6.votingapp.models.Question;
import com.grupo6.votingapp.models.Voting;
import com.grupo6.votingapp.services.ImageService;
import com.grupo6.votingapp.services.VotingService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;


@AllArgsConstructor
@RestController
@Transactional
@RequestMapping("/votings")
public class VotingController {

    private VotingService votingService;
    private CheckTokenMiddlewares authMiddlewares;
    private ImageService imageService;
    private final ObjectMapper objectMapper;
    
    private static final String MESSAGE_FIELD = "message";
    private static final String NOT_FOUND_VOTING_WITH_USER_MESSAGE = "User with id '%s' does not have access to a voting with id '%s'!";

    @GetMapping //* Parece funcionar
    public ResponseEntity<Object> getVotings(@CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, user_id -> {
            List<Voting> votings = votingService.getAccessibleVotingsToUser(user_id);
            List<Long> votingIds = votings.stream().map(Voting::getId).toList(); //* ids das votações para descobrir a contagem de votos em cada uma delas
            Map<Long, Long> votesCounts = votingService.getVotesCount(votingIds);//* N.º votos por cada votação -> formato {voting_id: votes_count}
            List<VotingWithNoRelationsDTO> response = votings.stream()
                .map(voting -> {
                    Long votesCount = votesCounts.getOrDefault(voting.getId(), 0L);
                    VotingWithNoRelationsDTO votingWithNoRelationsDTO = new VotingNoRelationsVotesCountDTO(voting, votesCount);
                    boolean userAlreadyVoted = votingService.userAlreadyVoted(voting.getId(), Long.parseLong(user_id)); //! Tentar ver se dá para fazer isto numa só query.
                    votingWithNoRelationsDTO.setUseralreadyvoted(userAlreadyVoted);
                    return votingWithNoRelationsDTO;
                }).toList();
            return ResponseEntity.ok(response);
        });
    }

    @GetMapping("/user") //* Parece funcionar
    public ResponseEntity<Object> getVotingsFromUser(@CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, user_id -> {
            List<Voting> votings = votingService.getVotingsFromCreatorId(user_id);
            List<Long> votingIds = votings.stream().map(Voting::getId).toList();
            Map<Long, Long> votesCounts = votingService.getVotesCount(votingIds);
            List<VotingWithNoRelationsDTO> response = votings
                .stream()
                .map(voting -> {
                    Long votesCount = votesCounts.getOrDefault(voting.getId(), 0L);
                    VotingWithNoRelationsDTO votingWithNoRelationsDTO = new VotingNoRelationsVotesCountDTO(voting, votesCount);
                    boolean userAlreadyVoted = votingService.userAlreadyVoted(voting.getId(), Long.parseLong(user_id));
                    votingWithNoRelationsDTO.setUseralreadyvoted(userAlreadyVoted);
                    return votingWithNoRelationsDTO;
                }).toList();
            return ResponseEntity.ok(response);
        });
    }

    @GetMapping("/{voting_id}") //* Parece funcionar
    public ResponseEntity<Object> getVoting(@PathVariable String voting_id, @CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, user_id -> {
            Voting voting = votingService.getAccessibleVotingToUser(voting_id, user_id);
            if(voting == null){
                Map<String, String> error = Map.of(MESSAGE_FIELD, String.format(NOT_FOUND_VOTING_WITH_USER_MESSAGE, user_id, voting_id));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            VotingWithNoCreatorDTO response = new VotingWithNoCreatorDTO(voting);
            boolean userAlreadyVoted = votingService.userAlreadyVoted(voting.getId(), Long.parseLong(user_id));
            response.setUseralreadyvoted(userAlreadyVoted);
            response.setCreator(new UsersWithNoRelationsDTO(voting.getCreator()));
            return ResponseEntity.ok(response);
        });
    }

    private RegisterVotingDTO convertJsonToRegisterVotingDTO(String jsonString) throws IOException {
        return objectMapper.readValue(jsonString, RegisterVotingDTO.class);
    }

    @PostMapping
    public ResponseEntity<Object> createVote(
        @RequestParam("voting") String jsonString, 
        @RequestParam(value = "images", required = false) List<MultipartFile> images,
        @CookieValue(value = "token", defaultValue = "") String token
    ) {
        return authMiddlewares.checkTokenSimple(token, user_id -> {
            Map<String, String> uploadedImages = new HashMap<>();
            try {
                RegisterVotingDTO newVoting = convertJsonToRegisterVotingDTO(jsonString);
                newVoting.setCreationdate(new Date());
                Voting voting = newVoting.toEntity();
                
                if(images != null && !images.isEmpty()){
                    uploadedImages = imageService.uploadImages(images);
                    
                    voting.setImage(uploadedImages.getOrDefault(newVoting.getImage(), null));
    
                    for (Question question : voting.getQuestions()) {
                        for(Option option : question.getOptions()) {
                            option.setImage(uploadedImages.getOrDefault(option.getImage(), null));
                        }
                    }
                }

                Voting registeredVoting = votingService.saveVoting(voting, user_id);
                VotingWithNoRelationsDTO response = new VotingWithNoRelationsDTO(registeredVoting);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                e.printStackTrace();
                for(String uploadedImageName : uploadedImages.values()) {
                    imageService.deleteFile(uploadedImageName);
                }
                Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        });
    }

    @GetMapping("{voting_id}/stats")
    public ResponseEntity<Object> getVotingStats(@PathVariable String voting_id, @CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, user_id -> {
            Voting voting = votingService.getAccessibleVotingToUser(voting_id, user_id);
            if(voting == null){//* O user tem acesso à votação?
                Map<String, String> error = Map.of(MESSAGE_FIELD, String.format(NOT_FOUND_VOTING_WITH_USER_MESSAGE, user_id, voting_id));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.ok(votingService.getVotingStats(voting_id));
        });
    }
    
}
