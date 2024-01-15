package org.stuchat.chatserver.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ChatInfoResponse {
    Long id;
    String chatName;
    String lastMessage;
    String lastDate;
}
