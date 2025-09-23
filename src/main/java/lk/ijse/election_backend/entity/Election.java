package lk.ijse.election_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Election {

    public enum ElectionStatus {
        NOT_STARTED,     // Election has been scheduled but not yet begun
        VOTING_OPEN,     // Polls are open and voting is in progress
        VOTING_CLOSED,   // Polls have closed, votes being counted
        COUNTING,        // Votes are actively being counted
        COMPLETED,       // Counting finished, results announced
        CANCELLED,       // Election cancelled for some reason
        DISPUTED         // Results contested or under legal challenge
    }

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private String type;

    @Column(name = "start_date", nullable = false)
    private String startDate;

    @Column(name = "end_date", nullable = false)
    private String endDate;

    @ElementCollection
    @Column(nullable = false)
    private List<String> districts = new ArrayList<>();

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ElectionStatus status;

    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;

    @OneToMany(mappedBy = "electionId", cascade = CascadeType.ALL)
    private List<Candidate> candidates;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Vote> votes;
}
