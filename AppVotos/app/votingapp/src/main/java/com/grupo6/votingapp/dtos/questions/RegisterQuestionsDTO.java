package com.grupo6.votingapp.dtos.questions;

import java.util.List;

import com.grupo6.votingapp.dtos.options.RegisterOptionsDTO;
import com.grupo6.votingapp.models.Question;
import com.grupo6.votingapp.models.Voting;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterQuestionsDTO {
    private String description;
    private List<RegisterOptionsDTO> options;

    public RegisterQuestionsDTO() {
    }

    public RegisterQuestionsDTO(String description, List<RegisterOptionsDTO> options) {
        this.description = description;
        this.options = options;
    }

    public Question toEntity(Voting parent) {
        Question question = new Question();
        question.setDescription(description);
        question.setOptions(options.stream().map(option -> option.toEntity(question)).toList());
        question.setVoting(parent);
        return question;
    }

}
