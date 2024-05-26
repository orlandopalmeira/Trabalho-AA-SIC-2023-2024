package com.grupo6.votingapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo6.votingapp.models.Vote;


public interface VoteRepository extends JpaRepository<Vote, Long>{
    
}
