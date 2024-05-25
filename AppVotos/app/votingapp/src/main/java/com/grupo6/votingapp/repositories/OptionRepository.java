package com.grupo6.votingapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo6.votingapp.models.Option;

public interface OptionRepository extends JpaRepository<Option, Long>{
    
}
