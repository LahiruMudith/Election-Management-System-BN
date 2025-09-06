package lk.ijse.election_backend.service;

import lk.ijse.election_backend.dto.PartiesDto;
import lk.ijse.election_backend.entity.Parties;
import lk.ijse.election_backend.repository.PartiesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartiesService {
    private final PartiesRepository partiesRepository;

    public List<Parties> getAllParties() {
        return partiesRepository.findAll();
    }

    public String saveParty(PartiesDto party) {
        if (partiesRepository.findBySymbol(party.getSymbol()).isPresent()) {
            throw new RuntimeException("Party Already Registered");
        }
        partiesRepository.save(
                Parties.builder()
                        .id(party.getId())
                        .name(party.getName())
                        .symbol(party.getSymbol())
                        .color(party.getColor())
                        .isActive(party.isActive())
                        .build()
        );
        return "Party Registered Successfully";
    }

    public String updateParty(PartiesDto party) {
        Parties parties = partiesRepository.findById(party.getId())
                .orElseThrow(() -> new RuntimeException("Party Not Found"));

        parties.setName(party.getName());
        parties.setSymbol(party.getSymbol());
        parties.setColor(party.getColor());

        partiesRepository.save(parties);
        return "Party Update Successfully";
    }

    public String deleteParty(Integer id) {
        if (partiesRepository.findById(id).isEmpty()) {
            throw new RuntimeException("Party Not Found");
        }
        partiesRepository.deleteById(id);
        return "Party Deleted Successfully";
    }

    public Parties getPartyById(Integer id) {
        return partiesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Party Not Found"));
    }
}
