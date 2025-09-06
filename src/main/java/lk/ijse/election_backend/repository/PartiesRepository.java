package lk.ijse.election_backend.repository;

import lk.ijse.election_backend.entity.Parties;
import lk.ijse.election_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartiesRepository extends JpaRepository<Parties, Integer> {
    Optional<Parties> findByName(String name);

    Optional<Parties> findBySymbol(String symbol);
}
