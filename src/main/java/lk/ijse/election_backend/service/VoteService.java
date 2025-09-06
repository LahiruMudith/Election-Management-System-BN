package lk.ijse.election_backend.service;

import lk.ijse.election_backend.dto.UserDto;
import lk.ijse.election_backend.dto.VoteDto;
import lk.ijse.election_backend.entity.*;
import lk.ijse.election_backend.repository.CandidateRepository;
import lk.ijse.election_backend.repository.ElectionRepository;
import lk.ijse.election_backend.repository.VoteRepository;
import lk.ijse.election_backend.repository.VoterRepository;
import lk.ijse.election_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {
    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final VoterRepository voterRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public List<Vote> getAll(){
        return voteRepository.findAll();
    }

    public String save(VoteDto voteDto) {
        if (voteRepository.findByElection_IdAndVoter_Id(voteDto.getElection().getId(), voteDto.getVoter().getId()).isPresent()) {
            throw new RuntimeException("Vote Already Done");
        }

        Election election = electionRepository.findById(voteDto.getElection().getId())
                .orElseThrow(() -> new RuntimeException("Election Not Found"));

        Candidate candidate = candidateRepository.findById(voteDto.getCandidate().getId())
                .orElseThrow(() -> new RuntimeException("Candidate Not Found"));

        Voter voter = voterRepository.findById(voteDto.getVoter().getId())
                .orElseThrow(() -> new RuntimeException("Voter Not Found"));

        Vote vote = Vote.builder()
                        .id(voteDto.getId())
                        .election(election)
                        .candidate(candidate)
                        .voter(voter)
                        .voteTimestamp(new Timestamp(System.currentTimeMillis()))
                        .build();

        voteRepository.save(vote);
        return "Vote Successfully";
    }

    public Vote getById(Integer id){
        return voteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vote Not Found"));
    }
}
