package com.grupo6.votingapp.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.grupo6.votingapp.auth.PasswordUtil;
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

    public User saveUser(User user){ //* Parece funcionar
        user.setPassword(passwordUtil.encodePassword(user.getPassword()));
        return userRepository.save(user);
    }

    public String getEmailFromUserId(String id){ //* Parece funcionar
        return userRepository.findEmailFromUserId(id).orElse(null);
    }

}
