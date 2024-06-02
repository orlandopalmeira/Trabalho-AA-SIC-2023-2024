package com.grupo6.votingapp.controllers;

import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.grupo6.votingapp.auth.AuthService;
import com.grupo6.votingapp.dtos.users.RegisterUserDTO;
import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.services.UserService;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@Transactional // This annotation is used to indicate that the data read from the database should be in a transactional context.
@RequestMapping("/users") //* Rotas iniciadas por "/users" são tratadas por este controlador
public class UserController {

    private UserService userService;
    private CheckTokenMiddlewares authMiddlewares;
    private AuthService authService;

    //* Nomes dos campos utilizados nas diversas respostas ao utilizador
    private static final String TOKEN_FIELD = "token";
    private static final String MESSAGE_FIELD = "message";

    public UserController(UserService userService, CheckTokenMiddlewares authMiddlewares, AuthService authService) {
        this.userService = userService;
        this.authMiddlewares = authMiddlewares;
        this.authService = authService;
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

    //! Pedro: Acho que isto deveria estar numa rota qualquer da /auth e este controlador desaparecia
    @PostMapping //* Parece funcionar
    public ResponseEntity<Object> registerUser(@RequestBody RegisterUserDTO user, HttpServletResponse response) {
        //* Regista um utilizador (não requer autenticação)
        if (userService.emailExists(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(MESSAGE_FIELD, "User with email '" + user.getEmail() + "' already exists!"));
        }
        User userToSave = user.toEntity();
        userService.saveUser(userToSave);

        String email = user.getEmail();
        String password = user.getPassword();

        String token = authService.login(email, password);
        response.addCookie(authService.generateCookie(email, password));
        
        String user_id = authService.extractUserId(token);
        return ResponseEntity.ok(Map.of(TOKEN_FIELD, token,
                                            "id", user_id,
                                            MESSAGE_FIELD, "Login successful (" + email + ")"));

        // UsersWithNoRelationsDTO response = new UsersWithNoRelationsDTO(userService.saveUser(userToSave));
        // return ResponseEntity.ok(response);
    }
    
}
