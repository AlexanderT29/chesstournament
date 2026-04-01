package com.example.chesstournament.web.api;

import com.example.chesstournament.dto.RicaricaDTO;
import com.example.chesstournament.dto.TorneoDTO;
import com.example.chesstournament.model.Ruolo;
import com.example.chesstournament.model.StatoUtente;
import com.example.chesstournament.model.Utente;
import com.example.chesstournament.security.dto.ResponseBusta;
import com.example.chesstournament.security.dto.UtenteInfoJWTResponseDTO;
import com.example.chesstournament.service.UtenteService;
import com.example.chesstournament.web.api.exception.Forbidden403Exception;
import com.example.chesstournament.web.api.exception.NotFound404Exception;
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
            throw new NotFound404Exception("Utente non trovato.");
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
            throw new NotFound404Exception("Ricarica non trovata");
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utente utenteLoggato = service.cercaPerUsername(username).orElse(null);

        if (utenteLoggato == null) {
            throw new NotFound404Exception("Utente non trovato.");
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

    @PutMapping("/disabilita/{id}")
    public ResponseEntity<ResponseBusta<UtenteInfoJWTResponseDTO>> disabilitaUtente(@PathVariable("id") Long id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utente utenteLoggato = service.cercaPerUsername(username).orElse(null);

        if (utenteLoggato == null) {
            throw new NotFound404Exception("Utente non trovato.");
        }

        if(utenteLoggato.getId() != id || !utenteLoggato.getRuoli().contains(Ruolo.ROLE_ADMIN)){
            throw new Forbidden403Exception("Permessi negati per eseguire questa operazione.");
        }

        utenteLoggato.setStato(StatoUtente.DISABILITATO);

        service.aggiorna(utenteLoggato);

        return ResponseEntity.accepted().body(ResponseBusta.success(200, null, "Utente disabilitato con successo"));
    }

}
