package lk.ijse.election_backend.service;

import lk.ijse.election_backend.dto.LoginResponseDto;
import lk.ijse.election_backend.dto.UserDto;
import lk.ijse.election_backend.entity.User;

import java.util.List;

public interface UserService {
    LoginResponseDto login(String email, String password);
    List<User> getAll();
    String save(UserDto userDto);
    String update(UserDto userDto);
    String delete(Integer id);
    User getById(Integer id);
    User getUserByEmail(String email);
    boolean isUserExist(String email);
    User getUserByUserName(String username);
}
