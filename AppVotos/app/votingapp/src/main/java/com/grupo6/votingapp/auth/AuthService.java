package com.grupo6.votingapp.auth;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.grupo6.votingapp.exceptions.authentication.UnauthorizedException;
import com.grupo6.votingapp.models.User;

import jakarta.servlet.http.Cookie;

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

    /**
     * Regista um user, verificando se o email já existe. Se já existir, lança UnauthorizedException com uma mensagem descritiva do erro.
     * @param user
     * @return Retorna a instância de User registado
     * @throws UnauthorizedException
     */
    public User register(User user) throws UnauthorizedException{
        // Verific
        String email = user.getEmail();
        if (userService.emailExists(email)) {
            throw new UnauthorizedException("User with email '\" + user.getEmail() + \"' already exists!");
            // return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(MESSAGE_FIELD, "User with email '" + user.getEmail() + "' already exists!"));
        }
        return userService.saveUser(user);

    }

    public User register(String name, String email, LocalDate birthDate, String password) throws UnauthorizedException {
        if(name == null) throw new UnauthorizedException("Name is null");
        if(email == null) throw new UnauthorizedException("Email is null");
        if(birthDate == null) throw new UnauthorizedException("BirthDate is null");
        if(password == null) throw new UnauthorizedException("Password is null");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setBirthdate(birthDate);
        user.setPassword(password);
        return register(user);
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

    public String extractUserId(String token) {
        return jwtService.extractUserId(token);
    }

    public Cookie generateCookie(String email, String password){
        String token = login(email, password);
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true); //* cookie escondido de scripts javascript no browser do cliente (utilizador)
        cookie.setSecure(true);
        cookie.setDomain("localhost"); //! CUIDADO COM ISTO SE USARMOS DOCKER
        cookie.setPath("/"); // Set the path for the cookie
        cookie.setMaxAge(24 * 60 * 60); // Set the max age for 1 day
        return cookie;
    }

    public Cookie deleteCookie(){
        Cookie cookie = new Cookie("token", ""); //* invalida o token de sessão no cliente
        cookie.setHttpOnly(true); // Match the HttpOnly attribute
        cookie.setSecure(true); // Match the Secure attribute
        cookie.setDomain("localhost"); //! CUIDADO COM ISTO SE USARMOS DOCKER
        cookie.setPath("/"); // Match the Path attribute
        cookie.setMaxAge(0); //* maxAge=0 => apaga o cookie
        return cookie;
    }
}
