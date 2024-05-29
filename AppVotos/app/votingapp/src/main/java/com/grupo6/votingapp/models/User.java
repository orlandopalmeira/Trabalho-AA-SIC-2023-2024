package com.grupo6.votingapp.models;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User implements Comparable<User>, UserDetails{
    //* Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private LocalDate birthdate;

    //* Relações
    @OneToMany(mappedBy = "creator")
    @JsonManagedReference //* Para evitar recursividade infinita ao serializar para JSON em respostas HTTP (ver https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion)
    private List<Voting> votings;

    @ManyToMany(mappedBy = "privatevoters")
    private Set<Voting> privatevotings;

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

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public List<Voting> getVotings() {
        return votings;
    }

    public Set<Voting> getPrivatevotings() {
        return privatevotings;
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

    public void setBirthdate(LocalDate birthDate) {
        this.birthdate = birthDate;
    }

    public void setVotings(List<Voting> votings) {
        this.votings = votings;
    }

    //* Gestão da relação M:N com Votings
    public void addPrivateVoting(Voting voting) {
        this.privatevotings.add(voting);
        voting.getPrivatevoters().add(this);
    }

    public void removePrivateVoting(Voting voting) {
        this.privatevotings.remove(voting);
        voting.getPrivatevoters().remove(this);
    }

    public void removeAllPrivateVotings(){
        for(Voting voting : new HashSet<>(privatevotings)){
            removePrivateVoting(voting);
        }
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj){
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        return id == other.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public int compareTo(User other) {
        return id.compareTo(other.id);
    }

    //* Métodos da interface UserDetails (Spring Security)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getUsername() {
        return getEmail();
    }

}
