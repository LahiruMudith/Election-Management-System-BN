package lk.ijse.election_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Candidate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    private User userId;

    @ManyToOne
    @JoinColumn(name = "election_id")
    private Election electionId;

    @ManyToOne
    @JoinColumn(name = "party_id")
    private Parties partyId;

    @Column(nullable = false, unique = true)
    private String fullName;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false)
    private String profession;

    @Column(columnDefinition = "TEXT")
    private String manifesto;

    @Column(name = "is_approved", nullable = false)
    private boolean isApproved;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    @Column(name = "nic_front_img", columnDefinition = "TEXT", nullable = false)
    private String nicFrontImg;

    @Column(name = "nic_back_img", columnDefinition = "TEXT", nullable = false)
    private String nicBackImg;

    @Column(name = "selfie_img", columnDefinition = "TEXT", nullable = false)
    private String selfieImg;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Vote> votes;
}
