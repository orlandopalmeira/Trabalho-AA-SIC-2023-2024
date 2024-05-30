package com.grupo6.votingapp.models;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voter_id")
    private User voter;
    @ManyToOne
    @JoinColumn(name = "voting_id")
    private Voting voting;
    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<VotesQuestionsOptions> votesQuestionsOptions;

    public Vote() {
    }

    public Vote(User voter, Voting voting, Set<VotesQuestionsOptions> votesQuestionsOptions) {
        this.voter = voter;
        this.voting = voting;
        this.votesQuestionsOptions = votesQuestionsOptions;
    }

    public Long getId() {
        return id;
    }

    public User getVoter() {
        return voter;
    }

    public void setVotesQuestionsOptions(Collection<VotesQuestionsOptions> votesQuestionsOptions) {
        this.votesQuestionsOptions = votesQuestionsOptions.stream().collect(Collectors.toSet());
    }

    public Set<VotesQuestionsOptions> getVotesQuestionsOptions() {
        return votesQuestionsOptions;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }

    public Voting getVoting() {
        return voting;
    }

    public void setVoting(Voting voting) {
        this.voting = voting;
    }

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", voter=" + voter +
                ", voting=" + voting +
                '}';
    }

}
