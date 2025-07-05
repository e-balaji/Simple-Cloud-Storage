package com.airtribe.scs.repository;

import com.airtribe.scs.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    Optional<FileMetadata> findTopByFilenameAndOwnerUsernameOrderByVersionDesc(String filename, String username);

    List<FileMetadata> findByFilenameAndOwnerUsernameOrderByVersionDesc(String filename, String username);

    Optional<FileMetadata> findByFilenameAndVersionAndOwnerUsername(String filename, int version, String username);

}
