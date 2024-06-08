package com.grupo6.votingapp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.services.UserService;
import com.grupo6.votingapp.dtos.users.UserNameEmailDTO;


@RestController
@Transactional // This annotation is used to indicate that the data read from the database should be in a transactional context.
@RequestMapping("/users") //* Rotas iniciadas por "/users" são tratadas por este controlador
public class UserController {

    private UserService userService;
    private CheckTokenMiddlewares authMiddlewares;

    public UserController(UserService userService, CheckTokenMiddlewares authMiddlewares) {
        this.userService = userService;
        this.authMiddlewares = authMiddlewares;
    }
    
    @GetMapping("/{id}") //* Parece funcionar
    public ResponseEntity<Object> getUserById(@PathVariable String id, @CookieValue(value = "token", defaultValue = "") String token) {
        //* Retorna o user em caso de sucesso ou uma mensagem de erro
        return authMiddlewares.checkTokenUserIdMatch(token, id, id_ -> 
            ResponseEntity.ok(new UsersWithNoRelationsDTO(userService.getUser(id_)))
        );
    }

    @GetMapping("/email/{email}") //* Parece funcionar
    public ResponseEntity<Object> getUserByEmail(@PathVariable String email, @CookieValue(value = "token", defaultValue = "") String token) {
        //* Retorna os dados do user em caso de sucesso ou uma mensagem de erro
        return authMiddlewares.checkTokenEmailMatch(token, email, email_ -> 
            ResponseEntity.ok(new UsersWithNoRelationsDTO(userService.getUserByEmail(email_)))
        );
    }
    
    @GetMapping //! WIP
    public ResponseEntity<Object> getUsersByterm(@RequestParam(value = "term", defaultValue = "") String term, @CookieValue(value = "token", defaultValue = "") String token) {
        //* Retorna os dados do user em caso de sucesso ou uma mensagem de erro
        return authMiddlewares.checkTokenSimple(token, userId -> {
            List<User> users = term.isBlank() ? userService.getUsers() : userService.getUsersByTerm(term);
            List<UserNameEmailDTO> dtos = users.stream().map(UserNameEmailDTO::new).toList();
            return ResponseEntity.ok(dtos);
        });
    }
}
