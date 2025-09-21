package lk.ijse.election_backend.service;

import lk.ijse.election_backend.dto.VoterDto;
import lk.ijse.election_backend.entity.User;
import lk.ijse.election_backend.entity.Voter;
import lk.ijse.election_backend.repository.VoterRepository;
import lk.ijse.election_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoterService {
    private final VoterRepository voterRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public List<Voter> getAll(){
        return voterRepository.findAll();
    }

    public Voter getVoterByUserId(Integer userId){
        return voterRepository.findByUserId_Id(userId).orElseThrow(() -> new RuntimeException("Voter Not Found"));
    }

    public String save(VoterDto voterDto) {
        if (voterRepository.findByNicNumber(voterDto.getNicNumber()).isPresent()){
            throw new RuntimeException("Voter Already Registered");
        }

        System.out.println("nicBackImg: " + voterDto.getNicFrontImgUrl());
        System.out.println("nicFrontImg: " + voterDto.getNicBackImgUrl());
        System.out.println("selfieImg: " + voterDto.getSelfieImgUrl());

        voterRepository.save(
                Voter.builder()
                        .id(voterDto.getId())
                        .userId(voterDto.getUserId())
                        .nicNumber(voterDto.getNicNumber())
                        .phoneNumber(voterDto.getPhoneNumber())
                        .fullName(voterDto.getFullName())
                        .district(voterDto.getDistrict())
                        .isActive(true)
                        .verified(Voter.VoterStatus.PENDING)
                        .creatAt(new Timestamp(System.currentTimeMillis()))
                        .nicFrontImg(voterDto.getNicFrontImgUrl())
                        .nicBackImg(voterDto.getNicBackImgUrl())
                        .selfieImg(voterDto.getSelfieImgUrl())
                        .build()
        );
        return "User Registered Successfully";
    }
    public String update(VoterDto voterDto) {
        Voter voter = voterRepository.findById(voterDto.getId())
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        voter.setFullName(voterDto.getFullName());
        voter.setDistrict(voterDto.getDistrict());
        voter.setPhoneNumber(voterDto.getPhoneNumber());

        voterRepository.save(voter);
        return "User Update Successfully";
    }

    public String verify(Integer id) {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        voter.setVerified(Voter.VoterStatus.VERIFIED);
        voterRepository.save(voter);
        return "User Verified Successfully";
    }

    public String reject(Integer id) {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        voter.setVerified(Voter.VoterStatus.REJECTED);
        voterRepository.save(voter);
        return "User Verified Successfully";
    }

    public String delete(Integer id) {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        voter.setActive(false);
        voterRepository.save(voter);
        return "User Deleted Successfully";
    }

    public Voter getById(Integer id) {
        Voter voter = voterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User Not Found"));

        if (!voter.isActive()) {
            throw new RuntimeException("User Deleted");
        }

        return voter;
    }

}
