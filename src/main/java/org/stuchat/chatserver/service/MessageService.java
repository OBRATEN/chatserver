package org.stuchat.chatserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.stuchat.chatserver.entities.Message;
import org.stuchat.chatserver.repositories.MessageRepository;

import java.util.Optional;

@Service
public class MessageService {
    private MessageRepository messageRepository;

    @Autowired
    public void setMessageRepository(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Optional<Message> findById(Long id) {
        return messageRepository.findById(id);
    }
}
