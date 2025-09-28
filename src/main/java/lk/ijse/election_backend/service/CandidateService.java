package lk.ijse.election_backend.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lk.ijse.election_backend.dto.CandidateDto;
import lk.ijse.election_backend.entity.Candidate;
import lk.ijse.election_backend.entity.Election;
import lk.ijse.election_backend.entity.Parties;
import lk.ijse.election_backend.exception.UserAlreadyRegisteredException;
import lk.ijse.election_backend.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CandidateService {
    private final CandidateRepository candidateRepository;
    private final UserService userService;
    private final PartiesService partiesService;
    private final ElectionService electionService;

    public List<Candidate> getAll(){
        return candidateRepository.findAll();
    }

    public String save(CandidateDto candidateDto) {
        if (candidateRepository.findByUserId(candidateDto.getUserId()).isPresent()) {
            throw new UserAlreadyRegisteredException("Candidate Already Registered");
        }

        Parties party = partiesService.getPartyById(candidateDto.getPartyId());

        Candidate candidate = Candidate.builder()
                .id(candidateDto.getId())
                .userId(candidateDto.getUserId())
                .electionId(candidateDto.getElectionId())
                .partyId(party)
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

        Parties parties = partiesService.getPartyById(candidateDto.getPartyId());

        Election election = electionService.getById(candidateDto.getElectionId().getId());


        candidate.ifPresent(c -> {
            c.setElectionId(election);
            c.setPartyId(parties);
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

    public String updateElection(Integer candidateId, Integer electionId) {
        // Get the election entity by ID
        Election election = electionService.getById(electionId);
        if (election == null) {
            throw new EntityNotFoundException("Election not found with id: " + electionId);
        }

        // Get the candidate entity by ID
        Candidate candidate = candidateRepository.getReferenceById(candidateId);

        // Assign the election to the candidate
        candidate.setElectionId(election); // If using just the ID
        // or if you have a relationship, use:
        // candidate.setElection(election);

        // Save the updated candidate
        candidateRepository.save(candidate);
        return "Candidate Election Updated Successfully";
    }

    public String verifyCandidate(Integer id) {
        Candidate candidate = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate Not Found"));

        if (candidate.isApproved()) {
            candidate.setApproved(false);
            return "Candidate Rejected";
        }
        candidate.setApproved(true);
        candidateRepository.save(candidate);
        return "Candidate Verified";
    }
}
