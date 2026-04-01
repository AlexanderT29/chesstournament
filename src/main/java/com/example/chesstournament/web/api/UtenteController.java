package com.example.chesstournament.web.api;

import com.example.chesstournament.dto.RicaricaDTO;
import com.example.chesstournament.dto.TorneoDTO;
import com.example.chesstournament.model.Ruolo;
import com.example.chesstournament.model.Utente;
import com.example.chesstournament.security.dto.ResponseBusta;
import com.example.chesstournament.security.dto.UtenteInfoJWTResponseDTO;
import com.example.chesstournament.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/play")
public class UtenteController {

    private UtenteService service;

    public UtenteController(UtenteService service){
        this.service = service;
    }

    @GetMapping("/info")
    public ResponseEntity<ResponseBusta<UtenteInfoJWTResponseDTO>> infoPlayer(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Utente utenteLoggato = service.cercaPerUsername(username).orElse(null);

        if (utenteLoggato == null) {
            return ResponseEntity.status(404)
                    .body(ResponseBusta.error(404, "Utente non trovato nel database"));
        }

        List<String> roles = utenteLoggato.getRuoli().stream()
                .map(Ruolo::getCodice)
                .toList();

        TorneoDTO torneoDTO = null;
        if (utenteLoggato.getTorneo() != null) {
            torneoDTO = TorneoDTO.buildTorneoDTOFromModel(utenteLoggato.getTorneo(), false);
        }

        UtenteInfoJWTResponseDTO payload = new UtenteInfoJWTResponseDTO(
                utenteLoggato.getNome(),
                utenteLoggato.getCognome(),
                utenteLoggato.getUsername(),
                utenteLoggato.getDataRegistrazione(),
                roles,
                torneoDTO,
                utenteLoggato.getEloRating(),
                utenteLoggato.getMontePremi()
        );

        return ResponseEntity.ok(ResponseBusta.success(200, payload, "Info giocatore caricate correttamente"));

    }

    @PutMapping("/ricarica")
    public ResponseEntity<ResponseBusta<UtenteInfoJWTResponseDTO>> ricaricaMontePremi(@Valid @RequestBody RicaricaDTO ricarica){

        if(ricarica == null){
            return ResponseEntity.status(404)
                    .body(ResponseBusta.error(404, "La ricarica è vuota!"));
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utente utenteLoggato = service.cercaPerUsername(username).orElse(null);

        if (utenteLoggato == null) {
            return ResponseEntity.status(404)
                    .body(ResponseBusta.error(404, "Utente non trovato"));
        }

        utenteLoggato.setMontePremi(utenteLoggato.getMontePremi() + ricarica.getImporto());

        service.aggiorna(utenteLoggato);

        UtenteInfoJWTResponseDTO responseDTO = new UtenteInfoJWTResponseDTO(
                utenteLoggato.getNome(),
                utenteLoggato.getCognome(),
                utenteLoggato.getUsername(),
                utenteLoggato.getMontePremi()
        );

        return ResponseEntity.ok(ResponseBusta.success(200, responseDTO, "Ricarica effettuata con successo!"));
    }

}
