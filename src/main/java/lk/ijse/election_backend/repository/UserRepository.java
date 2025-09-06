package lk.ijse.election_backend.repository;

import lk.ijse.election_backend.entity.User;
import lk.ijse.election_backend.entity.Voter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByEmailAndIsActiveTrue(String email);
}
