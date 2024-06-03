package com.grupo6.votingapp.dtos.votes;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateVoteDTO {
    private String votingid; //* votação que o user votou
    private Map<String,List<String>> options; //* opções que o user seleccionou em cada pergunta formato {question_id: [option_id, option_id, ...], ...}
    
    public CreateVoteDTO() {
        // Empty constructor
    }

    @Override
    public String toString() {
        return "VoteDTO [options=" + options.toString() + ", votingid=" + votingid + "]";
    }
    
}
