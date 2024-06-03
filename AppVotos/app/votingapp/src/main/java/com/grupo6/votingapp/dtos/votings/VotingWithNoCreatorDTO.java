package com.grupo6.votingapp.dtos.votings;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grupo6.votingapp.dtos.interfaces.DTO;
import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;
import com.grupo6.votingapp.models.Question;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.models.Voting;

public class VotingWithNoCreatorDTO implements DTO<Voting> {
    private Long id;
    private String title;
    private String description;
    private String image;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") //* Para evitar erros de parsing
    private Date creationdate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC") //* Para evitar erros de parsing
    private Date enddate;
    private boolean privatevoting;
    private boolean showstats;
    private boolean showstatsrealtime;
    private List<Question> questions;
    private Set<UsersWithNoRelationsDTO> privatevoters;

    public VotingWithNoCreatorDTO(Voting voting){
        this.id = voting.getId();
        this.title = voting.getTitle();
        this.description = voting.getDescription();
        this.image = voting.getImage();
        this.creationdate = voting.getCreationdate();
        this.enddate = voting.getEnddate();
        this.privatevoting = voting.getPrivatevoting();
        this.showstats = voting.getShowstats();
        this.showstatsrealtime = voting.getShowstatsrealtime();
        this.questions = voting.getQuestions();
        this.privatevoters = voting.getPrivatevoters().stream().map(UsersWithNoRelationsDTO::new).collect(Collectors.toSet());
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

    public boolean getShowstats() {
        return showstats;
    }

    public void setShowstats(boolean showstats) {
        this.showstats = showstats;
    }

    public boolean getShowstatsrealtime() {
        return showstatsrealtime;
    }

    public void setShowstatsrealtime(boolean showstatsrealtime) {
        this.showstatsrealtime = showstatsrealtime;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Set<UsersWithNoRelationsDTO> getPrivatevoters() {
        return privatevoters;
    }

    public void setPrivatevoters(Set<User> privatevoters) {
        this.privatevoters = privatevoters.stream().map(UsersWithNoRelationsDTO::new).collect(Collectors.toSet());
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
        voting.setShowstats(showstats);
        voting.setShowstatsrealtime(showstatsrealtime);
        voting.setQuestions(questions);
        voting.setPrivatevoters(privatevoters.stream().map(UsersWithNoRelationsDTO::toEntity).collect(Collectors.toSet()));
        return voting;
    }
}
