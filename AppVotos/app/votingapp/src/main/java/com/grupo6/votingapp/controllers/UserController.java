package com.grupo6.votingapp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.grupo6.votingapp.auth.AuthService;
import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.services.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@Transactional // This annotation is used to indicate that the data read from the database should be in a transactional context.
@RequestMapping("/users") //* Rotas iniciadas por "/users" são tratadas por este controlador
public class UserController {

    private UserService userService;
    private AuthService authService;

    //* Nomes dos campos utilizados nas diversas respostas ao utilizador
    private static final String MESSAGE_FIELD = "message";

    public UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    private ResponseEntity<Object> checkTokenUserIdMatch(String token, String id, Function<String, ResponseEntity<Object>> callback){
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

    private ResponseEntity<Object> checkTokenEmailMatch(String token, String email, Function<String, ResponseEntity<Object>> callback){
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

    @GetMapping //* Funciona
    public ResponseEntity<Iterable<User>> getUsers() {
        //* Retorna todos os users
        //! Rota de debug -> sem autenticação -> remover na entrega do trabalho
        return ResponseEntity.ok(userService.getUsers());
    }
    
    @GetMapping("/{id}") //* Parece funcionar
    public ResponseEntity<Object> getUserById(@PathVariable String id, @CookieValue(value = "token", defaultValue = "") String token) {
        //* Retorna o user em caso de sucesso ou uma mensagem de erro
        return checkTokenUserIdMatch(token, id, id_ -> 
            ResponseEntity.ok(new UsersWithNoRelationsDTO(userService.getUser(id_)))
        );
    }

    @GetMapping("/email/{email}") //* Parece funcionar
    public ResponseEntity<Object> getUserByEmail(@PathVariable String email, @CookieValue(value = "token", defaultValue = "") String token) {
        //* Retorna os dados do user em caso de sucesso ou uma mensagem de erro
        return checkTokenEmailMatch(token, email, email_ -> 
            ResponseEntity.ok(new UsersWithNoRelationsDTO(userService.getUserByEmail(email_)))
        );
    }

    @PostMapping //* Parece funcionar
    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        //* Regista um utilizador (não requer autenticação)
        User userInDB = userService.getUserByEmail(user.getEmail());
        if (userInDB != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(MESSAGE_FIELD, "User with email \"" + user.getEmail() + "\" already exists!"));
        }
        UsersWithNoRelationsDTO response = new UsersWithNoRelationsDTO(userService.saveUser(user));
        return ResponseEntity.ok(response);
    }
    
}
