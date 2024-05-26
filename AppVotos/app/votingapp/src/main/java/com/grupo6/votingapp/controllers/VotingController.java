package com.grupo6.votingapp.controllers;

import java.util.Date;
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
    
    public VotingController(VotingService votingService, AuthService authService) {
        this.votingService = votingService;
        this.authService = authService;
    }

    private ResponseEntity<Object> checkTokenUserIdMatch(String token, String id, Function<String, ResponseEntity<Object>> callback){
        try {
            if(token == null || token.isEmpty() || !authService.checkTokenLight(token)){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "Invalid token: \"" + token + "\"!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            if (!authService.checkUserId(token, id)){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "The token \"" + token + "\" doesn't match userId '" + id + "'!");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            } 
            return callback.apply(id);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
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

    @GetMapping("/{id}")
    public ResponseEntity<Object> getVoting(@PathVariable String id, @CookieValue(value = "token", defaultValue = "") String token) {
        //UNTESTED: Ver se este método funciona
        //TODO: process GET /:id request
        return ResponseEntity.ok(null);
    }

    @PostMapping //* Parece funcionar
    public ResponseEntity<Object> createVote(@RequestBody Voting newVoting, @CookieValue(value = "token", defaultValue = "") String token) {
        return checkTokenSimple(token, user_id -> {
            newVoting.setCreationdate(new Date()); //* para ser marcada a data em que foi criada a votação 
            Voting registeredVoting = votingService.saveVoting(newVoting, user_id); //* Guarda a votação na base de dados 
            return ResponseEntity.ok(registeredVoting); //* Retorna a votação registada
        });
    }
    
}
