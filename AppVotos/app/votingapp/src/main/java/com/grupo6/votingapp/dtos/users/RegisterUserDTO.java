package com.grupo6.votingapp.dtos.users;
import java.time.LocalDate;

import com.grupo6.votingapp.models.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDTO {
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

    public User toEntity() {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setBirthdate(birthdate);
        user.setPassword(password);
        return user;
    }
    
}
