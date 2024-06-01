package com.grupo6.votingapp.dtos.users;

import java.time.LocalDate;

import com.grupo6.votingapp.dtos.interfaces.DTO;
import com.grupo6.votingapp.models.User;

public class UsersWithNoRelationsDTO implements DTO<User> {
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

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    @Override
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
