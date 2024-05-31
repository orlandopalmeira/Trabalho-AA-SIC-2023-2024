package com.grupo6.votingapp.auth;

import org.springframework.stereotype.Service;

import com.grupo6.votingapp.exceptions.authentication.UnauthorizedException;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.services.UserService;

@Service
public class AuthService {
    private JwtService jwtService;
    private PasswordUtil passwordUtil;
    private UserService userService;

    public AuthService(JwtService jwtService, PasswordUtil passwordUtil, UserService userService) {
        this.jwtService = jwtService;
        this.passwordUtil = passwordUtil;
        this.userService = userService;
    }

    /**
     * Verifica o user e retorna um token
     * @return um token jwt
     * @throws UnauthorizedException
     */
    public String login(User user) throws UnauthorizedException{
        if(user == null) throw new UnauthorizedException("User is null");
        User userInDB = userService.getUserByEmail(user.getEmail());
        if (userInDB == null) {
            throw new UnauthorizedException("User not found");
        }
        //* A password do user na base de dados é a "encoded"
        if (!passwordUtil.checkPassword(user.getPassword(), userInDB.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }
        return jwtService.generateToken(userInDB);
    }

    public String login(String email, String password) throws UnauthorizedException {
        if(email == null) throw new UnauthorizedException("Email is null");
        if(password == null) throw new UnauthorizedException("Password is null");

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return login(user);
    }

    public boolean checkUserId(String token, String userId) {
        if(token == null || userId == null) return false;
        String id = jwtService.extractUserId(token);
        return userId.equals(id);
    }

    public boolean checkUserId(String token, Long userId) {
        return checkIfIdHasEmail(token, Long.toString(userId));
    }

    /**
     * Verifica se o id do user dentro do token JWT corresponde ao id do user com o email fornecido
     * @param token
     * @param email
     * @return
     */
    public boolean checkIfIdHasEmail(String token, String email) {
        if(token == null) throw new UnauthorizedException("Token is null");
        if(email == null) throw new UnauthorizedException("Email is null");

        String emailFromDatabase = userService.getEmailFromUserId(jwtService.extractUserId(token));
        return emailFromDatabase != null && emailFromDatabase.equals(email);
    }

    public boolean checkToken(String token) {
        try {
            return token != null && jwtService.isTokenValid(token, userService.getUser(jwtService.extractUserId(token)));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkTokenLight(String token) {
        try {
            return token != null && jwtService.isTokenValid(token);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return jwtService.extractUserId(token);
    }
}
