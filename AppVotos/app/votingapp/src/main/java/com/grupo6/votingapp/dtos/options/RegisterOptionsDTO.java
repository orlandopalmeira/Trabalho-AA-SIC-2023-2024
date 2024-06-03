package com.grupo6.votingapp.dtos.options;

import com.grupo6.votingapp.models.Option;
import com.grupo6.votingapp.models.Question;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterOptionsDTO {
    private String description;
    private String image;

    public RegisterOptionsDTO() {
    }

    public RegisterOptionsDTO(String description, String image) {
        this.description = description;
        this.image = image;
    }

    public Option toEntity(Question parent) {
        Option option = new Option();
        option.setDescription(description);
        option.setImage(image);
        option.setQuestion(parent);
        return option;
    }
    
}
