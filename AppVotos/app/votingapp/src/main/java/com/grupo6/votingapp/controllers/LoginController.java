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
import com.grupo6.votingapp.exceptions.authentication.UnauthorizedException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
@Transactional
@RequestMapping("/auth")
public class LoginController {
    private AuthService authService;

    //* Nomes dos campos utilizados nas diversas respostas ao utilizador
    private static final String MESSAGE_FIELD = "message";
    private static final String TOKEN_FIELD = "token";

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

    @GetMapping("/logout") //* Parece funcionar
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        response.addCookie(authService.deleteCookie());
        return ResponseEntity.ok(Map.of(MESSAGE_FIELD, "Logout successful"));
    }
}
