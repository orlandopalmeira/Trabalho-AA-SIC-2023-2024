package com.grupo6.votingapp.services;

import org.springframework.stereotype.Service;

import com.grupo6.votingapp.repositories.OptionRepository;
import com.grupo6.votingapp.repositories.QuestionRepository;
import com.grupo6.votingapp.repositories.VotingRepository;

@Service
public class VotingService {
    VotingRepository votingRepository;
    QuestionRepository questionRepository;
    OptionRepository optionRepository;
}
