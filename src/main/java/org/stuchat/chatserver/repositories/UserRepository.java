package org.stuchat.chatserver.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.stuchat.chatserver.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    List<User> findAllByOnline(boolean online);

    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    boolean existsById(Long id);
}