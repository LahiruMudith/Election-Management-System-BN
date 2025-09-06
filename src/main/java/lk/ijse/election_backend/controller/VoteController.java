package lk.ijse.election_backend.controller;

import lk.ijse.election_backend.dto.ApiResponse;
import lk.ijse.election_backend.dto.VoteDto;
import lk.ijse.election_backend.dto.VoterDto;
import lk.ijse.election_backend.entity.Vote;
import lk.ijse.election_backend.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/v1/vote")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VoteController {
    private final VoteService voteService;

    @GetMapping(value = "/getAll")
    public ApiResponse getAllVoters() {
        List<Vote> all = voteService.getAll();
        if (all.isEmpty()) {
            return new ApiResponse(404, "No Voters Found", null);
        }
        return new ApiResponse(200, "Success", all);
    }

    @PostMapping(value = "/save")
    public ApiResponse saveVoter(@RequestBody VoteDto voteDto) {
        String response = voteService.save(voteDto);
        return new ApiResponse(201, response, null);
    }
}
