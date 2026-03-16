package lk.ijse.election_backend.service;

import lk.ijse.election_backend.dto.CandidateDto;
import lk.ijse.election_backend.entity.Candidate;

import java.util.List;

public interface CandidateService {
    List<Candidate> getAll();
    String save(CandidateDto candidateDto);
    String update(CandidateDto candidateDto);
    String delete(Integer id);
    Candidate getById(Integer id);
    String updateElection(Integer candidateId, Integer electionId);
    String verifyCandidate(Integer id);
}
