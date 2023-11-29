package org.stuchat.chatserver.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class DialogueListResponse {
    private List<DialogueResponse> responseList;
}
