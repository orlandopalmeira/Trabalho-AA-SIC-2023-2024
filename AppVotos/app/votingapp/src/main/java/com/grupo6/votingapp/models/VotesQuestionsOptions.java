package com.grupo6.votingapp.models;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

//* Tabela que representa uma relação ternária
@Getter
@Setter
@Entity
@Table(name = "votes_questions_options")
public class VotesQuestionsOptions {
    @EmbeddedId
    private VotesQuestionsOptionsId id;

    @ManyToOne
    @MapsId("voteid")
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @ManyToOne
    @MapsId("questionid")
    @JoinColumn(name = "question_id")
    private Question question;

    @ManyToOne
    @MapsId("optionid")
    @JoinColumn(name = "option_id")
    private Option option;


    //tostring
    @Override
    public String toString() {
        return "VotesQuestionsOptions [id=" + id + ", option=" + option + ", question=" + question + ", vote=" + vote + "]";
    }

}