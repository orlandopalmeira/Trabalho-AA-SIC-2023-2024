package com.grupo6.votingapp.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo6.votingapp.dtos.votings.VotingWithNoRelationsDTO;
import com.grupo6.votingapp.dtos.votings.RegisterVotingDTO;
import com.grupo6.votingapp.dtos.votings.VotingNoRelationsVotesCountDTO;
import com.grupo6.votingapp.dtos.votings.VotingWithNoCreatorDTO;
import com.grupo6.votingapp.models.Voting;
import com.grupo6.votingapp.services.StatsService;
import com.grupo6.votingapp.services.VotingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@Transactional
@RequestMapping("/votings")
public class VotingController {

    private VotingService votingService;
    private StatsService statsService;
    private CheckTokenMiddlewares authMiddlewares;
    
    private static final String MESSAGE_FIELD = "message";
    private static final String NOT_FOUND_VOTING_WITH_USER_MESSAGE = "User with id '%s' does not have access to a voting with id '%s'!";
    
    public VotingController(VotingService votingService, StatsService statsService, CheckTokenMiddlewares authMiddlewares) {
        this.votingService = votingService;
        this.statsService = statsService;
        this.authMiddlewares = authMiddlewares;
    }

    @GetMapping //* Parece funcionar
    public ResponseEntity<Object> getVotings(@CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, user_id -> {
            List<VotingWithNoRelationsDTO> response = 
                votingService.getAccessibleVotingsToUser(user_id)
                .stream()
                .map(VotingWithNoRelationsDTO::new) //* Equivalente a "voting -> new VotingWithNoRelationsDTO(voting)"
                .toList();
            return ResponseEntity.ok(response);
        });
    }

    @GetMapping("/user") //* Parece funcionar
    public ResponseEntity<Object> getVotingsFromUser(@CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, user_id -> {
            List<Voting> votings = votingService.getVotingsFromCreatorId(user_id);
            List<Long> votingIds = votings.stream().map(Voting::getId).toList();
            Map<Long, Long> votesCounts = statsService.getVotesCount(votingIds);
            List<VotingWithNoRelationsDTO> response = votings
                .stream()
                .map(voting -> {
                    Long votesCount = votesCounts.getOrDefault(voting.getId(), 0L);
                    return (VotingWithNoRelationsDTO) new VotingNoRelationsVotesCountDTO(voting, votesCount);
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
            return ResponseEntity.ok(response);
        });
    }

    @PostMapping //* Parece funcionar
    public ResponseEntity<Object> createVote(@RequestBody RegisterVotingDTO newVoting, @CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, user_id -> {
            newVoting.setCreationdate(new Date());
            Voting registeredVoting = votingService.saveVoting(newVoting.toEntity(), user_id);
            VotingWithNoRelationsDTO response = new VotingWithNoRelationsDTO(registeredVoting);
            return ResponseEntity.ok(response);
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
            return ResponseEntity.ok(statsService.getVotingStats(voting_id));
        });
    }
    
}
