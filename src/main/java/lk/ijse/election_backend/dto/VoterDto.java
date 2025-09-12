package lk.ijse.election_backend.dto;

import jakarta.persistence.*;
import lk.ijse.election_backend.entity.User;
import lk.ijse.election_backend.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class VoterDto {
    private Integer id;

    private User userId;

    private String nicNumber;

    private String phoneNumber;

    private String fullName;

    private String district;

    private String verified;

    private boolean isActive;

    private Timestamp creatAt;
}
