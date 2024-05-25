package com.grupo6.votingapp.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.grupo6.votingapp.auth.PasswordUtil;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.repositories.UserRepository;

@Service
public class UserService  {
    private UserRepository userRepository;
    private PasswordUtil passwordUtil;

    public UserService(
        UserRepository userRepository,
        PasswordUtil passwordUtil
    ){
        this.userRepository = userRepository;
        this.passwordUtil = passwordUtil;
    }

    public List<User> getUsers(){
        return userRepository.findAll();
    }

    public User getUser(Long id){
        return userRepository.findById(id).orElse(null);
    }

    public User getUser(String id){
        return userRepository.findById(Long.parseLong(id)).orElse(null);
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElse(null);
    }

    public User saveUser(User user){
        user.setPassword(passwordUtil.encodePassword(user.getPassword()));
        return userRepository.save(user);
    }

    public User updateUser(User user){
        User userToUpdate = userRepository.findById(user.getId()).orElse(null);
        if(userToUpdate != null){
            userToUpdate.updateFromUser(user);
            userToUpdate.setPassword(passwordUtil.encodePassword(userToUpdate.getPassword()));
            return userRepository.save(userToUpdate);
        }
        return null;
    }

    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }

    public String getEmailFromUserId(String id){
        return userRepository.findEmailFromUserId(id).orElse(null);
    }

}
