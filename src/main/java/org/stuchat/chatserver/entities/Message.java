package org.stuchat.chatserver.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@Entity
@Data
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "dialogue_id")
    private Long dialogueId;

    @Column(name = "sender_id")
    private Long senderId;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private Timestamp date;

    public Message(Long dialogue_id, Long sender_id, String content) {
        this.dialogueId = dialogue_id;
        this.senderId = sender_id;
        this.content = content;
        this.date = Timestamp.from(Instant.now());
    }

    public Message() {

    }
}
