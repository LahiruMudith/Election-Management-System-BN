package lk.ijse.election_backend.dto;

import jakarta.persistence.*;
import lk.ijse.election_backend.entity.Candidate;
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
public class ElectionDto {

    private Integer id;

    private String title;

    private String description;

    private String type;

    private String startDate;

    private String endDate;

    private List<String> districts;

    private List<CandidateDto> candidates;

    private String status;

    private Timestamp createdAt;
}
