package com.grupo6.votingapp.dtos.votings;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grupo6.votingapp.models.Voting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VotingWithNoRelationsDTO {
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
    //* Variáveis auxiliares à UI
    private boolean useralreadyvoted;

    public VotingWithNoRelationsDTO(Voting voting){
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
        return voting;
    }
}
