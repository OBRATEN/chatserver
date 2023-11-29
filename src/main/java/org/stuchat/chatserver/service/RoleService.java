package org.stuchat.chatserver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.stuchat.chatserver.entities.Role;
import org.stuchat.chatserver.repositories.RoleRepository;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;

    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER").get();
    }
}