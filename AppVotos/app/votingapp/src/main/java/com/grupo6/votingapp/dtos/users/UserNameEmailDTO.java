package com.grupo6.votingapp.dtos.users;

import com.grupo6.votingapp.models.User;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserNameEmailDTO {
    private Long id;
    private String name;
    private String email;

    public UserNameEmailDTO() {
    }

    public UserNameEmailDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
    }

    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        return user;
    }
    
}
