package org.stuchat.chatserver.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class MessageResponse {
    private Long id;
    private Boolean isYouSender;
    private String content;
    private Timestamp date;
}
