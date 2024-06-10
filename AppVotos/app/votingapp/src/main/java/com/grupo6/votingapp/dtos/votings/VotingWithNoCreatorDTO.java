package com.grupo6.votingapp.dtos.votings;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grupo6.votingapp.dtos.users.UsersWithNoRelationsDTO;
import com.grupo6.votingapp.models.Question;
import com.grupo6.votingapp.models.Voting;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class VotingWithNoCreatorDTO {
    private Long id;
    private String title;
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
    private List<Question> questions;
    private Set<UsersWithNoRelationsDTO> privatevoters;
    //* Variáveis auxiliares à UI
    private UsersWithNoRelationsDTO creator;
    private boolean useralreadyvoted;

    public VotingWithNoCreatorDTO(Voting voting){
        this.id = voting.getId();
        this.title = voting.getTitle();
        this.description = voting.getDescription();
        this.image = voting.getImage();
        this.creationdate = voting.getCreationdate();
        this.enddate = voting.getEnddate();
        this.privatevoting = voting.isPrivatevoting();
        this.showstats = voting.isShowstats();
        this.showstatsrealtime = voting.isShowstatsrealtime();
        this.secretvotes = voting.isSecretvotes();
        this.questions = voting.getQuestions();
        this.privatevoters = voting.getPrivatevoters().stream().map(UsersWithNoRelationsDTO::new).collect(Collectors.toSet());
        this.creator = new UsersWithNoRelationsDTO(voting.getCreator());
    }

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
        voting.setSecretvotes(secretvotes);
        voting.setQuestions(questions);
        voting.setPrivatevoters(privatevoters.stream().map(UsersWithNoRelationsDTO::toEntity).collect(Collectors.toSet()));
        return voting;
    }
}
