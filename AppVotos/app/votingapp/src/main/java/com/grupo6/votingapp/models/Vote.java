package com.grupo6.votingapp.models;

import java.util.Set;

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
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                ", voter=" + voter +
                ", voting=" + voting +
                '}';
    }

}
