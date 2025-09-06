package lk.ijse.election_backend.controller;

import lk.ijse.election_backend.dto.ApiResponse;
import lk.ijse.election_backend.dto.LoginResponseDto;
import lk.ijse.election_backend.dto.UserDto;
import lk.ijse.election_backend.entity.User;
import lk.ijse.election_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    @GetMapping(value = "/getAllUsers")
    public ApiResponse getAllUsers() {
        List<User> all = userService.getAll();
        if (all.isEmpty()) {
            return new ApiResponse(404, "No Users Found", null);
        }
        return new ApiResponse(200, "Success", all);
    }

    @GetMapping("getUser/{id}")
    public ApiResponse getUserById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            return new ApiResponse(404, "User Not Found", null);
        }
        return new ApiResponse(200, "Success", user);
    }

    @PutMapping(value = "/updateUser")
    public ApiResponse updateUser(@RequestBody UserDto userDto) {
        String response = userService.update(userDto);
        return new ApiResponse(200, response, null);
    }

    @DeleteMapping("deleteUser/{id}")
    public ApiResponse deleteUser(@PathVariable Integer id) {
        String response = userService.delete(id);
        return new ApiResponse(200, response, null);
    }
}
