package lk.ijse.election_backend.controller;

import lk.ijse.election_backend.dto.ApiResponse;
import lk.ijse.election_backend.dto.CandidateDto;
import lk.ijse.election_backend.dto.UserDto;
import lk.ijse.election_backend.entity.Candidate;
import lk.ijse.election_backend.service.CandidateService;
import lk.ijse.election_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RequestMapping("/api/v1/candidate")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class CandidateController {
    private final CandidateService candidateService;
    private final UserService userService;

    @GetMapping("/getAll")
    public ApiResponse getAllCandidates() {
        List<Candidate> all = candidateService.getAll();
//        all.removeIf(candidate -> !candidate.isActive());
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

    @PatchMapping(value = "verify/{id}")
    public ResponseEntity verifyCandidate(@PathVariable Integer id) {
        String response = candidateService.verifyCandidate(id);
        return ResponseEntity.status(200).body(new ApiResponse(200, response, null));
    }
}
