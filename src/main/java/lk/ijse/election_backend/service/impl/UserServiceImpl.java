package lk.ijse.election_backend.service.impl;

import lk.ijse.election_backend.dto.LoginResponseDto;
import lk.ijse.election_backend.dto.UserDto;
import lk.ijse.election_backend.entity.User;
import lk.ijse.election_backend.repository.UserRepository;
import lk.ijse.election_backend.service.UserService;
import lk.ijse.election_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponseDto login(String email, String password) {
        User user = userRepository.findByEmailAndIsActiveTrue(email).orElseThrow(
                () -> new RuntimeException("User Not Found")
        );

        if (!passwordEncoder.matches(password, user.getPassword())){
            throw new RuntimeException("Invalid Password");
        }
        String token = jwtUtil.generateToken(user.getUsername());
        return new LoginResponseDto(token, user.getUsername(), user.getRole().name());
    }

    public List<User> getAll(){
        return userRepository.findAll();
    }

    public String save(UserDto userDto) {
        System.out.println("=== UserServiceImpl.save() started ===");
        System.out.println("Input UserDto: " + userDto);
        
        System.out.println("Checking if username already exists: " + userDto.getUsername());
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            System.out.println("Username already exists - throwing exception");
            throw new RuntimeException("User Already Registered");
        }
        System.out.println("Username is available");

        System.out.println("Building User entity");
        User user = User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(User.UserRole.valueOf(userDto.getRole()))
                .isActive(true)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        System.out.println("User entity created: " + user);
        System.out.println("Saving user to database");

        userRepository.save(user);
        System.out.println("User saved successfully");
        System.out.println("=== UserServiceImpl.save() completed ===");
        return "User Registered Successfully";
    }

    public String update(UserDto userDto) {
        if (userRepository.findById(userDto.getId()).isEmpty()){
            throw new RuntimeException("User Not Found");
        }

        User user = User.builder()
                .id(userDto.getId())
                .email(userDto.getEmail())
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(User.UserRole.valueOf(userDto.getRole()))
                .isActive(true)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        userRepository.save(user);
        return "User Update Successfully";
    }

    public String delete(Integer id) {
        if (userRepository.findById(id).isEmpty()) {
            throw new RuntimeException("User Not Found");
        }
        userRepository.deleteById(id);
        return "User Deleted Successfully";
    }

    public User getById(Integer id){
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public User getUserByEmail(String email){
        return userRepository.findByEmailAndIsActiveTrue(email)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }

    public boolean isUserExist(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email).isPresent();
    }

    public User getUserByUserName(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User Not Found"));
    }
}
