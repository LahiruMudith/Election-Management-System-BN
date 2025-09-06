package lk.ijse.election_backend.service;

import lk.ijse.election_backend.dto.ElectionDto;
import lk.ijse.election_backend.entity.Election;
import lk.ijse.election_backend.entity.User;
import lk.ijse.election_backend.repository.ElectionRepository;
import lk.ijse.election_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ElectionService {
    private final ElectionRepository electionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public List<Election> getAll(){
        return electionRepository.findAll();
    }

    public String save(ElectionDto electionDto) {
        if (electionRepository.findByTitle(electionDto.getTitle()).isPresent()) {
            throw new RuntimeException("Election Already Registered");
        }

        Election election = Election.builder()
                .id(electionDto.getId())
                .title(electionDto.getTitle())
                .description(electionDto.getDescription())
                .startDate(electionDto.getStartDate())
                .endDate(electionDto.getEndDate())
                .status(Election.ElectionStatus.valueOf(electionDto.getStatus()))
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();

        electionRepository.save(election);
        return "Election Registered Successfully";
    }

    public String update(ElectionDto electionDto) {
        if (electionRepository.findById(electionDto.getId()).isEmpty()){
            throw new RuntimeException("Election Not Found");
        }
        Election election = electionRepository.findById(electionDto.getId()).orElseThrow(() -> new RuntimeException("Election Not Found"));

        election.setTitle(electionDto.getTitle());
        election.setDescription(electionDto.getDescription());
        election.setStatus(Election.ElectionStatus.valueOf(electionDto.getStatus()));

        electionRepository.save(election);
        return "Election Update Successfully";
    }

    public String delete(Integer id) {
        if (electionRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Election Not Found");
        }
        electionRepository.deleteById(id);
        return "Election Deleted Successfully";
    }

    public Election getById(Integer id){
        return electionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Election Not Found"));
    }
}
