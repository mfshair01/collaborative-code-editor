package com.example.collaborative_code_editor.presentation.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Set;

@Controller
public class PresenceController {

    private final SimpMessageSendingOperations messagingTemplate;

    private Map<String, Set<String>> fileUsersMap = new ConcurrentHashMap<>();


    public PresenceController(SimpMessageSendingOperations messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void joinFile(@DestinationVariable String fileId, Map<String, String> message) {
        String username = message.get("username");
        fileUsersMap.computeIfAbsent(fileId, k -> Collections.newSetFromMap(new ConcurrentHashMap<>())).add(username);

        messagingTemplate.convertAndSend("/topic/presence/" + fileId, fileUsersMap.get(fileId));
    }

    public void leaveFile(@DestinationVariable String fileId, Map<String, String> message) {
        String username = message.get("username");
        Set<String> users = fileUsersMap.get(fileId);
        if (users != null) {
            users.remove(username);
            if (users.isEmpty()) {
                fileUsersMap.remove(fileId);
            } else {
                messagingTemplate.convertAndSend("/topic/presence/" + fileId, users);
            }
        }
    }

}
