package com.grupo6.votingapp.dtos.options;

import com.grupo6.votingapp.dtos.interfaces.DTOOneParent;
import com.grupo6.votingapp.models.Option;
import com.grupo6.votingapp.models.Question;

public class RegisterOptionsDTO implements DTOOneParent<Option, Question> {
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

    public String getDescription() {
        return description;
    }

    public String getImage() {
        return image;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(String image) {
        this.image = image;
    }
    
}
