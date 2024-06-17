package com.grupo6.votingapp.dtos.stats;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class QuestionStats {
    private Long id;
    private String description;
    private List<OptionStats> options;

    public void addOption(OptionStats option) {
        options.add(option);
    }

    @Override
    public int hashCode() { 
        return Objects.hash(id); 
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        QuestionStats question = (QuestionStats) obj;
        return Objects.equals(id, question.id) && Objects.equals(description, question.description);
    }

}
