package lk.ijse.election_backend.dto;

import jakarta.persistence.*;
import lk.ijse.election_backend.entity.Candidate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class PartiesDto {
    private Integer id;

    private String name;

    private String description;

    private String symbol;

    private String color;

    private String leaderName;

    private String founderYear;

    private boolean isActive;
}
