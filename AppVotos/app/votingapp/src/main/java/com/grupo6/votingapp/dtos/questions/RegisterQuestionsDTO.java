package com.grupo6.votingapp.dtos.questions;

import java.util.List;

import com.grupo6.votingapp.dtos.interfaces.DTOOneParent;
import com.grupo6.votingapp.dtos.options.RegisterOptionsDTO;
import com.grupo6.votingapp.models.Question;
import com.grupo6.votingapp.models.Voting;

public class RegisterQuestionsDTO implements DTOOneParent<Question, Voting> {
    private String description;
    private List<RegisterOptionsDTO> options;

    public RegisterQuestionsDTO() {
    }

    public RegisterQuestionsDTO(String description, List<RegisterOptionsDTO> options) {
        this.description = description;
        this.options = options;
    }

    @Override
    public Question toEntity(Voting parent) {
        Question question = new Question();
        question.setDescription(description);
        question.setOptions(options.stream().map(option -> option.toEntity(question)).toList());
        question.setVoting(parent);
        return question;
    }

    public String getDescription() {
        return description;
    }

    public List<RegisterOptionsDTO> getOptions() {
        return options;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOptions(List<RegisterOptionsDTO> options) {
        this.options = options;
    }

}
