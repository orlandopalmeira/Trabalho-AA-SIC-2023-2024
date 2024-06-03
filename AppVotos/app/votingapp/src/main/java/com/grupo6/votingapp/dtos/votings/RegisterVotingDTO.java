package com.grupo6.votingapp.dtos.votings;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grupo6.votingapp.dtos.interfaces.DTO;
import com.grupo6.votingapp.dtos.questions.RegisterQuestionsDTO;
import com.grupo6.votingapp.models.Voting;

public class RegisterVotingDTO implements DTO<Voting> {
    private String title;
    private String description;
    private String image;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date creationdate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date enddate;
    private boolean privatevoting;
    private boolean showstats;
    private boolean showstatsrealtime;
    private List<RegisterQuestionsDTO> questions;

    public RegisterVotingDTO() {
    }

    public RegisterVotingDTO(String title, String description, String image, Date creationdate, Date enddate, boolean privatevoting, List<RegisterQuestionsDTO> questions, boolean showstats, boolean showstatesrealtime) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.creationdate = creationdate;
        this.enddate = enddate;
        this.privatevoting = privatevoting;
        this.questions = questions;
        this.showstats = showstats;
        this.showstatsrealtime = showstatesrealtime;
    }

    //region methods
    public Voting toEntity() {
        Voting voting = new Voting();
        voting.setTitle(title);
        voting.setDescription(description);
        voting.setImage(image);
        voting.setCreationdate(creationdate);
        voting.setEnddate(enddate);
        voting.setPrivatevoting(privatevoting);
        voting.setQuestions(questions.stream().map(q -> q.toEntity(voting)).toList());
        voting.setShowstats(showstats);
        voting.setShowstatsrealtime(showstatsrealtime);
        return voting;
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

    public void setShowstatsrealtime(boolean showstatesrealtime) {
        this.showstatsrealtime = showstatesrealtime;
    }

    public List<RegisterQuestionsDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<RegisterQuestionsDTO> questions) {
        this.questions = questions;
    }
    //endregion
}
