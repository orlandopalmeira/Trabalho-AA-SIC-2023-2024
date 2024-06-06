package com.grupo6.votingapp.repositories;

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

    @Query(value="SELECT EXISTS(SELECT 1 FROM users WHERE email = ?1)", nativeQuery = true)
    Long emailExists(String email);

    @Query("SELECT u FROM User u WHERE u.id in :usersIds")
    List<User> findUsersByIds(List<Long> usersIds);

    //! WIP
    @Query("SELECT u FROM User u WHERE u.name LIKE %:term% OR u.email LIKE %:term%")
    Optional<List<User>> findByTerm(String term);
}
