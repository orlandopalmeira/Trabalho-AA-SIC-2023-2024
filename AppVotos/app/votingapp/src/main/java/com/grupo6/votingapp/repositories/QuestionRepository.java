package com.grupo6.votingapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.grupo6.votingapp.models.Question;

public interface QuestionRepository extends JpaRepository<Question, Long>{
}
