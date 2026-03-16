package lk.ijse.election_backend.service;

import lk.ijse.election_backend.dto.VoteDto;
import lk.ijse.election_backend.entity.Vote;

import java.util.List;

public interface VoteService {
    List<Vote> getAll();
    String save(VoteDto voteDto);
    Vote getById(Integer id);
}
