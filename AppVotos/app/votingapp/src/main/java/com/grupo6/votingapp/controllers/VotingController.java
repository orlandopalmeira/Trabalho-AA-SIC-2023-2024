package com.grupo6.votingapp.controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.grupo6.votingapp.exceptions.authentication.UnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo6.votingapp.dtos.votings.RegisterVotingDTO;
import com.grupo6.votingapp.dtos.votings.UpdateVotingDTO;
import com.grupo6.votingapp.models.Voting;
import com.grupo6.votingapp.services.VotingService;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@AllArgsConstructor
@RestController
@Transactional
@RequestMapping("/votings")
public class VotingController {

    private VotingService votingService;
    private CheckTokenMiddlewares authMiddlewares;
    private final ObjectMapper objectMapper;
    
    private static final String MESSAGE_FIELD = "message";

    @GetMapping //* Parece funcionar
    public ResponseEntity<Object> getVotings(
        @RequestParam(value="term", required = false, defaultValue = "") String term,
        @RequestParam(value="orderBy", required = false, defaultValue = "enddate") String orderBy,
        @RequestParam(value="order", required = false, defaultValue = "asc") String order,
        @RequestParam(value="page", required = false, defaultValue = "1") int page,
        @RequestParam(value="votings_per_page", required = false, defaultValue = "8") int pageSize,
        @CookieValue(value = "token", defaultValue = "") String token
    ) {
        return authMiddlewares.checkTokenSimple(token, user_id -> {
            Map<String, Object> votings = votingService.getAccessibleVotingsToUser(user_id, term, orderBy, order, page, pageSize);
            return ResponseEntity.ok(votings);
        });
    }

    @GetMapping("/user") //* Parece funcionar
    public ResponseEntity<Object> getVotingsFromUser(
        @RequestParam(value="page", required = false, defaultValue = "1") int page,
        @RequestParam(value="votings_per_page", required = false, defaultValue = "12") int pageSize,
        @RequestParam(value="enddate_start", required = false, defaultValue = "") String enddate_start,
        @RequestParam(value="enddate_end", required = false, defaultValue = "") String enddate_end,
        @RequestParam(value="creationdate_start", required = false, defaultValue = "") String creationdate_start,
        @RequestParam(value="creationdate_end", required = false, defaultValue = "") String creationdate_end,
        @RequestParam(value="privatevoting", required = false, defaultValue = "") String privatevoting,
        @RequestParam(value="in_progress", required = false, defaultValue = "") String in_progress_filter,
        @RequestParam(value="orderBy", required = false, defaultValue = "enddate") String orderBy,
        @RequestParam(value="order", required = false, defaultValue = "asc") String order,
        @CookieValue(value = "token", defaultValue = "") String token
    ) {
        return authMiddlewares.checkTokenSimple(token, user_id -> 
            ResponseEntity.ok(votingService.getVotingsFromCreatorId(
                user_id, 
                enddate_start, enddate_end,
                creationdate_start, creationdate_end,
                privatevoting,
                in_progress_filter,
                orderBy, order,             
                page, pageSize
            ))
        );
    }

    @GetMapping("/history") //* Parece funcionar
    public ResponseEntity<Object> getVotingHistory(
        @RequestParam(value="page", required = false, defaultValue = "1") int page,
        @RequestParam(value="votings_per_page", required = false, defaultValue = "12") int pageSize,
        @RequestParam(value="enddate_start", required = false, defaultValue = "") String enddate_start,
        @RequestParam(value="enddate_end", required = false, defaultValue = "") String enddate_end,
        @RequestParam(value="creationdate_start", required = false, defaultValue = "") String creationdate_start,
        @RequestParam(value="creationdate_end", required = false, defaultValue = "") String creationdate_end,
        @RequestParam(value="privatevoting", required = false, defaultValue = "") String privatevoting,
        @RequestParam(value="in_progress", required = false, defaultValue = "") String in_progress_filter,
        @RequestParam(value="orderBy", required = false, defaultValue = "enddate") String orderBy,
        @RequestParam(value="order", required = false, defaultValue = "asc") String order,
        @CookieValue(value = "token", defaultValue = "") String token
    ) {
        return authMiddlewares.checkTokenSimple(token, user_id -> 
            ResponseEntity.ok(votingService.getUserVotingHistory(
                user_id, 
                enddate_start, enddate_end, 
                creationdate_start, creationdate_end,
                privatevoting,
                in_progress_filter,
                orderBy, order,
                page, pageSize
            ))
        );
    }

    @GetMapping("/{voting_id}") //* Parece funcionar
    public ResponseEntity<Object> getVoting(@PathVariable String voting_id, @CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, user_id -> {
            try{
                return ResponseEntity.ok(votingService.getAccessibleVotingToUser(voting_id, user_id));
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

    private RegisterVotingDTO convertJsonToRegisterVotingDTO(String jsonString) throws IOException {
        return objectMapper.readValue(jsonString, RegisterVotingDTO.class);
    }

    @PostMapping //* Parece funcionar
    public ResponseEntity<Object> createVote(
        @RequestParam("voting") String jsonString, 
        @RequestParam(value = "images", required = false) List<MultipartFile> images,
        @CookieValue(value = "token", defaultValue = "") String token
    ) {
        return authMiddlewares.checkTokenSimple(token, user_id -> {
            try {
                RegisterVotingDTO newVoting = convertJsonToRegisterVotingDTO(jsonString);
                Voting registeredVoting = votingService.createVoting(newVoting, images, user_id);
                Map<String, Object> responseMap = Map.of(
                    "id", registeredVoting.getId(),
                    MESSAGE_FIELD, "Votação criada com sucesso."
                );
                return ResponseEntity.ok(responseMap);
            } catch (Exception e) {
                e.printStackTrace();
                Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
        });
    }

    @PutMapping("/{voting_id}") //* Parece funcionar
    public ResponseEntity<Object> updateVoting(@PathVariable String voting_id, @RequestBody UpdateVotingDTO updatedVoting, @CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, userId -> {
            try {
                return ResponseEntity.ok(votingService.updateVoting(updatedVoting, voting_id, userId));
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

    @DeleteMapping("/{voting_id}") //* Parece funcionar
    public ResponseEntity<Object> deleteVoting(@PathVariable String voting_id, @CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, userId -> {
            try {
                votingService.deleteVoting(voting_id, userId);
                return ResponseEntity.ok().build();
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

    @GetMapping("{voting_id}/stats") //* Parece funcionar
    public ResponseEntity<Object> getVotingStats(@PathVariable String voting_id, @CookieValue(value = "token", defaultValue = "") String token) {
        return authMiddlewares.checkTokenSimple(token, userId -> {
            try {
                return ResponseEntity.ok(votingService.getVotingStats(userId, voting_id));
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
