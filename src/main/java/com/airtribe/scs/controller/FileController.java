package com.airtribe.scs.controller;

import com.airtribe.scs.entity.FileMetadata;
import com.airtribe.scs.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.airtribe.scs.entity.Visibility;

import java.io.*;
import java.nio.file.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    private final Path uploadDir;

    public FileController(@Value("${file.upload-dir:uploads}") String uploadDir) throws IOException {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadDir);
    }

    @PostMapping("/upload/{folder}")
    public ResponseEntity<String> uploadToFolder(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String folder,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "visibility", defaultValue = "PRIVATE") Visibility visibility) throws IOException {

        return fileService.uploadToFolder(authHeader, folder, file, uploadDir, visibility);

    }


    @GetMapping("/list/{user}/{folder}")
    public ResponseEntity<?> listFilesInFolder(
            @PathVariable String user,
            @PathVariable String folder) throws IOException {

        return fileService.listFilesInFolder(user, folder, uploadDir);

    }


    @GetMapping("/download/versioned/{filename}/{version}")
    public ResponseEntity<?> downloadVersionedFile(
            @PathVariable String filename,
            @PathVariable int version,
            Principal principal) throws IOException {

        return fileService.downloadFile(filename, version, principal);

    }

    @GetMapping("/versions/{filename}")
    public ResponseEntity<?> listVersions(@PathVariable String filename, Principal principal) {
       return fileService.listVersions(filename,principal);
    }


}
