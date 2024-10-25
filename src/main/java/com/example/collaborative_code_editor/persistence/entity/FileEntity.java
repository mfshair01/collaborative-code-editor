package com.example.collaborative_code_editor.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class FileEntity {

    @Transient
    private final Lock lock = new ReentrantLock();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String path;

    private boolean isDirectory;

    @Column(columnDefinition = "TEXT")
    @Basic(fetch = FetchType.LAZY)
    private String content;

    private String ownerUsername;

    public FileEntity(String name, String path, boolean isDirectory, String content, String ownerUsername) {
        this.name = name;
        this.path = path;
        this.isDirectory = isDirectory;
        this.content = content;
        this.ownerUsername = ownerUsername;
    }
}
