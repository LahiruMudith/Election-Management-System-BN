package lk.ijse.election_backend.repository;

import lk.ijse.election_backend.entity.Election;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ElectionRepository extends JpaRepository<Election, Integer> {
    Optional<Election> findByTitle(String username);
}
