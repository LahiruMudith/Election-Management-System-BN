package lk.ijse.election_backend.controller;

import lk.ijse.election_backend.dto.ApiResponse;
import lk.ijse.election_backend.dto.CandidateDto;
import lk.ijse.election_backend.entity.Candidate;
import lk.ijse.election_backend.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/candidate")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CandidateController {
    private final CandidateService candidateService;

    @GetMapping("/getAll")
    public ApiResponse getAllCandidates() {
        List<Candidate> all = candidateService.getAll();
        all.removeIf(candidate -> !candidate.isActive());
        return new ApiResponse(200, "Success", all);
    }

    @GetMapping("{id}")
    public ApiResponse getCandidateById(@PathVariable Integer id) {
        Candidate candidate = candidateService.getById(id);

        if (candidate == null || !candidate.isActive()) {
            return new ApiResponse(404, "Candidate Not Found", null);
        }
        return new ApiResponse(200, "Success", candidate);
    }

    @PostMapping(value = "/save" , produces = "application/json" , consumes = "application/json")
    public ApiResponse saveCandidates(@RequestBody CandidateDto candidate) {
        String response = candidateService.save(candidate);
        return new ApiResponse(201, response, null);
    }

    @PutMapping("/update")
    public ApiResponse updateCandidates(@RequestBody CandidateDto candidate) {
        String response = candidateService.update(candidate);
        return new ApiResponse(200, response, null);
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse deleteCandidates(@PathVariable Integer id) {
        String response = candidateService.delete(id);
        return new ApiResponse(200, response, null);
    }
}
