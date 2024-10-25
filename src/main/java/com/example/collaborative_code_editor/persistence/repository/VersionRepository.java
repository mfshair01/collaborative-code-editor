package com.example.collaborative_code_editor.persistence.repository;

import com.example.collaborative_code_editor.persistence.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VersionRepository extends JpaRepository<Version, Long> {
    List<Version> findByFileEntityIdOrderByTimestampDesc(Long fileEntityId);
}
