package com.example.collaborative_code_editor.business.service;

import com.example.collaborative_code_editor.business.exception.FileNotFoundException;
import com.example.collaborative_code_editor.business.exception.InvalidOperationException;
import com.example.collaborative_code_editor.business.exception.VersionNotFoundException;
import com.example.collaborative_code_editor.presentation.dto.FileDTO;
import com.example.collaborative_code_editor.presentation.dto.FileDetailDTO;
import com.example.collaborative_code_editor.persistence.entity.FileEntity;
import com.example.collaborative_code_editor.persistence.entity.Version;
import com.example.collaborative_code_editor.persistence.repository.FileRepository;
import com.example.collaborative_code_editor.persistence.repository.VersionRepository;
import jakarta.persistence.OptimisticLockException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileService {

    private final FileRepository fileRepository;
    private final VersionRepository versionRepository;

    public FileService(FileRepository fileRepository, VersionRepository versionRepository) {
        this.fileRepository = fileRepository;
        this.versionRepository = versionRepository;
    }

    public FileEntity createFile(String name, String path, boolean isDirectory, String content, String ownerUsername) {
        FileEntity fileEntity = new FileEntity(name, path, isDirectory, content, ownerUsername);
        return fileRepository.save(fileEntity);
    }

    public FileEntity getFile(Long id) {
        return fileRepository.findById(id).orElseThrow(() -> new FileNotFoundException("File not found."));
    }

    public List<FileDTO> listFiles(String path) {
        List<FileEntity> files = fileRepository.findByPath(path);
        return files.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    private FileDTO convertToDTO(FileEntity fileEntity) {
        return new FileDTO(
                fileEntity.getId(),
                fileEntity.getName(),
                fileEntity.getPath(),
                fileEntity.isDirectory(),
                fileEntity.getOwnerUsername()
        );
    }

    @Transactional
    public FileEntity updateFile(Long id, String content, String username) {
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found."));
        if (fileEntity.isDirectory()) {
            throw new InvalidOperationException("Cannot update content of a directory.");
        }

        fileEntity.getLock().lock();
        try {
            fileEntity.setContent(content);
            try {
                fileEntity = fileRepository.save(fileEntity);
            } catch (OptimisticLockException e) {
                throw new ConcurrentModificationException("File was updated by another user.");
            }

            Version version = new Version(
                    fileEntity,
                    content,
                    username,
                    LocalDateTime.now()
            );
            versionRepository.save(version);

            return fileEntity;
        } finally {
            fileEntity.getLock().unlock();
        }
    }

    public List<Version> getVersions(Long fileEntityId) {
        if (!fileRepository.existsById(fileEntityId)) {
            throw new FileNotFoundException("File not found.");
        }
        return versionRepository.findByFileEntityIdOrderByTimestampDesc(fileEntityId);
    }

    public boolean revertToVersion(Long fileId, Long versionId, String username) {
        Version version = versionRepository.findById(versionId)
                .orElseThrow(() -> new VersionNotFoundException("Version not found."));
        FileEntity fileEntity = version.getFileEntity();
        if (!fileEntity.getId().equals(fileId)) {
            throw new InvalidOperationException("Version does not belong to the specified file.");
        }

        fileEntity.getLock().lock();
        try {
            fileEntity.setContent(version.getContent());
            fileRepository.save(fileEntity);

            Version newVersion = new Version(
                    fileEntity,
                    version.getContent(),
                    username,
                    LocalDateTime.now()
            );
            versionRepository.save(newVersion);

            return true;
        } finally {
            fileEntity.getLock().unlock();
        }
    }

    public boolean deleteFile(Long id) {
        if (!fileRepository.existsById(id)) {
            throw new FileNotFoundException("File not found.");
        }
        fileRepository.deleteById(id);
        return true;
    }

    public FileDetailDTO getFileDetail(Long id) {
        FileEntity fileEntity = fileRepository.findById(id)
                .orElseThrow(() -> new FileNotFoundException("File not found."));
        return convertToDetailDTO(fileEntity);
    }

    private FileDetailDTO convertToDetailDTO(FileEntity fileEntity) {
        return new FileDetailDTO(
                fileEntity.getId(),
                fileEntity.getName(),
                fileEntity.getPath(),
                fileEntity.isDirectory(),
                fileEntity.getContent(),
                fileEntity.getOwnerUsername()
        );
    }
}
