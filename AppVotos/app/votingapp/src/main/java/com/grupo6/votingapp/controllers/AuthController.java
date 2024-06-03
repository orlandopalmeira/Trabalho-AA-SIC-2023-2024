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
// import com.grupo6.votingapp.models.User;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
@Transactional
@RequestMapping("/auth")
public class AuthController {
    private AuthService authService;

    //* Nomes dos campos utilizados nas diversas respostas ao utilizador
    private static final String TOKEN_FIELD = "token";
    private static final String MESSAGE_FIELD = "message";

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Procedimento de login
     * @param response - resposta HTTP
     * @param email - email do utilizador
     * @param password - password do utilizador
     * @return Map com o token, id do utilizador e mensagem de sucesso para retornar na resposta
     */
    private Map<String, String> login_procedure(HttpServletResponse response, String email, String password){
        String token = authService.login(email, password);

        response.addCookie(authService.generateCookie(email, password));
        String user_id = authService.extractUserId(token);

        return Map.of(TOKEN_FIELD, token,
                    "id", user_id,
                    MESSAGE_FIELD, "Login successful (" + email + ")");
    }

    @PostMapping("/login") //* Parece funcionar
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            Map<String, String> result = login_procedure(response, email, password);
            // String token = authService.login(email, password);
            // response.addCookie(authService.generateCookie(email, password));
            // String user_id = authService.extractUserId(token);

            return ResponseEntity.ok(result);

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
        try{
            authService.register(user.toEntity());
            String email = user.getEmail();
            String password = user.getPassword();

            Map<String, String> result = login_procedure(response, email, password);

            return ResponseEntity.ok(result);

        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(MESSAGE_FIELD, e.getMessage()));
        } catch (Exception e) {
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
