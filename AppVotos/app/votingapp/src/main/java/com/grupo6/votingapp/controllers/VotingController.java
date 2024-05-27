package com.grupo6.votingapp.controllers;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.hibernate.Hibernate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo6.votingapp.auth.AuthService;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.models.Voting;
import com.grupo6.votingapp.services.UserService;
import com.grupo6.votingapp.services.VotingService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@Transactional
@RequestMapping("/votings")
public class VotingController {

    private AuthService authService;
    private VotingService votingService;
    
    private static final String MESSAGE_FIELD = "message";
    private static final String NOT_FOUND_VOTING_MESSAGE = "Voting with id '%s' and creator_id '%s' not found!";
    
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

    @GetMapping("/{voting_id}") //* Parece funcionar
    public ResponseEntity<Object> getVoting(@PathVariable String voting_id, @CookieValue(value = "token", defaultValue = "") String token) {
        return checkTokenSimple(token, user_id -> {
            Voting voting = votingService.getFromCreatorIdAndVotingId(user_id, voting_id);
            if(voting == null){//* Não existe uma votação com id = voting_id e creator_id = user_id 
                Map<String, String> error = Map.of(MESSAGE_FIELD, String.format(NOT_FOUND_VOTING_MESSAGE, voting_id, user_id));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.ok(voting);
        });
    }

    @GetMapping("/user/{user_id}") //* Parece funcionar
    public ResponseEntity<Object> getVotingsFromUser(@PathVariable String user_id, @CookieValue(value = "token", defaultValue = "") String token) {
        // TODO: Pode ser necessário nesta rota ter query strings caso o utilizador queira alguma espécie de filtragem.
        return checkTokenSimple(token, logged_user_id -> {
            if(!logged_user_id.equals(user_id)){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "You can only see your own votings!");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            }
            return ResponseEntity.ok(votingService.getVotingsFromCreatorId(user_id));
        });
    }

    @PostMapping //* Parece funcionar
    public ResponseEntity<Object> createVote(@RequestBody Voting newVoting, @CookieValue(value = "token", defaultValue = "") String token) {
        return checkTokenSimple(token, user_id -> {
            newVoting.setCreationdate(new Date()); //* para ser marcada a data em que foi criada a votação 
            Voting registeredVoting = votingService.saveVoting(newVoting, user_id); //* Guarda a votação na base de dados 
            return ResponseEntity.ok(registeredVoting); //* Retorna a votação registada
        });
    }

    @PostMapping("/{voting_id}/privatevoters")
    public ResponseEntity<Object> addPrivateVoter(@PathVariable String voting_id, @RequestBody List<Long> privateVotersIds, @CookieValue(value = "token", defaultValue = "") String token) {
        return checkTokenSimple(token, user_id -> {
            Voting votingInDB = votingService.getFromCreatorIdAndVotingId(user_id, voting_id);
            if(votingInDB == null){//* Não existe uma votação com id = voting_id e creator_id = user_id
                Map<String, String> error = Map.of(MESSAGE_FIELD, String.format(NOT_FOUND_VOTING_MESSAGE, voting_id, user_id));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            return ResponseEntity.ok(votingService.setPrivateVoters(voting_id, privateVotersIds));
        });
    }

    @PutMapping("/{voting_id}") //* Parece funcionar
    public ResponseEntity<Object> updateVote(@PathVariable String voting_id, @RequestBody Voting updatedVoting, @CookieValue(value = "token", defaultValue = "") String token) {
        return checkTokenSimple(token, user_id -> {
            Voting votingInDB = votingService.getFromCreatorIdAndVotingId(user_id, voting_id);
            if(votingInDB == null){//* Não existe uma votação com id = voting_id e creator_id = user_id
                Map<String, String> error = Map.of(MESSAGE_FIELD, String.format(NOT_FOUND_VOTING_MESSAGE, voting_id, user_id));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            updatedVoting.setId(voting_id);
            Voting updatedVotingInDB = votingService.updateVoting(updatedVoting); //* Actualiza a votação na base de dados
            return ResponseEntity.ok(updatedVotingInDB);
        });
    }

    @DeleteMapping("/{voting_id}") //* Parece funcionar
    public ResponseEntity<Object> deleteVote(@PathVariable String voting_id, @CookieValue (value = "token", defaultValue = "") String token) {
        return checkTokenSimple(token, user_id -> {
            Voting votingInDB = votingService.getFromCreatorIdAndVotingId(user_id, voting_id);
            if(votingInDB == null){//* Não existe uma votação com id = voting_id e creator_id = user_id
                Map<String, String> error = Map.of(MESSAGE_FIELD, String.format(NOT_FOUND_VOTING_MESSAGE, voting_id, user_id));
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }
            Hibernate.initialize(votingInDB.getQuestions()); //! VER MELHOR ISTO
            Hibernate.initialize(votingInDB.getPrivatevoters()); //! VER MELHOR ISTO
            votingService.deleteVoting(votingInDB.getId());
            return ResponseEntity.ok(votingInDB);
        });
    }
    
}
