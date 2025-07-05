package com.airtribe.scs.service;

import com.airtribe.scs.entity.FileMetadata;
import com.airtribe.scs.entity.User;
import com.airtribe.scs.entity.Visibility;
import com.airtribe.scs.repository.FileMetadataRepository;
import com.airtribe.scs.repository.UserRepository;
import com.airtribe.scs.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileMetadataRepository fileMetadataRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public ResponseEntity<String> uploadToFolder(String authHeader, String folder, MultipartFile file, Path uploadDir, Visibility visibility) throws IOException {
        String username = jwtUtil.extractUsername(authHeader.substring(7));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());

        // Determine next version
        int version = fileMetadataRepository
                .findTopByFilenameAndOwnerUsernameOrderByVersionDesc(originalFilename, username)
                .map(FileMetadata::getVersion)
                .orElse(0) + 1;

        // Save file with version suffix
        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String baseName = originalName.contains(".")
                ? originalName.substring(0, originalName.lastIndexOf('.'))
                : originalName;

        String extension = originalName.contains(".")
                ? originalName.substring(originalName.lastIndexOf('.'))
                : "";

        String versionedFilename = baseName + "_v" + (version + 1) + extension;
        Path userFolderPath = uploadDir.resolve(Paths.get(username, folder)).normalize();
        Files.createDirectories(userFolderPath);
        Path targetLocation = userFolderPath.resolve(versionedFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        // Save metadata
        FileMetadata metadata = new FileMetadata();
        metadata.setFilename(originalFilename);
        metadata.setFolderPath(userFolderPath.toString());
        metadata.setVisibility(visibility);
        metadata.setOwner(user);
        metadata.setVersion(version);
        fileMetadataRepository.save(metadata);

        return ResponseEntity.ok("Uploaded version " + version + " of file: " + originalFilename);
    }

    public ResponseEntity<?> downloadFile(String filename, int version, Principal principal) throws MalformedURLException, FileNotFoundException {
        FileMetadata file = fileMetadataRepository
                .findByFilenameAndVersionAndOwnerUsername(filename, version, principal.getName())
                .orElseThrow(() -> new FileNotFoundException("Version not found"));

        boolean isPublic = file.getVisibility() == Visibility.PUBLIC;
        boolean isOwner = file.getOwner().getUsername().equals(principal.getName());

        if (!isPublic && !isOwner) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied");
        }

//        String fullFilename = filename + "_v" + version;
        Path filePath = Paths.get(file.getFolderPath(), file.getFilename()).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    public ResponseEntity<?> listFilesInFolder(String user, String folder, Path uploadDir) throws IOException {
        Path folderPath = uploadDir.resolve(Paths.get(user, folder)).normalize();
        if (!Files.exists(folderPath) || !Files.isDirectory(folderPath)) {
            return ResponseEntity.badRequest().body("Folder not found.");
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(folderPath)) {
            List<String> filenames = new ArrayList<>();
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    filenames.add(path.getFileName().toString());
                }
            }
            return ResponseEntity.ok(filenames);
        }
    }

    public ResponseEntity<?> listVersions(String filename,Principal principal){
        List<FileMetadata> versions = fileMetadataRepository
                .findByFilenameAndOwnerUsernameOrderByVersionDesc(filename, principal.getName());

        List<String> versionList = versions.stream()
                .map(meta -> "Version " + meta.getVersion() + " - " + meta.getVisibility())
                .toList();

        return ResponseEntity.ok(versionList);
    }
}
