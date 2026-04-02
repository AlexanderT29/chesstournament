package com.example.chesstournament.web.api;

import com.example.chesstournament.dto.TorneoDTO;
import com.example.chesstournament.model.Ruolo;
import com.example.chesstournament.model.StatoTorneo;
import com.example.chesstournament.model.Torneo;
import com.example.chesstournament.model.Utente;
import com.example.chesstournament.security.dto.ResponseBusta;
import com.example.chesstournament.service.RuoloService;
import com.example.chesstournament.service.TorneoService;
import com.example.chesstournament.service.UtenteService;
import com.example.chesstournament.web.api.exception.BadRequest400Exception;
import com.example.chesstournament.web.api.exception.Forbidden403Exception;
import com.example.chesstournament.web.api.exception.NotFound404Exception;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tornei")
public class TorneoController {

    private UtenteService utenteService;
    private RuoloService ruoloService;
    private TorneoService torneoService;

    public TorneoController(UtenteService utenteService, RuoloService ruoloService, TorneoService torneoService){
        this.utenteService = utenteService;
        this.ruoloService = ruoloService;
        this.torneoService = torneoService;
    }

    @PostMapping
    public ResponseEntity<ResponseBusta<String>> creaNuovoTorneo(@Valid @RequestBody TorneoDTO body){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Utente utenteLoggato = utenteService.cercaPerUsername(username)
                .orElseThrow(() -> new NotFound404Exception("Utente non trovato!"));

        List<String> codiciRuoli = utenteLoggato.getRuoli().stream()
                .map(Ruolo::getCodice)
                .toList();

        if(!codiciRuoli.contains(Ruolo.ROLE_ORGANIZER) &&
                !codiciRuoli.contains(Ruolo.ROLE_ADMIN)){
            throw new Forbidden403Exception("Non hai i permessi per fare questa operazione!");
        }

        Torneo nuovoTorneo = new Torneo();
        Torneo torneoDaCreare = torneoService.cercaPerDenominazione(body.getDenominazione()).orElse(null);
        if( torneoDaCreare != null){
            throw new BadRequest400Exception("Non puoi creare un Torneo con la stessa denominazione di un Torneo esistente!");
        }
        nuovoTorneo.setDenominazione(body.getDenominazione());
        nuovoTorneo.setDataCreazione(LocalDate.now());
        nuovoTorneo.setStato(StatoTorneo.APERTURA);
        nuovoTorneo.setEloMinimo(body.getEloMinimo());
        nuovoTorneo.setQuotaIscrizione(body.getQuotaIscrizione());
        nuovoTorneo.setMaxGiocatori(body.getMaxGiocatori());

        Set<Utente> partecipanti = new HashSet<>(0);

        nuovoTorneo.setPartecipanti(partecipanti);
        nuovoTorneo.setUtenteCreazione(utenteLoggato);

        torneoService.inserisciNuovo(nuovoTorneo);

        ResponseBusta<String> bustaSuccesso = ResponseBusta.success(200, null, "Torneo creato con successo!");
        return ResponseEntity.ok(bustaSuccesso);
    }

