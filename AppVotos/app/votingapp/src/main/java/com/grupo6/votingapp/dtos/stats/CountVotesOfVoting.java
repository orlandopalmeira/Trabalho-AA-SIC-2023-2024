package com.grupo6.votingapp.dtos.stats;

public class CountVotesOfVoting implements Stats{
    private Long id;
    private Long numvotes;

    public CountVotesOfVoting(Long id, Long num_votes) {
        this.id = id;
        this.numvotes = num_votes;
    }

    public Long getId() {
        return id;
    }

    public Long getNumvotes() {
        return numvotes;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNumvotes(Long num_votes) {
        this.numvotes = num_votes;
    }

}
