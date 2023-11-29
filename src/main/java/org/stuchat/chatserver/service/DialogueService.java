package org.stuchat.chatserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stuchat.chatserver.entities.Dialogue;
import org.stuchat.chatserver.repositories.DialogueRepository;

import java.util.Optional;

@Service
public class DialogueService {
    private DialogueRepository dialogueRepository;

    @Autowired
    public void setDialogueRepository(DialogueRepository dialogueRepository) {
        this.dialogueRepository = dialogueRepository;
    }

    public Optional<Dialogue> findById(Long id) {
        return dialogueRepository.findById(id);
    }
}