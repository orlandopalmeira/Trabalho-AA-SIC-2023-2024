package com.grupo6.votingapp.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voter_id", nullable = false)
    private User voter;
    @ManyToOne
    @JoinColumn(name = "voting_id", nullable = false)
    private Voting voting;
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    public Vote() {
    }

    public Vote(User voter, Voting voting, Option option, Question question) {
        this.voter = voter;
        this.voting = voting;
        this.option = option;
        this.question = question;
    }

    public Long getId() {
        return id;
    }

    public User getVoter() {
        return voter;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }

    public Voting getVoting() {
        return voting;
    }

    public Question getQuestion() {
        return question;
    }

    public void setVoting(Voting voting) {
        this.voting = voting;
    }

    public Option getOption() {
        return option;
    }

    public void setOption(Option option) {
        this.option = option;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", voter=" + voter +
                ", voting=" + voting +
                ", option=" + option +
                '}';
    }

}
