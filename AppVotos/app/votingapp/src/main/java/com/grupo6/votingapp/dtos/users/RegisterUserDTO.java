package com.grupo6.votingapp.dtos.users;
import java.time.LocalDate;

import com.grupo6.votingapp.dtos.interfaces.DTO;
import com.grupo6.votingapp.models.User;

public class RegisterUserDTO implements DTO<User>{
    private String name;
    private String email;
    private LocalDate birthdate;
    private String password;

    public RegisterUserDTO() {
    }

    public RegisterUserDTO(String name, String email, LocalDate birthdate, String password) {
        this.name = name;
        this.email = email;
        this.birthdate = birthdate;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public User toEntity() {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setBirthdate(birthdate);
        user.setPassword(password);
        return user;
    }
    
}
