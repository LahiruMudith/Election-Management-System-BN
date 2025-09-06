package lk.ijse.election_backend.repository;

import lk.ijse.election_backend.entity.Candidate;
import lk.ijse.election_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface CandidateRepository extends JpaRepository<Candidate, Integer> {
    Optional<Candidate> findByUserId(User userId);
}
