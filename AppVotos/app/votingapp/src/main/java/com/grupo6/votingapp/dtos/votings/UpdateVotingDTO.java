package com.grupo6.votingapp.dtos.votings;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.grupo6.votingapp.models.Voting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateVotingDTO {
    private String title;
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Europe/Lisbon")
    private Date enddate;
    private boolean showstats;
    private boolean showstatsrealtime;
    
    public void updateVotingFromDTO(Voting voting) {
        voting.setTitle(this.title);
        voting.setDescription(this.description);
        voting.setEnddate(this.enddate);
        voting.setShowstats(this.showstats);
        voting.setShowstatsrealtime(this.showstatsrealtime);
    }
}