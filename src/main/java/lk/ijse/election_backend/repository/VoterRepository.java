package lk.ijse.election_backend.repository;

import lk.ijse.election_backend.entity.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoterRepository extends JpaRepository<Voter, Integer> {
    Optional<Voter> findByNicNumber(String nicNumber);
}
