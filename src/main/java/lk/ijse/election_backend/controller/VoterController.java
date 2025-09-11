package lk.ijse.election_backend.controller;

import lk.ijse.election_backend.dto.ApiResponse;
import lk.ijse.election_backend.dto.UserDto;
import lk.ijse.election_backend.dto.VoterDto;
import lk.ijse.election_backend.entity.Voter;
import lk.ijse.election_backend.service.VoterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/voter")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VoterController {
    private final VoterService voterService;

    @GetMapping(value = "/getAll")
    public ApiResponse getAllVoters() {
        List<Voter> all = voterService.getAll();
        if (all.isEmpty()) {
            return new ApiResponse(404, "No Voters Found", null);
        }
        all.removeIf(voter -> !voter.isActive());
        return new ApiResponse(200, "Success", all);
    }

    @GetMapping("get/{id}")
    public ApiResponse getVoterById(@PathVariable Integer id) {
        Voter voter = voterService.getById(id);
        if (voter == null) {
            return new ApiResponse(404, "Voter Not Found", null);
        }
        return new ApiResponse(200, "Success", voter);
    }

    @PostMapping(value = "/save")
    public ApiResponse saveVoter(@RequestBody VoterDto voterDto) {
        String response = voterService.save(voterDto);
        return new ApiResponse(201, response, null);
    }

    @PutMapping(value = "/update")
    public ApiResponse updateVoter(@RequestBody VoterDto voterDto) {
        String response = voterService.update(voterDto);
        return new ApiResponse(200, response, null);
    }

    @DeleteMapping("delete/{id}")
    public ApiResponse deleteVoter(@PathVariable Integer id) {
        String response = voterService.delete(id);
        return new ApiResponse(200, response, null);
    }

    @PostMapping(value = "verifyVoter" )
    public ResponseEntity<ApiResponse> verifyVoter(@RequestBody VoterDto voterDto) {

        return ResponseEntity.ok(new ApiResponse(200, "Success", null));
    }
}
