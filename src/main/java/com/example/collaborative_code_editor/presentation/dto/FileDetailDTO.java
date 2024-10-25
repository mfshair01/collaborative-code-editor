package com.example.collaborative_code_editor.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDetailDTO {

    private Long id;
    private String name;
    private String path;
    private boolean isDirectory;
    private String content;
    private String ownerUsername;
}