package com.grupo6.votingapp.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.grupo6.votingapp.auth.PasswordUtil;
import com.grupo6.votingapp.models.User;
import com.grupo6.votingapp.repositories.UserRepository;

@Service
public class UserService implements UserDetailsService {
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

    public User getUser(Long id){ //* Parece funcionar
        return userRepository.findById(id).orElse(null);
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

    public User updateUser(User user){ //* Parece funcionar
        User userToUpdate = userRepository.findById(user.getId()).orElse(null);
        if(userToUpdate != null){
            userToUpdate.updateFromUser(user);
            if(user.getPassword() != null && !user.getPassword().isEmpty()){
                //* Só actualiza a password se a informação dela vier no pedido
                //* Isto é feito porque a password é cifrada e se fizermos encode de "" ou null pode dar problemas
                userToUpdate.setPassword(passwordUtil.encodePassword(userToUpdate.getPassword()));
            }
            return userRepository.save(userToUpdate);
        }
        return null;
    }

    public void deleteUser(Long id){ //* Parece funcionar
        User entity = userRepository.findById(id).orElse(null);
        if(entity != null){
            entity.removeAllPrivateVotings();
            userRepository.delete(entity);
        }
    }

    public String getEmailFromUserId(String id){ //* Parece funcionar
        return userRepository.findEmailFromUserId(id).orElse(null);
    }

    //* Métodos da interface UserDetailsService (Spring Security)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

}
