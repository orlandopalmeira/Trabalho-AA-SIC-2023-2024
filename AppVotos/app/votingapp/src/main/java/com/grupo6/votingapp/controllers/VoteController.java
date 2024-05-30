package com.grupo6.votingapp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo6.votingapp.auth.AuthService;
import com.grupo6.votingapp.dtos.CreateVoteDTO;
import com.grupo6.votingapp.models.Voting;
import com.grupo6.votingapp.services.VoteService;
import com.grupo6.votingapp.services.VotingService;

import jakarta.transaction.Transactional;

import java.util.Map;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@Transactional
@RequestMapping("/votes")
public class VoteController {
    private VoteService voteService;
    private AuthService authService;
    private VotingService votingService;

    private static final String MESSAGE_FIELD = "message";

    public VoteController(VoteService voteService, AuthService authService, VotingService votingService) {
        this.voteService = voteService;
        this.authService = authService;
        this.votingService = votingService;
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

    @PostMapping
    public ResponseEntity<Object> createVote(@RequestBody CreateVoteDTO voteDto, @CookieValue(value = "token", defaultValue = "") String token) {
        return checkTokenSimple(token, userId -> {
            Voting voting = votingService.getAccessibleVotingToUser(voteDto.getVotingid(), userId);
            if(voting == null){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "Voting not found or not accessible to user!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            if(voteService.userAlreadyVoted(voting.getId(), Long.parseLong(userId))){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "User already voted in this voting!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            try {
                voteService.save(voteDto, userId); //! Objecto retornado aqui não vai na resposta HTTP porque dá um erro causado pelo lazy loading do Hibernate
                return ResponseEntity.ok(voteDto);
            } catch (Exception e) {
                e.printStackTrace();
                Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
        });
    }
    
}
