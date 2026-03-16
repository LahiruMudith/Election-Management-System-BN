package lk.ijse.election_backend.service;

import lk.ijse.election_backend.dto.VoterDto;
import lk.ijse.election_backend.entity.Voter;

import java.util.List;

public interface VoterService {
    List<Voter> getAll();
    Voter getVoterByUserId(Integer userId);
    String save(VoterDto voterDto);
    String update(VoterDto voterDto);
    String verify(Integer id);
    String reject(Integer id);
    String delete(Integer id);
    Voter getById(Integer id);
}
