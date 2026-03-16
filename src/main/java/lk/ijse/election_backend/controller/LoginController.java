package lk.ijse.election_backend.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.election_backend.dto.ApiResponse;
import lk.ijse.election_backend.dto.CandidateDto;
import lk.ijse.election_backend.dto.LoginResponseDto;
import lk.ijse.election_backend.dto.UserDto;
import lk.ijse.election_backend.entity.Parties;
import lk.ijse.election_backend.entity.User;
import lk.ijse.election_backend.service.CandidateService;
import lk.ijse.election_backend.service.PartiesService;
import lk.ijse.election_backend.service.UserService;
import lk.ijse.election_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequestMapping("/api/log")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class LoginController {
    private final CandidateService candidateService;
    private final UserService userService;
    private final PartiesService partiesService;
    private final JwtUtil jwtUtil;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private Cloudinary cloudinary;

    @PostMapping(value = "loginWithGoogle", consumes = "application/json", produces = "application/json")
    public ApiResponse loginWithGoogle(@RequestBody UserDto userDto, HttpServletResponse httpResponse) {
        if (userService.isUserExist(userDto.getEmail())) {
            LoginResponseDto loginResponse = userService.login(userDto.getEmail(), userDto.getPassword());
            return new ApiResponse(200, "User Already Exist", loginResponse);
        } else {
            userDto.setRole("USER");
            userDto.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            String response = userService.save(userDto);
            if (!response.equals("User Registered Successfully")){
                return new ApiResponse(500, "Registration Failed", null);
            }
            new Thread(() -> {
                MimeMessage mimeMessage = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

                String htmlMsg = "<h3>Election System Login Details</h3>"
                        + "<p><strong>Email:</strong> <span style='color:#2b7a78;'>" + userDto.getEmail() + "</span></p>"
                        + "<p><strong>Password:</strong> <span style='color:#d7263d;'>" + userDto.getPassword() + "</span></p>";

                try {
                    helper.setTo(userDto.getEmail());
                    helper.setSubject("Election System Login Password");
                    helper.setText(htmlMsg, true); // true indicates HTML
                    helper.setFrom("lahimudith@gmail.com");
                } catch (MessagingException e) {
                    throw new RuntimeException(e);
                }


                mailSender.send(mimeMessage);
            }).start();

//            ApiResponse apiResponse = loginUser(userDto.getEmail(), userDto.getPassword(), null);
//            System.out.println(apiResponse);
//            if (apiResponse.getStatus() != 200) {
//                return new ApiResponse(200, "Login Failed After Registration ", null);
//            }
            return new ApiResponse(200, "Google Register Successful", null);
        }
    }

    @GetMapping(value = "/checkToken", params = {"token"})
    public ApiResponse checkToken(@RequestParam String token) {
        boolean isValid = jwtUtil.validateToken(token);
        System.out.println("Token validation result: " + isValid);
        if (isValid) {
            return new ApiResponse(200, "Token is valid", null);
        } else {
            return new ApiResponse(401, "Invalid token", null);
        }
    }

    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ApiResponse saveUser(@RequestBody UserDto userDto) {
        System.out.println(userDto.getEmail());
        System.out.println(userDto.getPassword());
        System.out.println(userDto.getUsername());
        userDto.setRole("USER");
        String response = userService.save(userDto);
        return new ApiResponse(201, response, null);
    }

    @PostMapping(value = "/registerCandidate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> registerCandidate(
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("fullName") String fullName,
            @RequestParam("age") String age,
            @RequestParam("profession") String profession,
            @RequestParam("manifesto") String manifesto,
            @RequestParam("partyId") String partyId,
            @RequestParam(value = "idFront") MultipartFile idFront,
            @RequestParam(value = "idBack") MultipartFile idBack,
            @RequestParam(value = "selfie") MultipartFile selfie
    ) throws IOException {

        // Debug prints to check incoming data
        System.out.println("=== Candidate Registration Data ===");
        System.out.println("Email: " + email);
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        System.out.println("Full Name: " + fullName);
        System.out.println("Age: " + age);
        System.out.println("Profession: " + profession);
        System.out.println("Manifesto: " + manifesto);
        System.out.println("Party ID: " + partyId);
        System.out.println("ID Front file: " + (idFront != null ? idFront.getOriginalFilename() + " (" + idFront.getSize() + " bytes)" : "null"));
        System.out.println("ID Back file: " + (idBack != null ? idBack.getOriginalFilename() + " (" + idBack.getSize() + " bytes)" : "null"));
        System.out.println("Selfie file: " + (selfie != null ? selfie.getOriginalFilename() + " (" + selfie.getSize() + " bytes)" : "null"));
        System.out.println("=================================");

        System.out.println("Starting partyId validation");
        if (partyId == null || partyId.trim().isEmpty()) {
            System.out.println("Party ID validation failed - empty or null");
            return ResponseEntity.status(400).body(new ApiResponse(400, "Party is required", null));
        }
        System.out.println("Party ID validation passed");

        System.out.println("Starting number parsing for partyId and age");
        int parsedPartyId;
        int parsedAge;
        try {
            parsedPartyId = Integer.parseInt(partyId);
            parsedAge = Integer.parseInt(age);
            System.out.println("Number parsing successful - PartyId: " + parsedPartyId + ", Age: " + parsedAge);
        } catch (NumberFormatException e) {
            System.out.println("Number parsing failed: " + e.getMessage());
            return ResponseEntity.status(400).body(new ApiResponse(400, "Invalid age or party ID", null));
        }

        String nicFrontUrl = null, nicBackUrl = null, selfieUrl = null;
        System.out.println("Starting image upload to Cloudinary");

        try {
            if (idFront != null && !idFront.isEmpty()) {
                System.out.println("Uploading ID front image");
                Map result = cloudinary.uploader().upload(idFront.getBytes(), ObjectUtils.emptyMap());
                nicFrontUrl = (String) result.get("secure_url");
                System.out.println("ID front uploaded successfully: " + nicFrontUrl);
            }

            if (idBack != null && !idBack.isEmpty()) {
                System.out.println("Uploading ID back image");
                Map result = cloudinary.uploader().upload(idBack.getBytes(), ObjectUtils.emptyMap());
                nicBackUrl = (String) result.get("secure_url");
                System.out.println("ID back uploaded successfully: " + nicBackUrl);
            }

            if (selfie != null && !selfie.isEmpty()) {
                System.out.println("Uploading selfie image");
                Map result = cloudinary.uploader().upload(selfie.getBytes(), ObjectUtils.emptyMap());
                selfieUrl = (String) result.get("secure_url");
                System.out.println("Selfie uploaded successfully: " + selfieUrl);
            }
            System.out.println("All image uploads completed successfully");
        } catch (IOException e) {
            System.out.println("Image upload failed: " + e.getMessage());
            return ResponseEntity.status(500).body(new ApiResponse(500, "Image Save failed", null));
        }


        System.out.println("Saving user to database");
        userService.save(UserDto.builder()
                .email(email)
                .username(username)
                .password(password)
                .role("CANDIDATE")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build()
        );
        System.out.println("User saved successfully");
        
        System.out.println("Retrieving user by email: " + email);
        User userByEmail = userService.getUserByEmail(email);
        System.out.println("User retrieved: " + (userByEmail != null ? userByEmail.getUsername() : "null"));

        System.out.println("Checking user active status");
        if (userByEmail.isActive()){
            System.out.println("User is active, saving candidate data");
            String save = candidateService.save(CandidateDto.builder()
                    .userId(userByEmail)
                    .fullName(fullName)
                    .age(parsedAge)
                    .profession(profession)
                    .manifesto(manifesto)
                    .partyId(parsedPartyId)
                    .isApproved(false)
                    .isActive(true)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .nicFrontImg(nicFrontUrl)
                    .nicBackImg(nicBackUrl)
                    .selfieImg(selfieUrl)
                    .build()
            );
            System.out.println("Candidate save result: " + save);
            if (!save.equals("Candidate Registered Successfully")) {
                System.out.println("Candidate registration failed");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse(500, "Registration Failed: " + save, null));
            }
            System.out.println("Candidate registration successful");
        } else {
            System.out.println("User is not active");
        }

        System.out.println("Returning final success response");
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(201, "Candidate Registered!", null));
    }


    @GetMapping(value = "/login", params = {"email", "password"})
    public ApiResponse loginUser(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
        System.out.println("=== LoginController.loginUser() started ===");
        System.out.println("Login attempt - Email: " + email + ", Password: [PROTECTED]");
        
        System.out.println("Calling userService.login()");
        LoginResponseDto loginResponse = userService.login(email, password);
        System.out.println("UserService login response: " + loginResponse);
        
        if (loginResponse == null) {
            System.out.println("Login failed - Invalid credentials");
            return new ApiResponse(404, "Invalid Credentials", null);
        }
        System.out.println("Login successful - proceeding with token generation");

        String token = loginResponse.getAccessToken();
        System.out.println("Generated Token: " + token);
        
        System.out.println("Creating and setting cookie");
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // set false if not using HTTPS locally
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 day

        System.out.println("Adding cookie to response");
        response.addCookie(cookie); // <--- This sends the cookie to the client
        // Add this after response.addCookie(cookie);
        response.setHeader("Set-Cookie",
                String.format("token=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=Strict",
                        token, 24 * 60 * 60)
        );
        
        System.out.println("LoginResponse details: " + loginResponse);
        System.out.println("=== LoginController.loginUser() completed ===");
        return new ApiResponse(200, "Login Successful", loginResponse);
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
    public String getExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }

    @GetMapping(value = "/getAllParties")
    public ApiResponse getAllParties() {
        List<Parties> all = partiesService.getAllParties();
        if (all.isEmpty()) {
            return new ApiResponse(404, "No Parties Found", null);
        }
        return new ApiResponse(200, "Success", all);
    }
}
