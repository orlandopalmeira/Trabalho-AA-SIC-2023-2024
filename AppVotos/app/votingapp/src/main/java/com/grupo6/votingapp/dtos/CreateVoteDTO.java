package com.grupo6.votingapp.dtos;

import java.util.List;
import java.util.Map;

public class CreateVoteDTO {
    private String votingid; //* votação que o user votou
    private Map<String,List<String>> options; //* opções que o user seleccionou em cada pergunta formato {question_id: [option_id, option_id, ...], ...}
    
    public CreateVoteDTO() {
        // Empty constructor
    }

    public String getVotingid() {
        return votingid;
    }

    public void setVotingid(String voting_id) {
        this.votingid = voting_id;
    }

    public Map<String, List<String>> getOptions() {
        return options;
    }

    public void setOptions(Map<String, List<String>> options) {
        this.options = options;
    }

    @Override
    public String toString() {
        return "VoteDTO [options=" + options.toString() + ", votingid=" + votingid + "]";
    }
    
}
