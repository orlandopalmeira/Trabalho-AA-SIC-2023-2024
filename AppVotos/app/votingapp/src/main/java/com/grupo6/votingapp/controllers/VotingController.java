package com.grupo6.votingapp.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo6.votingapp.auth.AuthService;
import com.grupo6.votingapp.dtos.votings.VotingWithNoRelationsDTO;
import com.grupo6.votingapp.dtos.votings.VotingWithNoCreatorDTO;
import com.grupo6.votingapp.models.Voting;
import com.grupo6.votingapp.services.VotingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@Transactional
@RequestMapping("/votings")
public class VotingController {

    private AuthService authService;
    private VotingService votingService;
    
    private static final String MESSAGE_FIELD = "message";
    private static final String NOT_FOUND_VOTING_WITH_USER_MESSAGE = "User with id '%s' does not have access to a voting with id '%s'!";
    
    public VotingController(VotingService votingService, AuthService authService) {
        this.votingService = votingService;
        this.authService = authService;
    }

    private ResponseEntity<Object> checkTokenSimple(String token, Function<String, ResponseEntity<Object>> callback){
        try {
            if(token == null || token.isEmpty() || !authService.checkTokenLight(token)){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "Invalid token: \"" + token + "\"!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            return callback.apply(authService.getUserIdFromToken(token));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping //* Parece funcionar
    public ResponseEntity<Object> getVotings(@CookieValue(value = "token", defaultValue = "") String token) {
        return checkTokenSimple(token, user_id -> {
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
        return checkTokenSimple(token, user_id -> {
            List<VotingWithNoRelationsDTO> response = 
                votingService.getVotingsFromCreatorId(user_id)
                .stream()
                .map(VotingWithNoRelationsDTO::new) //* Equivalente a "voting -> new VotingWithNoRelationsDTO(voting)"
                .toList();
            return ResponseEntity.ok(response);
        });
    }

    @GetMapping("/{voting_id}") //* Parece funcionar
    public ResponseEntity<Object> getVoting(@PathVariable String voting_id, @CookieValue(value = "token", defaultValue = "") String token) {
        return checkTokenSimple(token, user_id -> {
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
    public ResponseEntity<Object> createVote(@RequestBody Voting newVoting, @CookieValue(value = "token", defaultValue = "") String token) {
        return checkTokenSimple(token, user_id -> {
            newVoting.setCreationdate(new Date());
            Voting registeredVoting = votingService.saveVoting(newVoting, user_id);
            VotingWithNoRelationsDTO response = new VotingWithNoRelationsDTO(registeredVoting);
            return ResponseEntity.ok(response);
        });
    }

    
}
