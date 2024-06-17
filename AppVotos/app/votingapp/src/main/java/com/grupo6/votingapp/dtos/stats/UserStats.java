package com.grupo6.votingapp.dtos.stats;

import com.grupo6.votingapp.models.User;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserStats {
    private Long id;
    private String name;
    private String email;
    private String avatar;

    public UserStats() {
    }

    public UserStats(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.avatar = user.getAvatar();
    }

    public User toEntity() {
        User user = new User();
        user.setId(this.id);
        user.setName(this.name);
        user.setEmail(this.email);
        user.setAvatar(this.avatar);
        return user;
    }
    
}
