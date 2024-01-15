package org.stuchat.chatserver.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.stuchat.chatserver.entities.Message;
import org.stuchat.chatserver.jwt.JwtUtils;
import org.stuchat.chatserver.repositories.DialogueRepository;
import org.stuchat.chatserver.repositories.MessageRepository;
import org.stuchat.chatserver.repositories.RoleRepository;
import org.stuchat.chatserver.repositories.UserRepository;
import org.stuchat.chatserver.requests.NewDialogueMessageRequest;
import org.stuchat.chatserver.responses.CommonResponse;
import org.stuchat.chatserver.responses.MessageResponse;

@Controller
@RequiredArgsConstructor
public class DialogueController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DialogueRepository dialogueRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat")
    public CommonResponse processMessage(Authentication authentication, @Payload NewDialogueMessageRequest request) {
        if (!dialogueRepository.existsById(request.getDialogueId())) {
            return new CommonResponse("No such dialogue");
        }
        String username = ((UserDetails)authentication.getPrincipal()).getUsername();
        Long userId = userRepository.findByUsername(username).get().getId();
        Long friendId;
        if (dialogueRepository.findById(request.getDialogueId()).get().getUser1Id() == userId) {
            friendId = dialogueRepository.findById(request.getDialogueId()).get().getUser2Id();
        } else {
            friendId = dialogueRepository.findById(request.getDialogueId()).get().getUser1Id();
        }
        Message message = new Message(
                request.getDialogueId(),
                userId,
                request.getContent()
        );
        messageRepository.save(message);
        messagingTemplate.convertAndSendToUser(
                userRepository.findById(friendId).get().getUsername(), "/queue/messages",
                new MessageResponse(message.getId(), false, message.getContent(), message.getDate())
        );
        return new CommonResponse("Message sent");
    }

}
