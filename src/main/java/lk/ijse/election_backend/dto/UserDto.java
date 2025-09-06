package lk.ijse.election_backend.dto;

import jakarta.persistence.*;
import lk.ijse.election_backend.entity.Candidate;
import lk.ijse.election_backend.entity.Voter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;


@Data
@AllArgsConstructor
@Builder
public class UserDto {
    private Integer id;
    private String email;
    private String username;
    private String password;
    private String role;
    private boolean isActive;
    private Timestamp createdAt;
}
