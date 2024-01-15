package org.stuchat.chatserver.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Data
public class FindUserResponse {
    private Long id;
    private String username;
}
