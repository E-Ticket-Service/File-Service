package abb.tech.file_service.repository;

import abb.tech.file_service.model.FileType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FileTypeRepository extends JpaRepository<FileType, Long> {
    Optional<FileType> findByName(String name);
}