    @GetMapping
    public ResponseEntity<ResponseBusta<List<TorneoDTO>>> listaTorneiOrganizzatore(){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Utente utenteLoggato = utenteService.cercaPerUsername(username)
            .orElseThrow(() -> new NotFound404Exception("Utente non trovato!"));

        List<String> codiciRuoli = utenteLoggato.getRuoli().stream()
                .map(Ruolo::getCodice)
                .toList();

        if(!codiciRuoli.contains(Ruolo.ROLE_ORGANIZER) &&
                !codiciRuoli.contains(Ruolo.ROLE_ADMIN)){
            throw new Forbidden403Exception("Non hai i permessi per fare questa operazione!");
        }

        List<TorneoDTO> tornei = torneoService.listAllTorneiDiOrganizzatore(utenteLoggato.getId()).stream()
                .map(torneo -> TorneoDTO.buildTorneoDTOFromModel(torneo, false))
                .collect(Collectors.toList());

        ResponseBusta<List<TorneoDTO>> bustaSuccesso = ResponseBusta.success(200, tornei, "Tornei trovati: " + tornei.size());

        return ResponseEntity.ok(bustaSuccesso);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseBusta<TorneoDTO>> dettaglioTorneo(@PathVariable("id") Long id){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Utente utenteLoggato = utenteService.cercaPerUsername(username)
                .orElseThrow(() -> new NotFound404Exception("Utente non trovato!"));

        List<String> codiciRuoli = utenteLoggato.getRuoli().stream()
                .map(Ruolo::getCodice)
                .toList();

        if(!codiciRuoli.contains(Ruolo.ROLE_ORGANIZER) &&
                !codiciRuoli.contains(Ruolo.ROLE_ADMIN)){
            throw new Forbidden403Exception("Non hai i permessi per fare questa operazione!");
        }

        Torneo torneoCercato = torneoService.cercaTorneoPerId(id)
                .orElseThrow(() -> new NotFound404Exception("Torneo non trovato!"));

        TorneoDTO payload = TorneoDTO.buildTorneoDTOFromModel(torneoCercato, true);

        ResponseBusta<TorneoDTO> bustaSuccesso = ResponseBusta.success(200, payload, "Torneo trovato con successo!");
        return ResponseEntity.ok(bustaSuccesso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseBusta<TorneoDTO>> modificaTorneo(@PathVariable("id") Long id, @RequestBody TorneoDTO body){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Utente utenteLoggato = utenteService.cercaPerUsername(username)
                .orElseThrow(() -> new NotFound404Exception("Utente non trovato!"));

        List<String> codiciRuoli = utenteLoggato.getRuoli().stream()
                .map(Ruolo::getCodice)
                .toList();

        if(!codiciRuoli.contains(Ruolo.ROLE_ORGANIZER) &&
                !codiciRuoli.contains(Ruolo.ROLE_ADMIN)){
            throw new Forbidden403Exception("Non hai i permessi per fare questa operazione!");
        }

        Torneo torneoCercato = torneoService.cercaTorneoPerId(id)
                .orElseThrow(() -> new NotFound404Exception("Torneo non trovato!"));

        if(torneoCercato.getUtenteCreazione().getId() != utenteLoggato.getId()){
            throw new Forbidden403Exception("Non puoi modificare un torneo non tuo!");
        }

        if(!torneoCercato.getPartecipanti().isEmpty()){
            throw new Forbidden403Exception("Non puoi modificare un torneo se ha dei partecipanti");
        }

        torneoCercato.setDenominazione(body.getDenominazione());
        torneoCercato.setStato(body.getStato());
        torneoCercato.setQuotaIscrizione(body.getQuotaIscrizione());
        torneoCercato.setMaxGiocatori(body.getMaxGiocatori());
        torneoCercato.setEloMinimo(body.getEloMinimo());
        torneoCercato.setUtenteCreazione(utenteService.cercaPerId(body.getUtenteCreazioneId())
                .orElseThrow(() -> new NotFound404Exception("Utente non trovato!")));

        torneoService.aggiorna(torneoCercato);

        TorneoDTO payload = TorneoDTO.buildTorneoDTOFromModel(torneoCercato, true);

        ResponseBusta<TorneoDTO> bustaSuccesso = ResponseBusta.success(200, payload , "Torneo Modificato con successo");

        return ResponseEntity.ok(bustaSuccesso);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseBusta<String>> cancellaTorneo(@PathVariable("id") Long id){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        Utente utenteLoggato = utenteService.cercaPerUsername(username)
                .orElseThrow(() -> new NotFound404Exception("Utente non trovato!"));

        List<String> codiciRuoli = utenteLoggato.getRuoli().stream()
                .map(Ruolo::getCodice)
                .toList();

        if(!codiciRuoli.contains(Ruolo.ROLE_ORGANIZER) &&
                !codiciRuoli.contains(Ruolo.ROLE_ADMIN)){
            throw new Forbidden403Exception("Non hai i permessi per fare questa operazione!");
        }

        Torneo torneoCercato = torneoService.cercaTorneoPerId(id)
                .orElseThrow(() -> new NotFound404Exception("Torneo non trovato!"));

        if(torneoCercato.getUtenteCreazione().getId() != utenteLoggato.getId()){
            throw new Forbidden403Exception("Non puoi modificare un torneo non tuo!");
        }

        if(!torneoCercato.getPartecipanti().isEmpty()){
            throw new Forbidden403Exception("Non puoi modificare un torneo se ha dei partecipanti");
        }

        torneoService.cancellaTorneo(torneoCercato.getId());

        ResponseBusta<String> bustaSuccess = ResponseBusta.success(204, null, "Torneo cancellato con successo");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(bustaSuccess);

    }



}
