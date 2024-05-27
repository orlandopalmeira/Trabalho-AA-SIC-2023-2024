package com.grupo6.votingapp.repositories;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.grupo6.votingapp.models.User;

/**
 * Interface que tem os CRUD básicos para o User. Também se pode colocar "métodos" personalizados (também com queries - anotação "Query")
 */
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);

    @Query("SELECT u.email FROM User u WHERE u.id = :id")
    Optional<String> findEmailFromUserId(String id);

    @Query("SELECT u FROM User u WHERE u.id IN :ids")
    List<User> findByIds(Collection<Long> ids);
}
