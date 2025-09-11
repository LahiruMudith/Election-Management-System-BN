package lk.ijse.election_backend.controller;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.election_backend.dto.ApiResponse;
import lk.ijse.election_backend.dto.CandidateDto;
import lk.ijse.election_backend.dto.LoginResponseDto;
import lk.ijse.election_backend.dto.UserDto;
import lk.ijse.election_backend.entity.User;
import lk.ijse.election_backend.service.CandidateService;
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
import java.util.UUID;

@RequestMapping("/api/log")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class LoginController {
    private final CandidateService candidateService;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    @Autowired
    private JavaMailSender mailSender;

    @PostMapping(value = "loginWithGoogle", consumes = "application/json", produces = "application/json")
    public ApiResponse loginWithGoogle(@RequestBody UserDto userDto) {
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

            ApiResponse apiResponse = loginUser(userDto.getEmail(), userDto.getPassword(), null);
            if (apiResponse.getStatus() != 200) {
                return new ApiResponse(500, "Login Failed After Registration ", null);
            }
            return new ApiResponse(200, "Login Successful", apiResponse.getData());
        }
    }

    @GetMapping(value = "/checkToken", params = {"token"})
    public ApiResponse checkToken(@RequestParam String token) {
        boolean isValid = jwtUtil.validateToken(token);
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
            @RequestParam(value = "idFront", required = false) MultipartFile idFront,
            @RequestParam(value = "idBack", required = false) MultipartFile idBack,
            @RequestParam(value = "selfie", required = false) MultipartFile selfie
    ) throws IOException {
        System.out.println("Called registerCandidate");
        System.out.println("email: " + email);
        // ... print others as needed
        if (idFront != null) System.out.println("idFront: " + idFront.getOriginalFilename());
        if (idBack != null) System.out.println("idBack: " + idBack.getOriginalFilename());
        if (selfie != null) System.out.println("selfie: " + selfie.getOriginalFilename());

        String uploadDir = "src/main/resources/assets/candidatePic"; // You can customize this path
        String idFrontName = null;
        String idBackName = null;
        String selfieName = null;

        if (idFront != null && !idFront.isEmpty()) {
            idFrontName = username + "_idFront" + getExtension(idFront.getOriginalFilename());
            saveFile(uploadDir, idFront, idFrontName);
        }
        if (idBack != null && !idBack.isEmpty()) {
            idBackName =username + "_idBack" + getExtension(idBack.getOriginalFilename());
            saveFile(uploadDir, idBack, idBackName);
        }
        if (selfie != null && !selfie.isEmpty()) {
            selfieName =  username + "_selfie" + getExtension(selfie.getOriginalFilename());
            saveFile(uploadDir, selfie, selfieName);
        }

        userService.save(UserDto.builder()
                .email(email)
                .username(username)
                .password(password)
                .role("CANDIDATE")
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build()
        );
        User userByEmail = userService.getUserByEmail(email);

        if (userByEmail.isActive()){
            String save = candidateService.save(CandidateDto.builder()
                    .userId(userByEmail)
                    .fullName(fullName)
                    .age(Integer.parseInt(age))
                    .profession(profession)
                    .manifesto(manifesto)
                    .isApproved(false)
                    .isActive(true)
                    .createdAt(new Timestamp(System.currentTimeMillis()))
                    .nicFrontImg(idFrontName)
                    .nicBackImg(idBackName)
                    .selfieImg(selfieName)
                    .build()
            );
            if (!save.equals("Candidate Registered Successfully")) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ApiResponse(500, "Registration Failed: " + save, null));
            }
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(201, "Candidate Registered!", null));
    }

    @GetMapping(value = "/login", params = {"email", "password"})
    public ApiResponse loginUser(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
        LoginResponseDto loginResponse = userService.login(email, password);
        if (loginResponse == null) {
            return new ApiResponse(404, "Invalid Credentials", null);
        }

        String token = loginResponse.getAccessToken();
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // set false if not using HTTPS locally
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 day

        response.addCookie(cookie); // <--- This sends the cookie to the client
        // Add this after response.addCookie(cookie);
        response.setHeader("Set-Cookie",
                String.format("token=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=Strict",
                        token, 24 * 60 * 60)
        );

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

}
