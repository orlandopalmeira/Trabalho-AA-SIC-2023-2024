package com.grupo6.votingapp.dtos.votings;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grupo6.votingapp.dtos.interfaces.DTO;
import com.grupo6.votingapp.models.Voting;

public class VotingWithNoRelationsDTO implements DTO<Voting> {
    private Long id;
    private String title;
    private String description;
    private String image;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") //* Para evitar erros de parsing
    private Date creationdate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") //* Para evitar erros de parsing
    private Date enddate;
    private boolean privatevoting;

    public VotingWithNoRelationsDTO(Voting voting){
        this.id = voting.getId();
        this.title = voting.getTitle();
        this.description = voting.getDescription();
        this.image = voting.getImage();
        this.creationdate = voting.getCreationdate();
        this.enddate = voting.getEnddate();
        this.privatevoting = voting.getPrivatevoting();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreationdate() {
        return creationdate;
    }

    public void setCreationdate(Date creationdate) {
        this.creationdate = creationdate;
    }

    public Date getEnddate() {
        return enddate;
    }

    public void setEnddate(Date enddate) {
        this.enddate = enddate;
    }

    public boolean isPrivatevoting() {
        return privatevoting;
    }

    public void setPrivatevoting(boolean privatevoting) {
        this.privatevoting = privatevoting;
    }

    @Override
    public Voting toEntity() {
        Voting voting = new Voting();
        voting.setId(id);
        voting.setTitle(title);
        voting.setDescription(description);
        voting.setImage(image);
        voting.setCreationdate(creationdate);
        voting.setEnddate(enddate);
        voting.setPrivatevoting(privatevoting);
        return voting;
    }
}
