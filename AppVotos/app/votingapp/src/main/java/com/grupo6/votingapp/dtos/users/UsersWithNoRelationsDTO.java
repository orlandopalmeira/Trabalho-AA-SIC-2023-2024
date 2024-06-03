package com.grupo6.votingapp.dtos.users;

import java.time.LocalDate;

import com.grupo6.votingapp.models.User;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UsersWithNoRelationsDTO {
    private Long id;
    private String name;
    private String email;
    private String password;
    private LocalDate birthdate;

    public UsersWithNoRelationsDTO() {
    }

    public UsersWithNoRelationsDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.birthdate = user.getBirthdate();
    }

    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setPassword(this.password);
        user.setBirthdate(this.birthdate);
        return user;
    }
    
}
