package lk.ijse.election_backend.service;

import lk.ijse.election_backend.dto.LoginResponseDto;
import lk.ijse.election_backend.dto.UserDto;
import lk.ijse.election_backend.entity.User;
import lk.ijse.election_backend.repository.UserRepository;
import lk.ijse.election_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
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
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new RuntimeException("User Already Registered");
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

        System.out.println(user);

        userRepository.save(user);
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
}
