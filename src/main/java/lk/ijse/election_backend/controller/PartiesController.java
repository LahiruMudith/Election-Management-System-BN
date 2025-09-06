package lk.ijse.election_backend.controller;

import lk.ijse.election_backend.dto.ApiResponse;
import lk.ijse.election_backend.dto.PartiesDto;
import lk.ijse.election_backend.entity.Parties;
import lk.ijse.election_backend.service.PartiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/parties")
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PartiesController {
    private final PartiesService partiesService;
    
    @GetMapping(value = "/getAll")
    public ApiResponse getAllParties() {
        List<Parties> all = partiesService.getAllParties();
        if (all.isEmpty()) {
            return new ApiResponse(404, "No Parties Found", null);
        }
        all.removeIf(parties -> !parties.isActive());
        return new ApiResponse(200, "Success", all);
    }

    @GetMapping("/get/{id}")
    public ApiResponse getPartiesById(@PathVariable Integer id) {
        Parties parties = partiesService.getPartyById(id);
        if (parties == null) {
            return new ApiResponse(404, "Parties Not Found", null);
        }
        return new ApiResponse(200, "Success", parties);
    }

    @PostMapping(value = "/save")
    public ApiResponse saveParties(@RequestBody PartiesDto partiesDto) {
        String response = partiesService.saveParty(partiesDto);
        System.out.println(partiesDto.getSymbol());
        return new ApiResponse(201, response, null);
    }

    @PutMapping(value = "/update")
    public ApiResponse updateParties(@RequestBody PartiesDto partiesDto) {
        String response = partiesService.updateParty(partiesDto);
        return new ApiResponse(200, response, null);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse deleteParties(@PathVariable Integer id) {
        String response = partiesService.deleteParty(id);
        return new ApiResponse(200, response, null);
    }
}
