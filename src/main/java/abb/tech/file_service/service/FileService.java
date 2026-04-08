package abb.tech.file_service.service;

import abb.tech.file_service.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface FileService {
    File uploadFile(MultipartFile file) throws IOException;
    File downloadFile(UUID fileId);
}
