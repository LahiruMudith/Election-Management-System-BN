package lk.ijse.election_backend.service;

import jakarta.transaction.Transactional;
import lk.ijse.election_backend.dto.CandidateDto;
import lk.ijse.election_backend.dto.UserDto;
import lk.ijse.election_backend.entity.Candidate;
import lk.ijse.election_backend.exception.UserAlreadyRegisteredException;
import lk.ijse.election_backend.repository.CandidateRepository;
import lk.ijse.election_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateService {
    private final CandidateRepository candidateRepository;

    public List<Candidate> getAll(){
        return candidateRepository.findAll();
    }

    public String save(CandidateDto candidateDto) {
        if (candidateRepository.findByUserId(candidateDto.getUserId()).isPresent()) {
            throw new UserAlreadyRegisteredException("Candidate Already Registered");
        }

        Candidate candidate = Candidate.builder()
                .id(candidateDto.getId())
                .userId(candidateDto.getUserId())
                .electionId(candidateDto.getElectionId())
                .partyId(candidateDto.getPartyId())
                .fullName(candidateDto.getFullName())
                .age(candidateDto.getAge())
                .profession(candidateDto.getProfession())
                .manifesto(candidateDto.getManifesto())
                .isApproved(candidateDto.isApproved())
                .isActive(candidateDto.isActive())
                .createdAt(candidateDto.getCreatedAt())
                .nicFrontImg(candidateDto.getNicFrontImg())
                .nicBackImg(candidateDto.getNicBackImg())
                .selfieImg(candidateDto.getSelfieImg())
                .build();

        candidateRepository.save(candidate);
        return "Candidate Registered Successfully";
    }

    public String update(CandidateDto candidateDto) {
        if (candidateRepository.findById(candidateDto.getId()).isEmpty()){
            throw new RuntimeException("Candidate Not Found");
        }
        Optional<Candidate> candidate = candidateRepository.findById(candidateDto.getId());

        candidate.ifPresent(c -> {
            c.setElectionId(candidateDto.getElectionId());
            c.setPartyId(candidateDto.getPartyId());
            c.setFullName(candidateDto.getFullName());
            c.setAge(candidateDto.getAge());
            c.setProfession(candidateDto.getProfession());
            c.setManifesto(candidateDto.getManifesto());
            c.setApproved(candidateDto.isApproved());
            c.setActive(candidateDto.isActive());
            c.setNicFrontImg(candidateDto.getNicFrontImg());
            c.setNicBackImg(candidateDto.getNicBackImg());
            c.setSelfieImg(candidateDto.getSelfieImg());
        });
        candidateRepository.save(candidate.get());
        return "Candidate Update Successfully";
    }

    @Transactional
    public String delete(Integer id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate Not Found"));

        candidate.setActive(false);
        candidateRepository.save(candidate);
        return "Candidate Deleted Successfully";
    }


    public Candidate getById(Integer id){
        return candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate Not Found"));
    }
}
