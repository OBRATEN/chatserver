package org.stuchat.chatserver.dtos;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
public class NewDialogueMessageRequest {
    private String friend;
    private String content;
    private Timestamp date;

    public NewDialogueMessageRequest(String friend, String content) {
        this.friend = friend;
        this.content = content;
        this.date = Timestamp.from(Instant.now());
    }
}
