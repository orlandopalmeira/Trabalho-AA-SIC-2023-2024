package com.grupo6.votingapp.models;

import java.util.Date;
import java.util.List;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "votings")
public class Voting{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = true) //* Pode optar por não ter imagem
    private String image;
    @Column(nullable = false, updatable = false)
    private Date creationdate;
    @Column(nullable = true) //* Se for NULL, é porque não tem um fim marcado
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") //* Para evitar erros de parsing
    private Date enddate;
    @Column(nullable = false)
    private boolean privatevoting;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @JsonBackReference  //* Para evitar recursividade infinita ao serializar para JSON em respostas HTTP (ver https://www.baeldung.com/jackson-bidirectional-relationships-and-infinite-recursion)
    private User creator;

    @OneToMany(mappedBy = "voting", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference //* para evitar recursividade infinita
    private List<Question> questions;

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
}