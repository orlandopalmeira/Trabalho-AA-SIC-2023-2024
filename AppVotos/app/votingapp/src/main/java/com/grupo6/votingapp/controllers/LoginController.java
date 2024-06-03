package com.grupo6.votingapp.controllers;

import java.util.Arrays;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.grupo6.votingapp.auth.AuthService;
import com.grupo6.votingapp.dtos.users.RegisterUserDTO;
import com.grupo6.votingapp.exceptions.authentication.UnauthorizedException;
import com.grupo6.votingapp.models.User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
@Transactional
@RequestMapping("/auth")
public class LoginController {
    private AuthService authService;

    //* Nomes dos campos utilizados nas diversas respostas ao utilizador
    private static final String TOKEN_FIELD = "token";
    private static final String MESSAGE_FIELD = "message";

    public LoginController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login") //* Parece funcionar
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            String token = authService.login(email, password);

            response.addCookie(authService.generateCookie(email, password));
            String user_id = authService.extractUserId(token);
            return ResponseEntity.ok(Map.of(TOKEN_FIELD, token,
                                            "id", user_id,
                                            MESSAGE_FIELD, "Login successful (" + email + ")"));
        } catch (UnauthorizedException e) { //* user não existe ou password errada
            Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) { //* outros erros
            Map<String, String> error = Map.of(MESSAGE_FIELD, Arrays.toString(e.getStackTrace()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/register") //* Parece funcionar
    public ResponseEntity<Object> registerUser(@RequestBody RegisterUserDTO user, HttpServletResponse response) {
        //* Regista um utilizador (não requer autenticação)
        // if (authService.emailExists(user.getEmail())) {
        //     return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(MESSAGE_FIELD, "User with email '" + user.getEmail() + "' already exists!"));
        // }
        // User userToSave = user.toEntity();
        // authService.saveUser(userToSave);
        try{
            authService.register(user.toEntity());
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(MESSAGE_FIELD, e.getMessage()));
        } catch (Exception e) {
            Map<String, String> error = Map.of(MESSAGE_FIELD, Arrays.toString(e.getStackTrace()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

        //! MODULARIZAR ESTE PROCESSO DE LOGIN PARA QUE AO FAZER LOGIN SIMPLES E AO REGISTAR, AS MESMAS COISAS SEJAM FEITAS AUTOMATICAMENTNE
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

    @GetMapping("/logout") //* Parece funcionar
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        response.addCookie(authService.deleteCookie());
        return ResponseEntity.ok(Map.of(MESSAGE_FIELD, "Logout successful"));
    }
}
