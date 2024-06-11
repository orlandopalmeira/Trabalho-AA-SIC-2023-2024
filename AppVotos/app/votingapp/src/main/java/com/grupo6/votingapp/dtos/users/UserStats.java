package com.grupo6.votingapp.dtos.users;

import java.time.LocalDate;

import com.grupo6.votingapp.models.User;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserStats {
    private Long id;
    private String name;
    private String email;
    private LocalDate birthdate;

    public UserStats() {
    }

    public UserStats(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.birthdate = user.getBirthdate();
    }

    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setBirthdate(this.birthdate);
        return user;
    }
    
}