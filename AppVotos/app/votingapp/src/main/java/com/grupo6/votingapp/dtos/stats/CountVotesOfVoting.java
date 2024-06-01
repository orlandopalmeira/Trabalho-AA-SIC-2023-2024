package com.grupo6.votingapp.dtos.stats;

public class CountVotesOfVoting{
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

    @Override
    public String toString() {
        return "CountVotesOfVoting [id=" + id + ", numvotes=" + numvotes + "]";
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CountVotesOfVoting other = (CountVotesOfVoting) obj;
        return id.equals(other.id) && numvotes.equals(other.numvotes);
    }


}
