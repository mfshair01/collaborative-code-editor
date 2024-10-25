package com.example.collaborative_code_editor.presentation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Controller
public class CodeEditorController {

    @GetMapping("/editor")
    public String editor(Model model, @AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            String username = principal.getAttribute("login");
            model.addAttribute("username", username);
        }
        return "codeEditor";
    }
}
