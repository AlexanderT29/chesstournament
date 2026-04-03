package com.example.chesstournament.web.api;

import com.example.chesstournament.dto.TorneoDTO;
import com.example.chesstournament.dto.UtenteDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private UtenteService service;

    private RuoloService ruoloService;

    private TorneoService torneoService;

    public AdminController(UtenteService service, RuoloService ruoloService, TorneoService torneoService){

        this.service = service;
        this.ruoloService = ruoloService;
        this.torneoService = torneoService;
    }

    @GetMapping("/utenti")
    public ResponseEntity<ResponseBusta<List<UtenteInfoJWTResponseDTO>>> listaUtenti(){

        List<Utente> utenti = service.listAllUtenti();

        List<UtenteInfoJWTResponseDTO> utentiResponse = service.convertiInUtenteInfoJWT(utenti);

        return ResponseEntity.ok(ResponseBusta.success(200, utentiResponse, "Lista utenti ottenuta con successo"));
    }

    @GetMapping("/utenti/{id}")
    public ResponseEntity<ResponseBusta<Object>> dettaglioSingoloUtente(@PathVariable("id") Long id){

        Utente utente = service.cercaPerId(id).orElseThrow(() -> new NotFound404Exception("Utente non trovato"));

        List<String> roles = ruoloService.convertiInCodiceListaRuoli(utente);

        TorneoDTO torneoDTO = null;
        if (utente.getTorneo() != null) {
            torneoDTO = TorneoDTO.buildTorneoDTOFromModel(utente.getTorneo(), false);
        }

        Set<TorneoDTO> torneiCreatiDTO = null;
        if(utente.getTorneiCreati() != null){
            utente.getTorneiCreati().stream()
                    .map(torneo -> TorneoDTO.buildTorneoDTOFromModel(torneo, false))
                    .collect(Collectors.toSet());
        }

        UtenteInfoJWTResponseDTO payload = new UtenteInfoJWTResponseDTO(
                utente.getNome(),
                utente.getCognome(),
                utente.getUsername(),
                utente.getDataRegistrazione(),
                roles,
                torneoDTO,
                utente.getEloRating(),
                utente.getMontePremi()
        );

            return ResponseEntity.ok(ResponseBusta.success(200, payload, "Info utente caricata con successo!"));

    }

    @PostMapping("/utenti")
    public ResponseEntity<ResponseBusta<String>> creaNuovoUtente(@Valid @RequestBody UtenteDTO body){
        if (service.cercaPerUsername(body.getUsername()).orElse(null) != null) {
            ResponseBusta<String> bustaErrore = ResponseBusta.error(400, "Errore: username già in uso");
            return ResponseEntity.badRequest().body(bustaErrore);
        }

        Utente nuovoUtente = body.buildNuovoUtenteModel();

        List<Ruolo> ruoliTrovati = ruoloService.cercaTuttiPerId(Arrays.asList(body.getRuoliIds()));

        nuovoUtente.getRuoli().addAll(ruoliTrovati);

        if(nuovoUtente.getRuoli().contains(Ruolo.ROLE_ADMIN) || nuovoUtente.getRuoli().contains(Ruolo.ROLE_ORGANIZER)){
            nuovoUtente.setTorneiCreati(new HashSet<>(0));
        }


        service.inserisciNuovo(nuovoUtente);

        ResponseBusta<String> bustaSuccesso = ResponseBusta.success(200, null, "Registrazione completata con successo");

        return ResponseEntity.ok(bustaSuccesso);
    }

    @PutMapping("/utenti/{id}")
    public ResponseEntity<ResponseBusta<String>> modificaUtente(@PathVariable("id") Long id, @Valid @RequestBody UtenteGestioneInfoJWTResponseDTO body){

        Utente utenteEsistente = service.cercaPerId(id)
                .orElseThrow(() -> new NotFound404Exception("Utente con ID " + id + " non trovato"));

        if (!utenteEsistente.getUsername().equals(body.getUsername()) &&
                service.cercaPerUsername(body.getUsername()).isPresent()) {
            throw new Forbidden403Exception("Il nuovo username è già occupato");
        }

        utenteEsistente = service.salvaUtenteDaDTO(body, id);


        ResponseBusta<String> bustaSuccesso = ResponseBusta.success(200, null, "Modifica completata con successo");

        return ResponseEntity.ok(bustaSuccesso);
    }

    @DeleteMapping("/utenti/{id}")
    public ResponseEntity<ResponseBusta<String>> disattivaUtente(@PathVariable("id") Long id){
        Utente utenteEsistente = service.cercaPerId(id)
                .orElseThrow(() -> new NotFound404Exception("Utente con ID " + id + " non trovato"));

        utenteEsistente.setStato(StatoUtente.DISABILITATO);
        service.aggiorna(utenteEsistente);

        ResponseBusta<String> bustaSuccesso = ResponseBusta.success(200, null, "Utente disabilitato con successo");

        return ResponseEntity.ok(bustaSuccesso);
    }

    @PutMapping("/torneo/{id}")
    public ResponseEntity<ResponseBusta<TorneoDTO>> modificaTorneo(@PathVariable("id") Long id, @RequestBody TorneoDTO body){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Utente utenteLoggato = service.cercaPerUsername(username)
                .orElseThrow(() -> new NotFound404Exception("Utente non trovato!"));

        List<String> codiciRuoli = utenteLoggato.getRuoli().stream()
                .map(Ruolo::getCodice)
                .toList();

        if(!codiciRuoli.contains(Ruolo.ROLE_ORGANIZER) &&
                !codiciRuoli.contains(Ruolo.ROLE_ADMIN)){
            throw new Forbidden403Exception("Non hai i permessi per fare questa operazione!");
        }


        Torneo torneoCercato = torneoService.aggiornaTorneo(body, id);

        TorneoDTO payload = TorneoDTO.buildTorneoDTOFromModel(torneoCercato, true);

        ResponseBusta<TorneoDTO> bustaSuccesso = ResponseBusta.success(200, payload , "Torneo Modificato con successo");

        return ResponseEntity.ok(bustaSuccesso);
    }

}
