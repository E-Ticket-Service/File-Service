package abb.tech.file_service.repository;

import abb.tech.file_service.model.FileExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileExtensionRepository extends JpaRepository<FileExtension, Long> {
    Optional<FileExtension> findByExtension(String extension);
}
