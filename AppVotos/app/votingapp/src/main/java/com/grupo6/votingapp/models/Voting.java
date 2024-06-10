package com.grupo6.votingapp.models;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
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
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "votings")
public class Voting implements Comparable<Voting>{
    //* Atributos
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 100)
    private String title;
    @Column(length = 500)
    private String description;
    private String image;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Lisbon") //* Para evitar erros de parsing
    private Date creationdate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Lisbon") //* Para evitar erros de parsing
    private Date enddate;
    private boolean privatevoting;
    private boolean showstats;
    private boolean showstatsrealtime;
    private boolean secretvotes;

    //* Relações
    //region Relações
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonBackReference  //* Para evitar recursividade infinita ao serializar para JSON em respostas HTTP (ver https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion)
    private User creator;

    @OneToMany(mappedBy = "voting", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Vote> votes;

    @OneToMany(mappedBy = "voting", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference //* para evitar recursividade infinita
    private List<Question> questions;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "private_voters",
        joinColumns = @JoinColumn(name = "voting_id"),
        inverseJoinColumns = @JoinColumn(name = "voter_id")
    )
    private Set<User> privatevoters;

    //endregion
    
    public void setPrivatevoters(Collection<User> privatevoters) { 
        this.privatevoters = privatevoters == null ? new HashSet<>() : new HashSet<>(privatevoters);
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
    //endregion
}