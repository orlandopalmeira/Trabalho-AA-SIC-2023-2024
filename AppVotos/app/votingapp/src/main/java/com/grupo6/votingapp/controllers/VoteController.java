package com.grupo6.votingapp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo6.votingapp.dtos.votes.CreateVoteDTO;
import com.grupo6.votingapp.exceptions.authentication.UnauthorizedException;
import com.grupo6.votingapp.exceptions.votingservice.UserAlreadyVotedException;
import com.grupo6.votingapp.services.VotingService;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@AllArgsConstructor
@RestController
@Transactional
@RequestMapping("/votes")
public class VoteController {
    private VotingService votingService;
    private CheckTokenMiddlewares authMiddlewares;

    private static final String MESSAGE_FIELD = "message";

    @PostMapping
    public ResponseEntity<Object> createVote(@RequestBody CreateVoteDTO voteDto, @CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, userId -> {
            try {
                return ResponseEntity.ok(votingService.createVote(voteDto, userId));
            } catch (UserAlreadyVotedException e) {
                Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            } catch (UnauthorizedException e) {
                Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            } catch (Exception e) {
                e.printStackTrace();
                Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
            }
        });
    }
    
}
