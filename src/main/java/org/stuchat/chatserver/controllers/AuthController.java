package org.stuchat.chatserver.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.stuchat.chatserver.entities.Role;
import org.stuchat.chatserver.entities.User;
import org.stuchat.chatserver.jwt.JwtUtils;
import org.stuchat.chatserver.repositories.DialogueRepository;
import org.stuchat.chatserver.repositories.MessageRepository;
import org.stuchat.chatserver.repositories.RoleRepository;
import org.stuchat.chatserver.repositories.UserRepository;
import org.stuchat.chatserver.requests.SigninRequest;
import org.stuchat.chatserver.requests.SignupRequest;
import org.stuchat.chatserver.responses.CommonResponse;
import org.stuchat.chatserver.responses.JwtResponse;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;

    @MessageMapping("/hello")
    @SendTo("/user/topic")
    public SigninRequest hello(@Payload SigninRequest message) {
        System.out.println(message.getUsername());
        return new SigninRequest(
                message.getUsername(),
                message.getPassword()
        );
    }

    @MessageMapping("user.signup")
    @SendTo("/user/topic")
    public CommonResponse signupWS(@Payload SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return new CommonResponse("Error: username exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            new CommonResponse("Error: email exists");
        }
        org.stuchat.chatserver.entities.User user = new org.stuchat.chatserver.entities.User(
                request.getUsername(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                true
        );
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName("Student")
                .orElseThrow(() -> new RuntimeException("Error, role Student not found!"));
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
        return new CommonResponse("User created");
    }

    @MessageMapping("user.signin")
    @SendTo("/user/topic")
    public JwtResponse signinWS(@Payload SigninRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);
        return new JwtResponse(jwt);
    }

    @MessageMapping("user.disconnect")
    @SendTo("/user/topic")
    public CommonResponse disconnectWS(Authentication authentication) {
        Long userId = userRepository.findByUsername(
                ((UserDetails)authentication.getPrincipal()).getUsername()
        ).get().getId();
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new CommonResponse("No such user");
        }
        user.setOnline(false);
        userRepository.save(user);
        return new CommonResponse("Disconnected");
    }
}
