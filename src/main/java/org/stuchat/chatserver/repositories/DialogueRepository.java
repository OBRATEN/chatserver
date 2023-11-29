package org.stuchat.chatserver.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.stuchat.chatserver.entities.Dialogue;
import org.stuchat.chatserver.entities.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DialogueRepository extends JpaRepository<Dialogue, Long> {
    Optional<Dialogue> findByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    boolean existsByUser1IdAndUser2Id(Long user1Id, Long user2Id);

    @Query("select d from Dialogue d where d.user1Id = :uid or d.user2Id = :uid")
    List<Dialogue> findAllByUser1Id(@Param("uid") Long userId);
}
