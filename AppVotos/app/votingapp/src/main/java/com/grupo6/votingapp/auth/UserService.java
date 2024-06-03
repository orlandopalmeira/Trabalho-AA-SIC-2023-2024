package com.grupo6.votingapp.auth;

import java.util.List;

import org.springframework.stereotype.Service;

import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.repositories.UserRepository;

@Service
public class UserService {
    private UserRepository userRepository;
    private PasswordUtil passwordUtil;

    public UserService(
        UserRepository userRepository,
        PasswordUtil passwordUtil
    ){
        this.userRepository = userRepository;
        this.passwordUtil = passwordUtil;
    }

    public List<User> getUsers(){ //* Parece funcionar
        return userRepository.findAll();
    }

    public User getUser(String id){ //* Parece funcionar
        return userRepository.findById(Long.parseLong(id)).orElse(null);
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
        user.setPassword(passwordUtil.encodePassword(user.getPassword()));
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

}
