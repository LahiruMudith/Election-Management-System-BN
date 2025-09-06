package lk.ijse.election_backend.dto;

import jakarta.persistence.*;
import lk.ijse.election_backend.entity.Candidate;
import lk.ijse.election_backend.entity.Election;
import lk.ijse.election_backend.entity.Voter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@Builder
public class VoteDto {
    private Integer id;

    private Election election;

    private Candidate candidate;

    private Voter voter;

    private String district;

    private Timestamp voteTimestamp;
}
