package lk.ijse.election_backend.service;

import lk.ijse.election_backend.dto.ElectionDto;
import lk.ijse.election_backend.entity.Election;

import java.util.List;

public interface ElectionService {
    List<Election> getAll();
    String save(ElectionDto electionDto);
    String update(ElectionDto electionDto);
    String delete(Integer id);
    Election getById(Integer id);
    Election getByTitle(String title);
}
