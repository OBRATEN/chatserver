package org.stuchat.chatserver.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DisconnectRequest {
    private String username;
    private String password;
}
