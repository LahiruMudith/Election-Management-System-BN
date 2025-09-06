package lk.ijse.election_backend.dto;

import jakarta.persistence.*;
import lk.ijse.election_backend.entity.Election;
import lk.ijse.election_backend.entity.Parties;
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
public class CandidateDto {
    private Integer id;
    private User userId;
    private Election electionId;
    private Parties partyId;
    private String fullName;
    private int age;
    private String profession;
    private String manifesto;
    private boolean isApproved;
    private boolean isActive;
    private Timestamp createdAt;
    private String nicFrontImg;
    private String nicBackImg;
    private String selfieImg;
}
