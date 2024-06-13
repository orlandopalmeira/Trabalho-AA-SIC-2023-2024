package com.grupo6.votingapp.dtos.users;

import com.grupo6.votingapp.models.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDTO {
    private String name;
    private String email;
    private String password;
    private String avatar;

    public RegisterUserDTO() {
    }

    public RegisterUserDTO(String name, String email, String password, String avatar) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
    }

    public User toEntity() {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setAvatar(avatar);
        return user;
    }
    
}
