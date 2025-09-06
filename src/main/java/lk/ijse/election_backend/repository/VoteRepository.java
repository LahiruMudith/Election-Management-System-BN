package lk.ijse.election_backend.repository;

import lk.ijse.election_backend.entity.User;
import lk.ijse.election_backend.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Integer> {
    Optional<Vote> findByElection_IdAndVoter_Id(Integer electionId, Integer voterId);
}
