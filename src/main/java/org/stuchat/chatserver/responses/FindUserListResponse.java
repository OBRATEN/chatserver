package org.stuchat.chatserver.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class FindUserListResponse {
    private List<FindUserResponse> responseList;
}
