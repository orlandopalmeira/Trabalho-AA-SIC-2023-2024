package com.grupo6.votingapp.services;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.repositories.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(
        UserRepository userRepository
    ){
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public List<User> getUsers(){ //* Parece funcionar
        return userRepository.findAll();
    }

    public List<User> getUsersByTerm(String term){ //! WIP
        return userRepository.findByTerm(term).orElse(null);
    }

    public User getUser(String id){ //* Parece funcionar
        return userRepository.findById(Long.parseLong(id)).orElse(null);
    }

    public List<User> getUsersByIds(List<Long> ids){
        return userRepository.findUsersByIds(ids);
    }

    public User getUserByEmail(String email){ //* Parece funcionar
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the entity instance completely.
     * A password é encriptada antes de ser guardada (ter isso em atenção)
     * @param user
     * @return the saved entity
     */
    public User saveUser(User user){ //* Parece funcionar
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String getEmailFromUserId(String id){ //* Parece funcionar
        return userRepository.findEmailFromUserId(id).orElse(null);
    }

    /**
     * Verifica se um email já existe na base de dados
     * @param email
     * @return true se o email já existe, false caso contrário
     */
    public boolean emailExists(String email){ //* Parece funcionar
        return userRepository.emailExists(email) == 1L;
    }

    public boolean checkPassword(String password, String encodedPassword){ //* Parece funcionar
        return bCryptPasswordEncoder.matches(password, encodedPassword);
    }

}
