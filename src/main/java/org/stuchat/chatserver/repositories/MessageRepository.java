package org.stuchat.chatserver.repositories;

import org.springframework.data.repository.CrudRepository;
import org.stuchat.chatserver.entities.Dialogue;
import org.stuchat.chatserver.entities.Message;

import java.util.List;
import java.util.Optional;

public interface MessageRepository extends CrudRepository<Message, Long> {
    Optional<Message> findById(Long id);
    boolean existsById(Long id);
    Optional<Message> findByDialogueId(Long dialogueId);
    List<Message> findAllByDialogueId(Long dialogueId);
    boolean existsAllByDialogueId(Long dialogueId);
    boolean existsByDialogueId(Long id);

    Optional<Message> findTopByDialogueId(Long id);
}
