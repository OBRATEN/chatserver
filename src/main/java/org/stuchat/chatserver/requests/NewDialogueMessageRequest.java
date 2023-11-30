package org.stuchat.chatserver.requests;

import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
public class NewDialogueMessageRequest {
    private Long dialogueId;
    private String content;
    private Timestamp date;

    public NewDialogueMessageRequest(Long dialogueId, String content) {
        this.dialogueId = dialogueId;
        this.content = content;
        this.date = Timestamp.from(Instant.now());
    }
}
