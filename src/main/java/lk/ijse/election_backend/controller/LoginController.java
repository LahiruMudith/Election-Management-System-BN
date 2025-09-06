package lk.ijse.election_backend.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lk.ijse.election_backend.dto.ApiResponse;
import lk.ijse.election_backend.dto.LoginResponseDto;
import lk.ijse.election_backend.dto.UserDto;
import lk.ijse.election_backend.service.UserService;
import lk.ijse.election_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RequestMapping("/api/log")
@RestController
@RequiredArgsConstructor
@CrossOrigin(
        origins = "http://localhost:3002", // Frontend URL (no '*')
        allowCredentials = "true"
)
public class LoginController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

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

    @GetMapping(value = "/login", params = {"email", "password"})
    public ApiResponse loginUser(@RequestParam String email, @RequestParam String password, HttpServletResponse response) {
        LoginResponseDto loginResponse = userService.login(email, password);
        if (loginResponse == null) {
            return new ApiResponse(404, "Invalid Credentials", null);
        }

//        String token = loginResponse.getAccessToken();
//        Cookie cookie = new Cookie("token", token);
//        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // set false if not using HTTPS locally
//        cookie.setPath("/");
//        cookie.setMaxAge(24 * 60 * 60); // 1 day
//
//        response.addCookie(cookie); // <--- This sends the cookie to the client
//        // Add this after response.addCookie(cookie);
//        response.setHeader("Set-Cookie",
//                String.format("token=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=Strict",
//                        token, 24 * 60 * 60)
//        );

        return new ApiResponse(200, "Login Successful", loginResponse);
    }
}
