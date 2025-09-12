package lk.ijse.election_backend.controller;

import lk.ijse.election_backend.dto.ApiResponse;
import lk.ijse.election_backend.dto.UserDto;
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
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RequestMapping("api/v1/voter")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")

public class VoterController {
    private final VoterService voterService;
    private final UserService userService;

    @GetMapping(value = "/getAll")
    public ApiResponse getAllVoters() {
        List<Voter> all = voterService.getAll();
        if (all.isEmpty()) {
            return new ApiResponse(404, "No Voters Found", null);
        }
        all.removeIf(voter -> !voter.isActive());
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

        //save voter nic photos and selfie
        String uploadDir = "src/main/resources/assets/voterPic"; // You can customize this path
        String idFrontName = null;
        String idBackName = null;
        String selfieName = null;

        if (nicFront != null && !nicFront.isEmpty()) {
            idFrontName = username + "_idFront" + getExtension(nicFront.getOriginalFilename());
            saveFile(uploadDir, nicFront, idFrontName);
        }
        if (nicBack != null && !nicBack.isEmpty()) {
            idBackName =username + "_idBack" + getExtension(nicBack.getOriginalFilename());
            saveFile(uploadDir, nicBack, idBackName);
        }
        if (selfie != null && !selfie.isEmpty()) {
            selfieName =  username + "_selfie" + getExtension(selfie.getOriginalFilename());
            saveFile(uploadDir, selfie, selfieName);
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
                .build();

        String message = voterService.save(VoterDto.builder()
                .userId(voter.getUserId())
                .nicNumber(voter.getNicNumber())
                .phoneNumber(voter.getPhoneNumber())
                .fullName(voter.getFullName())
                .district(voter.getDistrict())
                .isActive(voter.isActive())
                .creatAt(voter.getCreatAt())
                .build()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(201, message, null));
    }

    public String getExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
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
}
