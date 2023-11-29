package org.stuchat.chatserver.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DialogueResponse {
    String id;
    String friendName;
    String lastMessage;
    String lastDate;
}
