package com.grupo6.votingapp.models;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class VotesQuestionsOptionsId implements Serializable {
    private Long voteid;
    private Long questionid;
    private Long optionid;


    // Constructors
    public VotesQuestionsOptionsId() {
    }

    public VotesQuestionsOptionsId(Long voteid, Long questionid, Long optionid) {
        this.voteid = voteid;
        this.questionid = questionid;
        this.optionid = optionid;
    }

    // Getters
    public Long getVoteid() {
        return voteid;
    }

    public Long getQuestionid() {
        return questionid;
    }

    public Long getOptionid() {
        return optionid;
    }

    // Setters
    public void setVoteid(Long voteId) {
        this.voteid = voteId;
    }

    public void setQuestionid(Long questionId) {
        this.questionid = questionId;
    }

    public void setOptionid(Long optionId) {
        this.optionid = optionId;
    }

    public void setVoteid(String voteId) {
        this.voteid = Long.parseLong(voteId);
    }

    public void setQuestionid(String questionId) {
        this.questionid = Long.parseLong(questionId);
    }

    public void setOptionid(String optionId) {
        this.optionid = Long.parseLong(optionId);
    }

    // Equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VotesQuestionsOptionsId that = (VotesQuestionsOptionsId) o;
        return Objects.equals(voteid, that.voteid) &&
                Objects.equals(questionid, that.questionid) &&
                Objects.equals(optionid, that.optionid);
    }

    // HashCode method
    @Override
    public int hashCode() {
        return Objects.hash(voteid, questionid, optionid);
    }

    // toString method
    @Override
    public String toString() {
        return "VotesQuestionsOptionsId{" +
                "voteid=" + voteid +
                ", questionid=" + questionid +
                ", optionid=" + optionid +
                '}';
    }

}

