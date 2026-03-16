package lk.ijse.election_backend.service;

import lk.ijse.election_backend.dto.PartiesDto;
import lk.ijse.election_backend.entity.Parties;

import java.util.List;

public interface PartiesService {
    List<Parties> getAllParties();
    String saveParty(PartiesDto party);
    String updateParty(PartiesDto party);
    String deleteParty(Integer id);
    Parties getPartyById(Integer id);
    String deactivateParty(Integer id);
}
