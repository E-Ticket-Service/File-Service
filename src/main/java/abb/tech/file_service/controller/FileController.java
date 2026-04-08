package abb.tech.file_service.controller;

import abb.tech.file_service.model.File;
import abb.tech.file_service.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<File> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        File fileEntity = fileService.uploadFile(file);
        return ResponseEntity.ok(fileEntity);
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Resource> downloadFile(@PathVariable UUID uuid) throws IOException {
        File fileEntity = fileService.downloadFile(uuid);
        Path filePath = Paths.get(fileEntity.getFilePath());
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists()) {
            String contentType = fileEntity.getFileType() != null ? fileEntity.getFileType().getName() : "application/octet-stream";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.getFileName() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
