package lk.ijse.election_backend.controller;

import lk.ijse.election_backend.dto.ApiResponse;
import lk.ijse.election_backend.dto.ElectionDto;
import lk.ijse.election_backend.dto.UserDto;
import lk.ijse.election_backend.entity.Election;
import lk.ijse.election_backend.service.CandidateService;
import lk.ijse.election_backend.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/election")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class ElectionController {
    private final ElectionService electionService;
    private final CandidateService candidateService;

    @GetMapping(value = "/getAll")
    public ResponseEntity<ApiResponse> getAllElections() {
        List<Election> all = electionService.getAll();
        if (all.isEmpty()) {
            return ResponseEntity.status(404).body(new ApiResponse(404, "No Elections Found", null));
        }
        return ResponseEntity.status(200).body(new ApiResponse(200, "Success", all));
    }

    @GetMapping("get/{id}")
    public ApiResponse getElectionById(@PathVariable Integer id) {
        Election election = electionService.getById(id);
        if (election == null) {
            return new ApiResponse(404, "Election Not Found", null);
        }
        return new ApiResponse(200, "Success", election);
    }

    @PostMapping(value = "/save")
    public ResponseEntity<ApiResponse> saveElection(@RequestBody ElectionDto electionDto) {
        System.out.println(electionDto);
        String response = electionService.save(electionDto);
        if (response.equals("Election Already Exists")) {
            return ResponseEntity.status(409).body(new ApiResponse(409, response, null));
        }
        Election election = electionService.getByTitle(electionDto.getTitle());
        electionDto.getCandidates().forEach(candidateDto -> {
            candidateService.updateElection(candidateDto.getId(), election.getId());
        });
        return ResponseEntity.status(201).body(new ApiResponse(201, response, null));
    }

    @PutMapping(value = "/update")
    public ApiResponse updateElection(@RequestBody ElectionDto electionDto) {
        String response = electionService.update(electionDto);
        return new ApiResponse(200, response, null);
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse deleteElection(@PathVariable Integer id) {
        String response = electionService.delete(id);
        return new ApiResponse(200, response, null);
    }
}
