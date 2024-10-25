package com.example.collaborative_code_editor.presentation.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class EditorController {

    @MessageMapping("/edit/{fileId}")
    @SendTo("/topic/updates/{fileId}")
    public Map<String, Object> sendContent(@DestinationVariable String fileId, Map<String, Object> delta) {
        System.out.println("Broadcasting delta for file " + fileId + ": " + delta);
        return delta;
    }
}
