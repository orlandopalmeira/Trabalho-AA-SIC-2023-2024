package com.grupo6.votingapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo6.votingapp.models.Voting;

public interface VotingRepository extends JpaRepository<Voting, Long>{   
}
