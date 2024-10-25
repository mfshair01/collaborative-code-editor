package com.example.collaborative_code_editor.service;

import com.example.collaborative_code_editor.business.exception.*;
import com.example.collaborative_code_editor.business.service.FileService;
import com.example.collaborative_code_editor.persistence.entity.FileEntity;
import com.example.collaborative_code_editor.persistence.entity.Version;
import com.example.collaborative_code_editor.persistence.repository.FileRepository;
import com.example.collaborative_code_editor.persistence.repository.VersionRepository;
import com.example.collaborative_code_editor.presentation.dto.FileDTO;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private VersionRepository versionRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFile_Success() {
        Long fileId = 1L;
        FileEntity fileEntity = new FileEntity();
        fileEntity.setId(fileId);
        fileEntity.setName("TestFile.java");
        when(fileRepository.findById(fileId)).thenReturn(Optional.of(fileEntity));

        FileEntity result = fileService.getFile(fileId);

        assertNotNull(result);
        assertEquals(fileId, result.getId());
        verify(fileRepository, times(1)).findById(fileId);
    }

    @Test
    void testGetFile_FileNotFound() {
        Long fileId = 1L;
        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () -> {
            fileService.getFile(fileId);
        });

        verify(fileRepository, times(1)).findById(fileId);
    }

    @Test
    void testListFiles_Success() {
        String path = "/src";
        FileEntity file1 = new FileEntity("Test1.java", path, false, "", "user1");
        FileEntity file2 = new FileEntity("Test2.java", path, false, "", "user2");
        when(fileRepository.findByPath(path)).thenReturn(Arrays.asList(file1, file2));

        List<FileDTO> result = fileService.listFiles(path);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test1.java", result.get(0).getName());
        assertEquals("Test2.java", result.get(1).getName());
        verify(fileRepository, times(1)).findByPath(path);
    }

    @Test
    void testUpdateFile_Success() {
        Long fileId = 1L;
        String newContent = "Updated content";
        String username = "user1";

        FileEntity fileEntity = new FileEntity("TestFile.java", "/src", false, "Old content", "user1");
        fileEntity.setId(fileId);

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(fileEntity));
        when(fileRepository.save(any(FileEntity.class))).thenAnswer(i -> i.getArgument(0));

        FileEntity result = fileService.updateFile(fileId, newContent, username);

        assertNotNull(result);
        assertEquals(newContent, result.getContent());

        verify(fileRepository, times(1)).findById(fileId);
        verify(fileRepository, times(1)).save(fileEntity);
        verify(versionRepository, times(1)).save(any(Version.class));
    }

    @Test
    void testUpdateFile_FileNotFound() {
        Long fileId = 1L;
        String newContent = "Updated content";
        String username = "user1";

        when(fileRepository.findById(fileId)).thenReturn(Optional.empty());

        assertThrows(FileNotFoundException.class, () -> {
            fileService.updateFile(fileId, newContent, username);
        });

        verify(fileRepository, times(1)).findById(fileId);
        verify(fileRepository, never()).save(any(FileEntity.class));
    }

    @Test
    void testUpdateFile_IsDirectory() {
        Long fileId = 1L;
        String newContent = "Updated content";
        String username = "user1";

        FileEntity directory = new FileEntity("src", "/", true, null, "user1");
        directory.setId(fileId);

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(directory));

        assertThrows(InvalidOperationException.class, () -> {
            fileService.updateFile(fileId, newContent, username);
        });

        verify(fileRepository, times(1)).findById(fileId);
        verify(fileRepository, never()).save(any(FileEntity.class));
    }

    @Test
    void testDeleteFile_Success() {
        Long fileId = 1L;

        when(fileRepository.existsById(fileId)).thenReturn(true);

        boolean result = fileService.deleteFile(fileId);

        assertTrue(result);
        verify(fileRepository, times(1)).existsById(fileId);
        verify(fileRepository, times(1)).deleteById(fileId);
    }

    @Test
    void testDeleteFile_FileNotFound() {
        Long fileId = 1L;

        when(fileRepository.existsById(fileId)).thenReturn(false);

        assertThrows(FileNotFoundException.class, () -> {
            fileService.deleteFile(fileId);
        });

        verify(fileRepository, times(1)).existsById(fileId);
        verify(fileRepository, never()).deleteById(fileId);
    }

    @Test
    void testGetVersions_Success() {
        Long fileId = 1L;
        Version version1 = new Version();
        Version version2 = new Version();

        when(fileRepository.existsById(fileId)).thenReturn(true);
        when(versionRepository.findByFileEntityIdOrderByTimestampDesc(fileId))
                .thenReturn(Arrays.asList(version1, version2));

        List<Version> result = fileService.getVersions(fileId);

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(fileRepository, times(1)).existsById(fileId);
        verify(versionRepository, times(1)).findByFileEntityIdOrderByTimestampDesc(fileId);
    }

    @Test
    void testGetVersions_FileNotFound() {
        Long fileId = 1L;

        when(fileRepository.existsById(fileId)).thenReturn(false);

        assertThrows(FileNotFoundException.class, () -> {
            fileService.getVersions(fileId);
        });

        verify(fileRepository, times(1)).existsById(fileId);
        verify(versionRepository, never()).findByFileEntityIdOrderByTimestampDesc(fileId);
    }

    @Test
    void testRevertToVersion_Success() {
        Long fileId = 1L;
        Long versionId = 100L;
        String username = "user1";

        FileEntity fileEntity = new FileEntity("TestFile.java", "/src", false, "Current content", "user1");
        fileEntity.setId(fileId);

        Version version = new Version(fileEntity, "Previous content", "user1", LocalDateTime.now());
        version.setId(versionId);

        when(versionRepository.findById(versionId)).thenReturn(Optional.of(version));
        when(fileRepository.save(any(FileEntity.class))).thenAnswer(i -> i.getArgument(0));

        boolean result = fileService.revertToVersion(fileId, versionId, username);

        assertTrue(result);
        assertEquals("Previous content", fileEntity.getContent());

        verify(versionRepository, times(1)).findById(versionId);
        verify(fileRepository, times(1)).save(fileEntity);
        verify(versionRepository, times(1)).save(any(Version.class));
    }

    @Test
    void testRevertToVersion_VersionNotFound() {
        Long fileId = 1L;
        Long versionId = 100L;
        String username = "user1";

        when(versionRepository.findById(versionId)).thenReturn(Optional.empty());

        assertThrows(VersionNotFoundException.class, () -> {
            fileService.revertToVersion(fileId, versionId, username);
        });

        verify(versionRepository, times(1)).findById(versionId);
        verify(fileRepository, never()).save(any(FileEntity.class));
    }

    @Test
    void testRevertToVersion_VersionDoesNotBelongToFile() {
        Long fileId = 1L;
        Long versionId = 100L;
        String username = "user1";

        FileEntity fileEntity1 = new FileEntity();
        fileEntity1.setId(fileId);

        FileEntity fileEntity2 = new FileEntity();
        fileEntity2.setId(2L);

        Version version = new Version(fileEntity2, "Content", "user1", LocalDateTime.now());
        version.setId(versionId);

        when(versionRepository.findById(versionId)).thenReturn(Optional.of(version));

        assertThrows(InvalidOperationException.class, () -> {
            fileService.revertToVersion(fileId, versionId, username);
        });

        verify(versionRepository, times(1)).findById(versionId);
        verify(fileRepository, never()).save(any(FileEntity.class));
    }

    @Test
    void testUpdateFile_ConcurrentModification() {
        Long fileId = 1L;
        String newContent = "Updated content";
        String username = "user1";

        FileEntity fileEntity = new FileEntity("TestFile.java", "/src", false, "Old content", "user1");
        fileEntity.setId(fileId);

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(fileEntity));
        doThrow(new OptimisticLockException("Simulated concurrent modification"))
                .when(fileRepository).save(any(FileEntity.class));

        assertThrows(ConcurrentModificationException.class, () -> {
            fileService.updateFile(fileId, newContent, username);
        });

        verify(fileRepository, times(1)).findById(fileId);
        verify(fileRepository, times(1)).save(fileEntity);
    }

}
