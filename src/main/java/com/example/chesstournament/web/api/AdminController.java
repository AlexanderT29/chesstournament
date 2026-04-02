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
import com.example.chesstournament.service.UtenteService;
import com.example.chesstournament.web.api.exception.Forbidden403Exception;
import com.example.chesstournament.web.api.exception.NotFound404Exception;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private UtenteService service;

    private RuoloService ruoloService;

    public AdminController(UtenteService service, RuoloService ruoloService){

        this.service = service;
        this.ruoloService = ruoloService;
    }

    @GetMapping("/utenti")
    public ResponseEntity<ResponseBusta<List<UtenteInfoJWTResponseDTO>>> listaUtenti(){

        List<Utente> utenti = service.listAllUtenti();

        List<UtenteInfoJWTResponseDTO> utentiResponse = utenti.stream()
                .map(utente -> {
                    System.out.println(utente.getId());
                    List<String> roles = utente.getRuoli().stream()
                            .map(Ruolo::getCodice)
                            .toList();
                    TorneoDTO torneoDTO = null;
                    if (utente.getTorneo() != null) {
                        torneoDTO = TorneoDTO.buildTorneoDTOFromModel(utente.getTorneo(), false);
                    }
                    return new UtenteInfoJWTResponseDTO(
                            utente.getId(),
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

    @GetMapping("/utenti/{id}")
    public ResponseEntity<ResponseBusta<Object>> dettaglioSingoloUtente(@PathVariable("id") Long id){

        Utente utente = service.cercaPerId(id).orElseThrow(() -> new NotFound404Exception("Utente non trovato"));

        List<String> roles = utente.getRuoli().stream()
                .map(Ruolo::getCodice)
                .toList();

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


        Utente nuovoUtente = new Utente();
        nuovoUtente.setNome(body.getNome());
        nuovoUtente.setCognome(body.getCognome());
        nuovoUtente.setUsername(body.getUsername());


        nuovoUtente.setPassword(body.getPassword());


        nuovoUtente.setDataRegistrazione(LocalDate.now());
        nuovoUtente.setStato(StatoUtente.ATTIVO);
        nuovoUtente.setEloRating(0);
        nuovoUtente.setMontePremi(0.0);

        List<Long> listaRuoliIds = Arrays.asList(body.getRuoliIds());
        List<Ruolo> ruoliTrovati = ruoloService.cercaTuttiPerId(listaRuoliIds);

        nuovoUtente.getRuoli().addAll(ruoliTrovati);


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

        utenteEsistente.setNome(body.getNome());
        utenteEsistente.setCognome(body.getCognome());
        utenteEsistente.setUsername(body.getUsername());
        utenteEsistente.setDataRegistrazione(body.getDataRegistrazione());
        utenteEsistente.setStato(body.getStato());
        utenteEsistente.setEloRating(body.getEloRating());
        utenteEsistente.setMontePremi(body.getMontePremi());

        if (body.getRoles() != null) {
            List<String> listaRuoli = body.getRoles();
            List<Ruolo> ruoli = new ArrayList<>();
            for(String codice: listaRuoli){
                ruoli.add(ruoloService.cercaPerCodice(codice).orElse(null));
            }

            utenteEsistente.getRuoli().clear();
            utenteEsistente.getRuoli().addAll(ruoli);
        }

        if (body.getTorneiCreati() != null){
            Set<Torneo> torneiCreati = body.getTorneiCreati().stream()
                    .map(torneoDTO -> torneoDTO.buildTorneoModel(false))
                    .collect(Collectors.toSet());
            utenteEsistente.setTorneiCreati(torneiCreati);
        }
        service.aggiorna(utenteEsistente);

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

}
