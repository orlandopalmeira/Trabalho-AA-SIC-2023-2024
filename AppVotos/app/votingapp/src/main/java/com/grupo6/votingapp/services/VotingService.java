package com.grupo6.votingapp.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.models.Voting;
import com.grupo6.votingapp.repositories.OptionRepository;
import com.grupo6.votingapp.repositories.QuestionRepository;
import com.grupo6.votingapp.repositories.UserRepository;
import com.grupo6.votingapp.repositories.VotingRepository;

@Service
public class VotingService {
    VotingRepository votingRepository;
    QuestionRepository questionRepository;
    OptionRepository optionRepository;
    UserRepository userRepository;

    public VotingService(VotingRepository votingRepository, QuestionRepository questionRepository, OptionRepository optionRepository, UserRepository userRepository) {
        this.votingRepository = votingRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.userRepository = userRepository;
    }

    public Voting getAccessibleVotingToUser(String votingId, String userId){ //* Parece funcionar
        return votingRepository.findAccessibleVotingToUser(userId, Long.parseLong(votingId)).orElse(null);
    }

    public List<Voting> getAccessibleVotingsToUser(String userId){ //* Parece funcionar
        return votingRepository.findAccessibleVotingsToUser(userId);
    }

    public Voting saveVoting(Voting voting){//* Parece funcionar
        return votingRepository.save(voting); //* Guarda a votação na base de dados
    }

    public Voting saveVoting(Voting voting, String creator_id) {//* Parece funcionar
        User dummyUser = new User(); //* Este dummyUser serve só para a nova votação ter informação do id do seu criador.
        dummyUser.setId(Long.parseLong(creator_id)); //* mete o id do criador no objecto dummyUser
        voting.setCreator(dummyUser); //* para ser associado ao seu user criador na base de dados
        return saveVoting(voting);//* Guarda a votação na base de dados
    }

    public List<Voting> getVotingsFromCreatorId(String userId){ //* Parece funcionar
        return votingRepository.findByUserId(userId);
    }
}
