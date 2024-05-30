package com.grupo6.votingapp.models;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "votings")
public class Voting implements Comparable<Voting>{
    //* Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private String image;
    private Date creationdate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") //* Para evitar erros de parsing
    private Date enddate;
    private boolean privatevoting;

    //* Relações
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonBackReference  //* Para evitar recursividade infinita ao serializar para JSON em respostas HTTP (ver https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion)
    private User creator;

    @OneToMany(mappedBy = "voting", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference //* para evitar recursividade infinita
    private List<Question> questions;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "private_voters",
        joinColumns = @JoinColumn(name = "voting_id"),
        inverseJoinColumns = @JoinColumn(name = "voter_id")
    )
    @JsonIgnore
    private Set<User> privatevoters;
    
    public Voting() {}

    public Voting(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public Date getCreationdate() {
        return creationdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public boolean getPrivatevoting() {
        return privatevoting;
    }

    public User getCreator() {
        return creator;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Set<User> getPrivatevoters() {
        return privatevoters;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setId(String id) {
        this.id = Long.parseLong(id);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public void setPrivatevoting(boolean privatevoting) {
        this.privatevoting = privatevoting;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setPrivatevoters(Collection<User> privatevoters) { 
        this.privatevoters = privatevoters == null ? new HashSet<>() : new HashSet<>(privatevoters);
        if(privatevoters != null)
            privatevoters.forEach(user -> user.addPrivateVoting(this));
    }

    //* Gestão da relação M:N com Users
    public void addPrivateVoter(User user) {
        privatevoters.add(user);
        user.getPrivatevotings().add(this);
    }

    public void removePrivateVoter(User user) {
        privatevoters.remove(user);
        user.getPrivatevotings().remove(this);
    }

    public void removeAllPrivateVoters() {
        for(User user : new HashSet<>(privatevoters)) {
            removePrivateVoter(user);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Voting voting = (Voting) obj;
        return id.equals(voting.id);
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public int compareTo(Voting voting) {
        return id.compareTo(voting.id);
    }
}