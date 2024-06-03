package com.grupo6.votingapp.dtos.stats;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountVotesOfVoting{
    private Long id;
    private Long numvotes;

    public CountVotesOfVoting(Long id, Long num_votes) {
        this.id = id;
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
