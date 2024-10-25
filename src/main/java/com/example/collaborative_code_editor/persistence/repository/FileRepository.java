package com.example.collaborative_code_editor.persistence.repository;

import com.example.collaborative_code_editor.persistence.entity.FileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {
    List<FileEntity> findByPath(String path);
    Optional<FileEntity> findByPathAndName(String path, String name);
}
