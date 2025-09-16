package lk.ijse.election_backend.controller;

import lk.ijse.election_backend.dto.ApiResponse;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.nio.file.attribute.FileTime;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/voter/images")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

public class VoterImageController {
    String uploadDir = "src/main/resources/assets/voterPic"; // You can customize this path

    // GET /api/images/{username}/nic/front
    @GetMapping("/{nicNumber}/nic/front")
    public ResponseEntity<Resource> getNicFront(@PathVariable String nicNumber) throws IOException {
        return serveUserImage(nicNumber, "idFront");
    }

    // GET /api/images/{username}/nic/back
    @GetMapping("/{nicNumber}/nic/back")
    public ResponseEntity<Resource> getNicBack(@PathVariable String nicNumber) throws IOException {
        return serveUserImage(nicNumber, "idBack");
    }

    // GET /api/images/{nicNumber}/selfie
    @GetMapping("/{nicNumber}/selfie")
    public ResponseEntity<Resource> getSelfie(@PathVariable String nicNumber) throws IOException {
        return serveUserImage(nicNumber, "selfie");
    }

    private ResponseEntity<Resource> serveUserImage(String nicNumber, String suffix) throws IOException {
        Optional<Path> filePathOpt = findUserFileByPattern(nicNumber, suffix);
        if (filePathOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Path filePath = filePathOpt.get();
        Resource resource = toResource(filePath);
        if (resource == null || !resource.exists() || !resource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = MediaTypeFactory.getMediaType(filePath.getFileName().toString())
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionInline(filePath.getFileName().toString()))
                .body(resource);
//        return ResponseEntity.ok()
//                .contentType(mediaType)
//                .header(HttpHeaders.CONTENT_DISPOSITION, contentDispositionInline(filePath.getFileName().toString()))
//                .body(new ApiResponse(200, "Success", resource));
    }

    // Finds the newest file matching {username}_{suffix}.*
    private Optional<Path> findUserFileByPattern(String username, String suffix) throws IOException {
        Path dir = Paths.get(uploadDir);
        if (!Files.isDirectory(dir)) return Optional.empty();

        String glob = username + "_" + suffix + ".*"; // e.g., "jdoe_idFront.*"
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, glob)) {
            // pick newest by last modified time, if multiple
            Path newest = null;
            FileTime newestTime = null;
            for (Path p : stream) {
                if (Files.isRegularFile(p)) {
                    FileTime t = Files.getLastModifiedTime(p);
                    if (newest == null || (newestTime != null && t.compareTo(newestTime) > 0)) {
                        newest = p;
                        newestTime = t;
                    }
                }
            }
            return Optional.ofNullable(newest);
        }
    }

    private Resource toResource(Path path) {
        try {
            return new UrlResource(path.toUri());
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private String contentDispositionInline(String filename) {
        return "inline; filename=\"" + filename.replace("\"", "") + "\"";
    }
}