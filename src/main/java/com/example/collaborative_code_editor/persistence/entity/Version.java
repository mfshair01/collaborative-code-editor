package com.example.collaborative_code_editor.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "file_entity_id")
    private FileEntity fileEntity;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String username;

    private LocalDateTime timestamp;

    public Version(FileEntity fileEntity, String content, String username, LocalDateTime timestamp) {
        this.fileEntity = fileEntity;
        this.content = content;
        this.username = username;
        this.timestamp = timestamp;
    }
}
