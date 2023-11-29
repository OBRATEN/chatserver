package org.stuchat.chatserver.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.stuchat.chatserver.dtos.*;
import org.stuchat.chatserver.entities.Dialogue;
import org.stuchat.chatserver.entities.Message;
import org.stuchat.chatserver.entities.Role;
import org.stuchat.chatserver.entities.User;
import org.stuchat.chatserver.jwt.JwtUtils;
import org.stuchat.chatserver.repositories.DialogueRepository;
import org.stuchat.chatserver.repositories.MessageRepository;
import org.stuchat.chatserver.repositories.RoleRepository;
import org.stuchat.chatserver.repositories.UserRepository;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class MainController {
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

    @GetMapping("/home")
    public String homeMapping() {
        return "home";
    }

    @GetMapping("/")
    public String rootMapping() {
        return "root";
    }

    @GetMapping("/get/dialogues_list")
    public ResponseEntity<?> getDialoguesList(Authentication authentication) {
        Long user_id = userRepository.findByUsername(
                ((UserDetails)authentication.getPrincipal()).getUsername()
        ).get().getId();
        List<Dialogue> dialoguesList = dialogueRepository.findAllByUser1Id(user_id);
        if (dialoguesList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new CommonResponse("No dialogues yet"));
        }
        List<DialogueResponse> responseList = new ArrayList<>();
        for (Dialogue dia: dialoguesList) {
            if (messageRepository.existsByDialogueId(dia.getId())) {
                responseList.add(new DialogueResponse(
                                dia.getId().toString(),
                                userRepository.findById(
                                        (Objects.equals(dia.getUser1Id(), user_id)) ? dia.getUser2Id() : dia.getUser1Id()
                                ).get().getUsername(),
                                messageRepository.findTopByDialogueId(dia.getId()).get().getContent(),
                                messageRepository.findTopByDialogueId(dia.getId()).get().getDate().toString()
                        )
                );
            } else {
                responseList.add(new DialogueResponse(
                        dia.getId().toString(),
                        userRepository.findById(
                                (Objects.equals(dia.getUser1Id(), user_id)) ? dia.getUser2Id() : dia.getUser1Id()
                        ).get().getUsername(),
                        "No messages yet", ""
                ));
            }
        }
        return ResponseEntity.ok(new DialogueListResponse(responseList));
    }

    @GetMapping("/get/dialogue")
    public ResponseEntity<?> getDialogue(Authentication authentication,
                                         @RequestBody MessageListRequest request) {
        Long userId = userRepository.findByUsername(
                ((UserDetails)authentication.getPrincipal()).getUsername()
        ).get().getId();
        Long friendId = userRepository.findByUsername(request.getFriend()).get().getId();
        boolean isUserStarted = false;
        boolean dialogueExists = false;
        if (dialogueRepository.existsByUser1IdAndUser2Id(userId, friendId)) {
            dialogueExists = true; isUserStarted = true;
        } else if (dialogueRepository.existsByUser1IdAndUser2Id(friendId, userId)) {
            dialogueExists = true;
        }
        if (dialogueExists) {
            Long dialogueId;
            if (isUserStarted) dialogueId = dialogueRepository.findByUser1IdAndUser2Id(userId, friendId).get().getId();
            else dialogueId = dialogueRepository.findByUser1IdAndUser2Id(friendId, userId).get().getId();
            List<Message> messageList = messageRepository.findAllByDialogueId(dialogueId);
            List<MessageResponse> responseList = new ArrayList<>();
            if (messageList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new CommonResponse("Empty dialogue"));
            } else {
                for (Message message : messageList) {
                    responseList.add(new MessageResponse(
                            message.getId(),
                            (Objects.equals(userId, message.getSenderId())),
                            message.getContent(),
                            message.getDate()
                    ));
                }
                return ResponseEntity.ok(new MessageListResponse(responseList));
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CommonResponse("No such dialogue"));
        }
    }

    @PostMapping("/new/dialogue")
    public ResponseEntity<?> newDialogue(Authentication authentication,
                                         @RequestBody NewDialogueRequest request) {
        if (!userRepository.existsByUsername(request.getFriend())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponse("User not found"));
        }
        Long userId = userRepository.findByUsername(
                ((UserDetails)authentication.getPrincipal()).getUsername()
        ).get().getId();
        Long friendId = userRepository.findByUsername(request.getFriend()).get().getId();
        if (dialogueRepository.existsByUser1IdAndUser2Id(userId, friendId)
        || dialogueRepository.existsByUser1IdAndUser2Id(friendId, userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new CommonResponse("Dialogue already exists"));
        }
        Dialogue dialogue = new Dialogue(userId, friendId);
        dialogueRepository.save(dialogue);
        return ResponseEntity.ok(new CommonResponse("Diaogue created"));
    }
    @PostMapping("/new/message/to_dialogue")
    public ResponseEntity<?> newMessageDialogue(Authentication authentication,
                                                @RequestBody NewDialogueMessageRequest request) {
        if (!userRepository.existsByUsername(request.getFriend())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponse("User not found"));
        }
        Long userId = userRepository.findByUsername(
                ((UserDetails)authentication.getPrincipal()).getUsername()
        ).get().getId();
        Long friendId = userRepository.findByUsername(request.getFriend()).get().getId();
        boolean isUserStarted = false;
        boolean isDialogueExists = false;
        if (dialogueRepository.existsByUser1IdAndUser2Id(userId, friendId)) {
            isUserStarted = true;
            isDialogueExists = true;
        } else if (dialogueRepository.existsByUser1IdAndUser2Id(friendId, userId)) {
            isUserStarted = false;
            isDialogueExists = true;
        }
        if (!isDialogueExists) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new CommonResponse("No such dialogue"));
        Long dialogueId;
        if (isUserStarted) dialogueId = dialogueRepository.findByUser1IdAndUser2Id(userId, friendId).get().getId();
        else dialogueId = dialogueRepository.findByUser1IdAndUser2Id(friendId, userId).get().getId();
        Message message = new Message(
                dialogueId,
                (isUserStarted) ? userId : friendId,
                request.getContent()
        );
        messageRepository.save(message);
        return ResponseEntity.ok(new CommonResponse("Message sent"));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> createNewUser(@RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResponse("Error: username exists"));
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new CommonResponse("Error: email exists"));
        }
        User user = new User(
                signupRequest.getUsername(),
                signupRequest.getEmail(),
                passwordEncoder.encode(signupRequest.getPassword())
        );
        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName("Student")
                .orElseThrow(() -> new RuntimeException("Error, role Student not found!"));
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new CommonResponse("User created"));
    }

    @PostMapping("/signin")
    public ResponseEntity<?> createTokenMapping(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken(authentication);
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @GetMapping("/account")
    public String accountMapping() { return "account"; }

    @GetMapping("/admin")
    public String adminMapping() { return "admin"; }
}
