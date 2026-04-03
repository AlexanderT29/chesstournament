package com.example.chesstournament.web.api;

import com.example.chesstournament.dto.*;
import com.example.chesstournament.model.Ruolo;
import com.example.chesstournament.model.StatoUtente;
import com.example.chesstournament.model.Torneo;
import com.example.chesstournament.model.Utente;
import com.example.chesstournament.security.dto.ResponseBusta;
import com.example.chesstournament.security.dto.UtenteGestioneInfoJWTResponseDTO;
import com.example.chesstournament.security.dto.UtenteInfoJWTResponseDTO;
import com.example.chesstournament.service.RuoloService;
import com.example.chesstournament.service.TorneoService;
import com.example.chesstournament.service.UtenteService;
import com.example.chesstournament.web.api.exception.Forbidden403Exception;
import com.example.chesstournament.web.api.exception.NotFound404Exception;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/play")
public class UtenteController {

    private UtenteService service;

    private RuoloService ruoloService;

    private TorneoService torneoService;

    public UtenteController(UtenteService service, RuoloService ruoloService, TorneoService torneoService){
        this.service = service;
        this.ruoloService = ruoloService;
        this.torneoService = torneoService;
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

    @DeleteMapping("/disabilita/{id}")
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

    @PutMapping("/modfica/{id}")
    public ResponseEntity<ResponseBusta<String>> modificaUtente(@PathVariable("id") Long id,
                                                                                  @Valid @RequestBody UtenteGestioneInfoJWTResponseDTO body){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utente utenteLoggato = service.cercaPerUsername(username).orElse(null);

        if (utenteLoggato == null) {
            throw new NotFound404Exception("Utente non trovato.");
        }

        if(utenteLoggato.getId() != id && !utenteLoggato.getRuoli().contains(Ruolo.ROLE_ADMIN)){
            throw new Forbidden403Exception("Permessi negati per eseguire questa operazione.");
        }

        utenteLoggato.setNome(body.getNome());
        utenteLoggato.setCognome(body.getCognome());
        utenteLoggato.setUsername(body.getUsername());
        utenteLoggato.setDataRegistrazione(body.getDataRegistrazione());
        utenteLoggato.setStato(body.getStato());
        utenteLoggato.setEloRating(body.getEloRating());
        utenteLoggato.setMontePremi(body.getMontePremi());

        if (body.getRoles() != null) {
            List<String> listaRuoli = body.getRoles();
            List<Ruolo> ruoli = new ArrayList<>();
            for(String codice: listaRuoli){
                ruoli.add(ruoloService.cercaPerCodice(codice).orElse(null));
            }

            utenteLoggato.getRuoli().clear();
            utenteLoggato.getRuoli().addAll(ruoli);
        }


        service.aggiorna(utenteLoggato);

        ResponseBusta<String> bustaSuccesso = ResponseBusta.success(200, null, "Modifica completata con successo");

        return ResponseEntity.ok(bustaSuccesso);

    }

    @GetMapping("/ultimo-torneo")
    public ResponseEntity<ResponseBusta<TorneoDTO>> trovaUltimoTorneoPartecipato(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utente utenteLoggato = service.cercaPerUsername(username).orElseThrow(() -> new NotFound404Exception("Utente non trovato."));

        if(utenteLoggato.getTorneo() != null) {
            TorneoDTO torneo = TorneoDTO.buildTorneoDTOFromModel(utenteLoggato.getTorneo(), false);
            ResponseBusta<TorneoDTO> bustaSuccesso = ResponseBusta.success(200, torneo, "Ultimo Torneo trovato!");
            return ResponseEntity.ok(bustaSuccesso);
        } else {
            ResponseBusta<TorneoDTO> bustaVuota = ResponseBusta.success(204, null, "Ultimo Torneo non trovato");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(bustaVuota);
        }
    }

    @PostMapping("/iscriviti/{id}")
    public ResponseEntity<ResponseBusta<UtenteDTO>> iscrivitiATorneo(@PathVariable("id") Long id){

        Utente utente = service.iscriviAlTorneo(id);

        UtenteDTO utenteDTO = UtenteDTO.buildUtenteDTOFromModel(utente);

        ResponseBusta<UtenteDTO> bustaSuccesso = ResponseBusta.success(200, utenteDTO, "Iscritto al Torne " + id + " con successo!");

        return ResponseEntity.ok(bustaSuccesso);
    }

    @GetMapping("/ricerca")
    public ResponseEntity<ResponseBusta<List<TorneoDTO>>> ricercaTornei(@RequestParam String denominazione){

        TorneoRicercaDTO torneo = new TorneoRicercaDTO();
        torneo.setDenominazione(denominazione);

        List<Torneo> torneiTrovati = torneoService.ricercaTorneo(torneo);

        List<TorneoDTO> torneiResponse = torneiTrovati.stream()
                .map(torneo1 -> TorneoDTO.buildTorneoDTOFromModel(torneo1, false))
                .toList();

        ResponseBusta<List<TorneoDTO>> bustaSuccesso = ResponseBusta.success(200, torneiResponse, "Tornei trovati con successo!");
        return ResponseEntity.ok(bustaSuccesso);

    }

    @DeleteMapping("/abbandona")
    public ResponseEntity<ResponseBusta<String>> abbandonaTorneo(){

        service.abbandonaTorneo();

        ResponseBusta<String> bustaSuccesso = ResponseBusta.success(200, null, "Torneo abbandonato con successo");
        return ResponseEntity.ok(bustaSuccesso);
    }

    @PostMapping("/gioca/{idTorneo}")
    public ResponseEntity<ResponseBusta<UtenteInfoJWTResponseDTO>> giocaPartita(@PathVariable("idTorneo") Long idTorneo){

        RisultatoPartitaDTO risultato = service.giocaPartita(idTorneo);
        UtenteInfoJWTResponseDTO utenteResponse = UtenteInfoJWTResponseDTO.buildDTOFromModel(risultato.getUtenteAggiornato(), false);

        return ResponseEntity.ok(ResponseBusta.success(200, utenteResponse, risultato.getMessaggioEsito()));
    }

}
