package com.grupo6.votingapp.dtos.votings;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grupo6.votingapp.dtos.questions.RegisterQuestionsDTO;
import com.grupo6.votingapp.models.Voting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterVotingDTO {
    private String title;
    private String description;
    private String image;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Lisbon")
    private Date creationdate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Lisbon")
    private Date enddate;
    private boolean privatevoting;
    private boolean showstats;
    private boolean showstatsrealtime;
    private boolean secretvotes;
    private List<RegisterQuestionsDTO> questions;
    private List<String> privateSelectedUsers;

    public RegisterVotingDTO() {
    }

    public RegisterVotingDTO(String title, String description, String image, Date creationdate, Date enddate, boolean privatevoting, List<RegisterQuestionsDTO> questions, boolean showstats, boolean showstatesrealtime, boolean secretvotes) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.creationdate = creationdate;
        this.enddate = enddate;
        this.privatevoting = privatevoting;
        this.questions = questions;
        this.showstats = showstats;
        this.showstatsrealtime = showstatesrealtime;
        this.secretvotes = secretvotes;
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
        voting.setSecretvotes(secretvotes);
        return voting;
    }

}
