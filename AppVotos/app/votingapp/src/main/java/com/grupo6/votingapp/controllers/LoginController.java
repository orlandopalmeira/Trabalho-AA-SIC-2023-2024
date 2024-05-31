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
import com.grupo6.votingapp.auth.JwtService;
import com.grupo6.votingapp.exceptions.authentication.UnauthorizedException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@RestController
@Transactional
@RequestMapping("/auth")
public class LoginController {
    private AuthService authService;
    private JwtService jwtService;

    //* Nomes dos campos utilizados nas diversas respostas ao utilizador
    private static final String MESSAGE_FIELD = "message";
    private static final String TOKEN_FIELD = "token";

    public LoginController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    private Cookie generateCookie(String email, String password){
        String token = authService.login(email, password);
        Cookie cookie = new Cookie(TOKEN_FIELD, token);
        cookie.setHttpOnly(true); //* cookie escondido de scripts javascript no browser do cliente (utilizador)
        cookie.setSecure(true);
        cookie.setDomain("localhost"); //! CUIDADO COM ISTO SE USARMOS DOCKER
        cookie.setPath("/"); // Set the path for the cookie
        cookie.setMaxAge(24 * 60 * 60); // Set the max age for 1 day
        return cookie;
    }

    private Cookie deleteCookie(){
        Cookie cookie = new Cookie(TOKEN_FIELD, ""); //* invalida o token de sessão no cliente
        cookie.setHttpOnly(true); // Match the HttpOnly attribute
        cookie.setSecure(true); // Match the Secure attribute
        cookie.setDomain("localhost"); //! CUIDADO COM ISTO SE USARMOS DOCKER
        cookie.setPath("/"); // Match the Path attribute
        cookie.setMaxAge(0); //* maxAge=0 => apaga o cookie
        return cookie;
    }

    @PostMapping("/login") //* Parece funcionar
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials, HttpServletResponse response) {
        try {
            String email = credentials.get("email");
            String password = credentials.get("password");

            String token = authService.login(email, password);
            response.addCookie(generateCookie(email, password));
            
            String user_id = jwtService.extractUserId(token);
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
        response.addCookie(deleteCookie());
        return ResponseEntity.ok(Map.of(MESSAGE_FIELD, "Logout successful"));
    }
}
