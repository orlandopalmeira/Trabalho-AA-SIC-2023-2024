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

    //* CRUD
    public Voting saveVoting(Voting voting){//* Parece funcionar
        return votingRepository.save(voting); //* Guarda a votação na base de dados
    }

    public Voting saveVoting(Voting voting, String creator_id) {//* Parece funcionar
        User dummyUser = new User(); //* Este dummyUser serve só para a nova votação ter informação do id do seu criador.
        dummyUser.setId(Long.parseLong(creator_id)); //* mete o id do criador no objecto dummyUser
        voting.setCreator(dummyUser); //* para ser associado ao seu user criador na base de dados
        return saveVoting(voting);//* Guarda a votação na base de dados
    }

    public Voting updateVoting(Voting newVoting){ //* Parece funcionar (!!ver o TO DO!!)
        Voting oldVoting = votingRepository.findById(newVoting.getId()).orElse(null);
        if(oldVoting != null){
            if(newVoting.getTitle() != null)       oldVoting.setTitle(newVoting.getTitle());
            if(newVoting.getDescription() != null) oldVoting.setDescription(newVoting.getDescription());
            if(newVoting.getImage() != null)       oldVoting.setImage(newVoting.getImage());
            if(newVoting.getEnddate() != null)     oldVoting.setEnddate(newVoting.getEnddate());
            // TODO: se passar de privada a pública, devemos apagar esta votação da tabela M:N que associa votações privadas a votantes (utilizadores) inscritos nela (tabela private_voters)
            oldVoting.setPrivatevoting(newVoting.getPrivatevoting());
            return votingRepository.save(oldVoting);
        }
        return null;
    }

    public void deleteVoting(Long id){ //* Parece funcionar
        Voting voting = votingRepository.findById(id).orElse(null);
        if(voting != null){
            voting.removeAllPrivateVoters();
            votingRepository.delete(voting);
        }
    }

    public void deleteVoting(String id){ //* Parece funcionar
        deleteVoting(Long.parseLong(id));
    }

    public Voting getFromCreatorIdAndVotingId(String userId, String votingId){ //* Parece funcionar	
        return votingRepository.findByUserIdAndVotingId(userId, votingId).orElse(null);
    }

    public List<Voting> getVotingsFromCreatorId(String userId){ //* Parece funcionar
        return votingRepository.findByUserId(userId);
    }

    public Voting setPrivateVoters(String voting_id, List<Long> privateVotersIds){ //* Parece funcionar
        Voting votingInDB = votingRepository.findById(Long.parseLong(voting_id)).orElse(null);
        if(votingInDB != null){
            List<User> privateVoters = userRepository.findByIds(privateVotersIds); //! VER SE IMPLICA CARGA NA BD
            votingInDB.setPrivatevoters(privateVoters);
            return votingRepository.save(votingInDB);
        }
        return null;
    }
    
}
