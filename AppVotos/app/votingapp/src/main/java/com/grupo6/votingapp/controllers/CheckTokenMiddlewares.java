package com.grupo6.votingapp.controllers;

import java.util.Map;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.grupo6.votingapp.auth.AuthService;

@Component
public class CheckTokenMiddlewares {
    private AuthService authService;
    //* Nomes dos campos utilizados nas diversas respostas ao utilizador
    private static final String MESSAGE_FIELD = "message";

    public CheckTokenMiddlewares(AuthService authService) {
        this.authService = authService;
    }

    public ResponseEntity<Object> checkTokenSimple(String token, Function<String, ResponseEntity<Object>> callback){
        try {
            if(token == null || token.isEmpty() || !authService.checkTokenLight(token)){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "Invalid token: \"" + token + "\"!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            return callback.apply(authService.getUserIdFromToken(token));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //! Unused method
    public ResponseEntity<Object> checkTokenUserIdMatch(String token, String id, Function<String, ResponseEntity<Object>> callback){
        try {
            if(token == null || token.isEmpty() || !authService.checkTokenLight(token)){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "Invalid token: \"" + token + "\"!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            if (!authService.checkUserId(token, id)){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "The token \"" + token + "\" doesn't match userId \"" + id + "\"!");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            } 
            return callback.apply(id);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    //! Unused method
    public ResponseEntity<Object> checkTokenEmailMatch(String token, String email, Function<String, ResponseEntity<Object>> callback){
        try {
            if(token == null || token.isEmpty() || !authService.checkTokenLight(token)){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "Invalid token: " + token + "!");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }
            if (!authService.checkIfIdHasEmail(token, email)){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "The token " + token + " doesn't match email " + email + "!");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
            } 
            return callback.apply(email);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
