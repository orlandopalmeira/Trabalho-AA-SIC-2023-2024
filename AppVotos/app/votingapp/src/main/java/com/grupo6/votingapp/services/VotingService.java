package com.grupo6.votingapp.services;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.grupo6.votingapp.models.Option;
import com.grupo6.votingapp.models.Question;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.models.Voting;
import com.grupo6.votingapp.repositories.OptionRepository;
import com.grupo6.votingapp.repositories.QuestionRepository;
import com.grupo6.votingapp.repositories.VotingRepository;

@Service
public class VotingService {
    VotingRepository votingRepository;
    QuestionRepository questionRepository;
    OptionRepository optionRepository;

    public VotingService(VotingRepository votingRepository, QuestionRepository questionRepository, OptionRepository optionRepository) {
        this.votingRepository = votingRepository;
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }

    //* CRUD
    public List<Voting> getVotings(){
        //UNTESTED: Ver se este método funciona
        return votingRepository.findAll();
    }

    public Voting getVoting(Long id){
        //UNTESTED: Ver se este método funciona
        return votingRepository.findById(id).orElse(null);
    }

    public Voting getVoting(String id){
        //UNTESTED: Ver se este método funciona
        return votingRepository.findById(Long.parseLong(id)).orElse(null);
    }

    public Voting saveVoting(Voting voting){//* Parece funcionar
        Voting votingInDB = votingRepository.save(voting); //* Guarda a votação na base de dados
        //* Guarda as questões e opções de cada questão da votação na base de dados
        for(Question question : voting.getQuestions()){
            //* Guarda a questão na base de dados, associando-a à respectiva votação
            question.setVoting(votingInDB);
            questionRepository.save(question);
            for(Option option : question.getOptions()){
                //* Guarda a opção (de uma questão) na base de dados, associando-a à respectiva questão
                option.setQuestion(question);
                optionRepository.save(option);
            }
        }
        return votingInDB;
    }

    public Voting saveVoting(Voting voting, String creator_id) {//* Parece funcionar
        User dummyUser = new User(); //* Este dummyUser serve só para a nova votação ter informação do id do seu criador.
        dummyUser.setId(Long.parseLong(creator_id)); //* mete o id do criador no objecto dummyUser
        voting.setCreator(dummyUser); //* para ser associado ao seu user criador na base de dados
        return saveVoting(voting);//* Guarda a votação na base de dados
    }

    public Voting updateVoting(Voting voting){
        //! TODO: Implementar isto se for realmente necessário
        throw new UnsupportedOperationException("Not implemented yet");
    }

    public void deleteVoting(Long id){
        //UNTESTED: Ver se este método funciona
        votingRepository.deleteById(id);
    }

    public void deleteVoting(String id){
        //UNTESTED: Ver se este método funciona
        votingRepository.deleteById(Long.parseLong(id));
    }

    public Voting getFromCreatorIdAndVotingId(String userId, String votingId){
        //UNTESTED: Ver se este método funciona
        return votingRepository.findByUserIdAndVotingId(userId, votingId).orElse(null);
    }
}
