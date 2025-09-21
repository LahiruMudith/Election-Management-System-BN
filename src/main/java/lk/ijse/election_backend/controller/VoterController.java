package lk.ijse.election_backend.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lk.ijse.election_backend.dto.ApiResponse;
import lk.ijse.election_backend.dto.VoterDto;
import lk.ijse.election_backend.entity.User;
import lk.ijse.election_backend.entity.Voter;
import lk.ijse.election_backend.service.UserService;
import lk.ijse.election_backend.service.VoterService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

@RequestMapping("api/v1/voter")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

public class VoterController {
    private final VoterService voterService;
    private final UserService userService;
    private final Cloudinary cloudinary;

    @GetMapping(value = "/getAll")
    public ApiResponse getAllVoters() {
        List<Voter> all = voterService.getAll();
        if (all.isEmpty()) {
            return new ApiResponse(404, "No Voters Found", null);
        }
//        all.removeIf(voter -> !voter.isActive());
        return new ApiResponse(200, "Success", all);
    }

    @GetMapping("get/{id}")
    public ApiResponse getVoterById(@PathVariable Integer id) {
        Voter voter = voterService.getById(id);
        if (voter == null) {
            return new ApiResponse(404, "Voter Not Found", null);
        }
        return new ApiResponse(200, "Success", voter);
    }

    @GetMapping("get")
    public ApiResponse getVoter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        User userByUserName = userService.getUserByUserName(username);
        Voter voter = voterService.getVoterByUserId(userByUserName.getId());


        return new ApiResponse(200, "Success", voter);
    }

    @PostMapping(value = "/save")
    public ApiResponse saveVoter(@RequestBody VoterDto voterDto) {
        String response = voterService.save(voterDto);
        return new ApiResponse(201, response, null);
    }

    @PutMapping(value = "/update")
    public ApiResponse updateVoter(@RequestBody VoterDto voterDto) {
        String response = voterService.update(voterDto);
        return new ApiResponse(200, response, null);
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse deleteVoter(@PathVariable Integer id) {
        String response = voterService.delete(id);
        return new ApiResponse(200, response, null);
    }

    @SneakyThrows
    @PostMapping(value = "/verifyNic", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> verifyVoter(
            @RequestParam("nicNumber") String nicNumber,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("fullName") String fullName,
            @RequestParam("district") String district,
            @RequestParam("username") String username,
            @RequestParam("nicFront") MultipartFile nicFront,
            @RequestParam("nicBack") MultipartFile nicBack,
            @RequestParam("selfie") MultipartFile selfie) {

        String nicFrontUrl = null, nicBackUrl = null, selfieUrl = null;

        try {
            if (nicFront != null && !nicFront.isEmpty()) {
                Map result = cloudinary.uploader().upload(nicFront.getBytes(), ObjectUtils.emptyMap());
                nicFrontUrl = (String) result.get("secure_url");
            }

            if (nicBack != null && !nicBack.isEmpty()) {
                Map result = cloudinary.uploader().upload(nicBack.getBytes(), ObjectUtils.emptyMap());
                nicBackUrl = (String) result.get("secure_url");
            }

            if (selfie != null && !selfie.isEmpty()) {
                Map result = cloudinary.uploader().upload(selfie.getBytes(), ObjectUtils.emptyMap());
                selfieUrl = (String) result.get("secure_url");
            }
        } catch (IOException e) {
            return ResponseEntity.status(500).body(new ApiResponse(500, "Image Save failed", null));
        }

        //save voter details to the database
        User userByUserName = userService.getUserByUserName(username);
        Voter voter = Voter.builder()
                .userId(userByUserName)
                .nicNumber(nicNumber)
                .phoneNumber(phoneNumber)
                .fullName(fullName)
                .district(district)
                .isActive(true)
                .creatAt(new java.sql.Timestamp(System.currentTimeMillis()))
                .nicFrontImg(nicFrontUrl)
                .nicBackImg(nicBackUrl)
                .selfieImg(selfieUrl)
                .build();

        String message = voterService.save(VoterDto.builder()
                .userId(voter.getUserId())
                .nicNumber(voter.getNicNumber())
                .phoneNumber(voter.getPhoneNumber())
                .fullName(voter.getFullName())
                .district(voter.getDistrict())
                .isActive(voter.isActive())
                .creatAt(voter.getCreatAt())
                .nicFrontImgUrl(voter.getNicFrontImg())
                .nicBackImgUrl(voter.getNicBackImg())
                .selfieImgUrl(voter.getSelfieImg())
                .build()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(201, message, null));
    }

    public void saveFile(String uploadDir, MultipartFile file, String newFilename) throws IOException {
        if (file != null && !file.isEmpty()) {
            Path dirPath = Paths.get(uploadDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            Path filePath = dirPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    @PatchMapping(value = "/verify/{id}")
    public ResponseEntity<ApiResponse> verifyVoter(@PathVariable Integer id) {
        String response = voterService.verify(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(200, response, null));
    }
    @PatchMapping(value = "/reject/{id}")
    public ResponseEntity<ApiResponse> rejectVoter(@PathVariable Integer id) {
        String response = voterService.reject(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ApiResponse(200, response, null));
    }
}
