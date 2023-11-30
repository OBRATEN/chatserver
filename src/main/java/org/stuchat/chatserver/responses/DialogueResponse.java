package org.stuchat.chatserver.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DialogueResponse {
    Long id;
    String friendName;
    String lastMessage;
    String lastDate;
}
