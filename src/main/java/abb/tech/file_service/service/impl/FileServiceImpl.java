package abb.tech.file_service.service.impl;

import abb.tech.file_service.model.File;
import abb.tech.file_service.model.FileExtension;
import abb.tech.file_service.model.FileType;
import abb.tech.file_service.repository.FileExtensionRepository;
import abb.tech.file_service.repository.FileRepository;
import abb.tech.file_service.repository.FileTypeRepository;
import abb.tech.file_service.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final FileTypeRepository fileTypeRepository;
    private final FileExtensionRepository fileExtensionRepository;
    @Value("${spring.servlet.multipart.location}")
    private String uploadDir;

    @Override
    public File uploadFile(MultipartFile file) throws IOException {
        Path fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(fileStorageLocation);

        String originalFileName = getOriginalFileName(file);
        String extension = getFileExtension(originalFileName);
        String storedFileName = generateStoredFileName(extension);
        Path targetLocation = fileStorageLocation.resolve(storedFileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        FileType fileType = getOrCreateFileType(file.getContentType());
        FileExtension fileExtension = getOrCreateFileExtension(extension);

        File fileEntity = File.builder()
                .fileName(originalFileName)
                .fileType(fileType)
                .fileExtension(fileExtension)
                .filePath(targetLocation.toString())
                .fileSize(file.getSize())
                .build();

        return fileRepository.save(fileEntity);
    }

    private String getOriginalFileName(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            return "unknown_" + UUID.randomUUID();
        }
        return fileName;
    }

    private String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        return (i > 0) ? fileName.substring(i + 1) : "";
    }

    private String generateStoredFileName(String extension) {
        return UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
    }

    private FileType getOrCreateFileType(String contentType) {
        if (contentType == null) {
            return null;
        }
        return fileTypeRepository.findByName(contentType)
                .orElseGet(() -> fileTypeRepository.save(FileType.builder().name(contentType).build()));
    }

    private FileExtension getOrCreateFileExtension(String extension) {
        if (extension.isEmpty()) {
            return null;
        }
        final String ext = extension.toLowerCase();
        return fileExtensionRepository.findByExtension(ext)
                .orElseGet(() -> fileExtensionRepository.save(FileExtension.builder().extension(ext).build()));
    }

    @Override
    public File downloadFile(UUID fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
    }
}
