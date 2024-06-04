package com.grupo6.votingapp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo6.votingapp.dtos.votes.CreateVoteDTO;
import com.grupo6.votingapp.dtos.votes.VoteWithNoRelationsDTO;
import com.grupo6.votingapp.models.Vote;
import com.grupo6.votingapp.models.Voting;
import com.grupo6.votingapp.services.VoteService;
import com.grupo6.votingapp.services.VotingService;

import jakarta.transaction.Transactional;

import java.util.Map;

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
    private VotingService votingService;
    private CheckTokenMiddlewares authMiddlewares;

    private static final String MESSAGE_FIELD = "message";

    public VoteController(VoteService voteService, VotingService votingService, CheckTokenMiddlewares authMiddlewares) {
        this.voteService = voteService;
        this.votingService = votingService;
        this.authMiddlewares = authMiddlewares;
    }

    @PostMapping //* Parece funcionar
    public ResponseEntity<Object> createVote(@RequestBody CreateVoteDTO voteDto, @CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, userId -> {
            Voting voting = votingService.getAccessibleVotingToUser(voteDto.getVotingid(), userId);
            if(voting == null){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "Voting not found or not accessible to user!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            if(voteService.userAlreadyVoted(voting.getId(), Long.parseLong(userId))){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "User already voted in this voting!");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }

            try {
                Vote registeredVote = voteService.save(voteDto, userId);
                VoteWithNoRelationsDTO response = new VoteWithNoRelationsDTO(registeredVote);
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                e.printStackTrace();
                Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
        });
    }
    
}
