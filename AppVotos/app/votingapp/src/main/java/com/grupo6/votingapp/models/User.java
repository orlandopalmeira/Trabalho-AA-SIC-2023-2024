package com.grupo6.votingapp.models;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.util.HashMap;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements Comparable<User>{
    //* Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String avatar;

    //* Relações
    @OneToMany(mappedBy = "creator", fetch = FetchType.LAZY)
    @JsonManagedReference //* Para evitar recursividade infinita ao serializar para JSON em respostas HTTP (ver https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion)
    private List<Voting> votings;

    @ManyToMany(mappedBy = "privatevoters", fetch = FetchType.LAZY)
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
        claims.put("avatar", this.avatar != null ? this.avatar : "");
        return claims;
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

}
