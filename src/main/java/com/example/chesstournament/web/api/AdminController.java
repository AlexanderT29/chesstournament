package com.example.chesstournament.web.api;

import com.example.chesstournament.dto.TorneoDTO;
import com.example.chesstournament.model.Ruolo;
import com.example.chesstournament.model.Utente;
import com.example.chesstournament.security.dto.ResponseBusta;
import com.example.chesstournament.security.dto.UtenteInfoJWTResponseDTO;
import com.example.chesstournament.service.UtenteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private UtenteService service;

    public AdminController(UtenteService service){
        this.service = service;
    }

    @GetMapping("/utenti")
    public ResponseEntity<ResponseBusta<List<UtenteInfoJWTResponseDTO>>> listaUtenti(){

        List<Utente> utenti = service.listAllUtenti();

        List<UtenteInfoJWTResponseDTO> utentiResponse = utenti.stream()
                .map(utente -> {
                    List<String> roles = utente.getRuoli().stream()
                            .map(Ruolo::getCodice)
                            .toList();
                    TorneoDTO torneoDTO = null;
                    if (utente.getTorneo() != null) {
                        torneoDTO = TorneoDTO.buildTorneoDTOFromModel(utente.getTorneo(), false);
                    }
                    return new UtenteInfoJWTResponseDTO(
                            utente.getNome(),
                            utente.getCognome(),
                            utente.getUsername(),
                            utente.getDataRegistrazione(),
                            roles,
                            torneoDTO,
                            utente.getEloRating(),
                            utente.getMontePremi()
                    );
                })
                .toList();

        return ResponseEntity.ok(ResponseBusta.success(200, utentiResponse, "Lista utenti ottenuta con successo"));
    }


}
