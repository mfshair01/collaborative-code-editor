package com.example.collaborative_code_editor.presentation.controller;

import com.example.collaborative_code_editor.business.exception.*;
import com.example.collaborative_code_editor.presentation.dto.FileDTO;
import com.example.collaborative_code_editor.persistence.entity.FileEntity;
import com.example.collaborative_code_editor.persistence.entity.Version;
import com.example.collaborative_code_editor.business.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/create")
    public ResponseEntity<FileEntity> createFile(@RequestBody Map<String, Object> request,
                                                 @AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            String name = (String) request.get("name");
            String path = (String) request.get("path");
            Object isDirectoryObj = request.get("isDirectory");

            boolean isDirectory;
            if (isDirectoryObj instanceof Boolean) {
                isDirectory = (Boolean) isDirectoryObj;
            } else if (isDirectoryObj instanceof String) {
                isDirectory = Boolean.parseBoolean((String) isDirectoryObj);
            } else {
                isDirectory = false; // Default value
            }

            String content = (String) request.get("content");
            String username = principal.getAttribute("login");

            FileEntity fileEntity = fileService.createFile(name, path, isDirectory, content, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(fileEntity);
        }
        throw new UnauthorizedAccessException("User not authenticated.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileEntity> getFile(@PathVariable Long id) {
        FileEntity fileEntity = fileService.getFile(id);
        return ResponseEntity.ok(fileEntity);
    }

    @GetMapping("/list")
    public ResponseEntity<List<FileDTO>> listFiles(@RequestParam String path) {
        List<FileDTO> files = fileService.listFiles(path);
        return ResponseEntity.ok(files);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<FileEntity> updateFile(@PathVariable Long id,
                                                 @RequestBody Map<String, String> request,
                                                 @AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            String content = request.get("content");
            String username = principal.getAttribute("login");
            FileEntity updatedFile = fileService.updateFile(id, content, username);
            return ResponseEntity.ok(updatedFile);
        }
        throw new UnauthorizedAccessException("User not authenticated.");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable Long id) {
        fileService.deleteFile(id);
        return ResponseEntity.ok("File deleted successfully");
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<List<Version>> getVersions(@PathVariable Long id) {
        List<Version> versions = fileService.getVersions(id);
        return ResponseEntity.ok(versions);
    }

    @PostMapping("/{id}/revert")
    public ResponseEntity<String> revertToVersion(@PathVariable Long id,
                                                  @RequestBody Map<String, String> requestBody,
                                                  @AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            Long versionId = Long.parseLong(requestBody.get("versionId"));
            String username = principal.getAttribute("login");

            fileService.revertToVersion(id, versionId, username);
            return ResponseEntity.ok("Reverted to version " + versionId);
        }
        throw new UnauthorizedAccessException("User not authenticated.");
    }
}
