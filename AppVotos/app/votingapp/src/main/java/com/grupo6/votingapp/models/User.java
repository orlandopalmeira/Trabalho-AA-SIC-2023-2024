package com.grupo6.votingapp.models;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "creator")
    private List<Voting> votings;

    public User() {}

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Map<String,Object> claimsForJwt(){
        Map<String,Object> claims = new HashMap<>();
        claims.put("id", this.id);
        claims.put("name", this.name);
        claims.put("email", this.email);
        return claims;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public List<Voting> getVotings() {
        return votings;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setVotings(List<Voting> votings) {
        this.votings = votings;
    }

    public void updateFromUser(User user) {
        this.name = user.getName() == null ? this.name : user.getName();
        this.email = user.getEmail() == null ? this.email : user.getEmail();
        this.password = user.getPassword() == null ? this.password : user.getPassword();
    }

    @Override
    public String toString() {
        return "User [email=" + email + ", id=" + id + ", name=" + name + ", password=" + password + "]";
    }

}
