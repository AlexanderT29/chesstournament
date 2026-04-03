package com.example.chesstournament.service;

import com.example.chesstournament.dto.RisultatoPartitaDTO;
import com.example.chesstournament.dto.TorneoDTO;
import com.example.chesstournament.dto.UtenteDTO;
import com.example.chesstournament.model.*;
import com.example.chesstournament.repository.RuoloRepository;
import com.example.chesstournament.repository.TorneoRepository;
import com.example.chesstournament.repository.UtenteRepository;
import com.example.chesstournament.security.dto.UtenteGestioneInfoJWTResponseDTO;
import com.example.chesstournament.security.dto.UtenteInfoJWTResponseDTO;
import com.example.chesstournament.web.api.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UtenteServiceImpl implements UtenteService{

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private RuoloRepository ruoloRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    @Override
    public List<Utente> listAllUtenti() {
        return utenteRepository.findAll();
    }

    @Override
    @Transactional
    public Utente inserisciNuovo(Utente utenteInstance) {

        utenteInstance.setPassword(passwordEncoder.encode(utenteInstance.getPassword()));


        return utenteRepository.save(utenteInstance);
    }

    @Override
    public Optional<Utente> cercaPerUsername(String username) {
        return utenteRepository.findByUsername(username);
    }

    @Override
    @Transactional
    public Utente aggiorna(Utente utenteAggiornato) {
        return utenteRepository.save(utenteAggiornato);
    }

    @Override
    public Optional<Utente> cercaPerId(Long id) {
        return utenteRepository.findById(id);
    }

    public List<UtenteInfoJWTResponseDTO> convertiInUtenteInfoJWT(List<Utente> utenti) {

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
        return utentiResponse;
    }

    @Override
    public Utente creaUtenteDaUtenteDTO(UtenteDTO utenteDTO) {
        Utente nuovoUtente = new Utente();
        nuovoUtente.setNome(utenteDTO.getNome());
        nuovoUtente.setCognome(utenteDTO.getCognome());
        nuovoUtente.setUsername(utenteDTO.getUsername());


        nuovoUtente.setPassword(utenteDTO.getPassword());


        nuovoUtente.setDataRegistrazione(LocalDate.now());
        nuovoUtente.setStato(StatoUtente.ATTIVO);
        nuovoUtente.setEloRating(0);
        nuovoUtente.setMontePremi(0.0);


        return nuovoUtente;
    }

    @Override
    @Transactional
    public Utente salvaUtenteDaDTO(UtenteGestioneInfoJWTResponseDTO utente, Long id) {
        Utente utenteEsistente = utenteRepository.findById(id).orElseThrow(() -> new NotFound404Exception("Utente non trovato!"));

        utenteEsistente.setNome(utente.getNome());
        utenteEsistente.setCognome(utente.getCognome());
        utenteEsistente.setUsername(utente.getUsername());
        utenteEsistente.setDataRegistrazione(utente.getDataRegistrazione());
        utenteEsistente.setStato(utente.getStato());
        utenteEsistente.setEloRating(utente.getEloRating());
        utenteEsistente.setMontePremi(utente.getMontePremi());

        if (utente.getRoles() != null) {
            List<String> listaRuoli = utente.getRoles();
            List<Ruolo> ruoli = new ArrayList<>();
            for(String codice: listaRuoli){
                ruoli.add(ruoloRepository.findByCodice(codice).orElse(null));
            }

            utenteEsistente.getRuoli().clear();
            utenteEsistente.getRuoli().addAll(ruoli);
        }

        if (utente.getTorneiCreati() != null){
            Set<Torneo> torneiCreati = utente.getTorneiCreati().stream()
                    .map(torneoDTO -> torneoDTO.buildTorneoModel(false))
                    .collect(Collectors.toSet());
            utenteEsistente.setTorneiCreati(torneiCreati);
        }
        return utenteRepository.save(utenteEsistente);
    }

    @Override
    @Transactional
    public Utente iscriviAlTorneo(Long idTorneo) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utente utenteLoggato = utenteRepository.findByUsername(username).orElseThrow(() -> new NotFound404Exception("Utente non trovato."));

        if(utenteLoggato.getTorneo() != null){
            throw new Forbidden403Exception("Non puoi iscriverti a un nuovo Torneo perché iscrizione presente in un altro Torneo");
        }

        List<String> codiciRuoli = utenteLoggato.getRuoli().stream()
                .map(Ruolo::getCodice)
                .toList();

        if(!codiciRuoli.contains(Ruolo.ROLE_PLAYER)){
            throw new Forbidden403Exception("Permessi negati per eseguire questa operazione.");
        }

        Torneo torneoIscrizione = torneoRepository.findById(idTorneo).orElseThrow(() -> new NotFound404Exception("Torneo non trovato!"));

        if(utenteLoggato.getMontePremi() < torneoIscrizione.getQuotaIscrizione()){
            throw new CustomPlayerException("Fondo monte premi insufficiente. Ricaricare monte premi.");
        }

        if(torneoIscrizione.getPartecipanti().size() >= torneoIscrizione.getMaxGiocatori()){
            throw new CustomPlayerException("Il Torneo è al completo!");
        }

        if(torneoIscrizione.getStato() != StatoTorneo.APERTURA){
            throw new CustomPlayerException("Le iscrizioni per questo Torneo sono concluse!");
        }

        utenteLoggato.setTorneo(torneoIscrizione);
        torneoIscrizione.getPartecipanti().add(utenteLoggato);

        return utenteRepository.save(utenteLoggato);
    }

    @Override
    @Transactional
    public void abbandonaTorneo() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utente utenteLoggato = utenteRepository.findByUsername(username).orElseThrow(() -> new NotFound404Exception("Utente non trovato."));

        if(utenteLoggato.getTorneo() == null){
            throw new NotFound404Exception("Non partecipi a nessun Torneo.");
        }

        Torneo torneoDaAbbandonare = torneoRepository.findById(utenteLoggato.getTorneo().getId()).orElse(null);

        if(torneoDaAbbandonare == null){
            throw new NotFound404Exception("Nessun Torneo trovato corrispondente!");
        }

        torneoDaAbbandonare.getPartecipanti().remove(utenteLoggato);

        utenteLoggato.setTorneo(null);

        utenteRepository.save(utenteLoggato);

    }

    @Override
    @Transactional
    public RisultatoPartitaDTO giocaPartita(Long idTorneo) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utente utenteLoggato = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new NotFound404Exception("Utente non trovato."));

        if(utenteLoggato.getTorneo() == null || !utenteLoggato.getTorneo().getId().equals(idTorneo)){
            throw new Unauthorized401Exception("L'Utente non appartiene a questo torneo!");
        }
        if(utenteLoggato.getTorneo().getStato() != StatoTorneo.IN_CORSO){
            throw new BadRequest400Exception("Il torneo non è attualmente in corso!");
        }

        double esito = Math.random();
        int somma = (int)(Math.random() * 500);
        int delta;
        String esitoTesto;


        if (esito < 0.33) {
            delta = -somma;
            esitoTesto = "Hai perso la partita! Bilancio: " + delta;
        } else if (esito < 0.66) {
            delta = 0;
            esitoTesto = "Pareggio! Bilancio invariato.";
        } else {
            delta = somma;
            esitoTesto = "Hai vinto la partita! Bilancio: +" + delta;
        }

        utenteLoggato.setMontePremi(utenteLoggato.getMontePremi() + delta);
        utenteLoggato.setEloRating(utenteLoggato.getEloRating() + 5);

        if (utenteLoggato.getMontePremi() < 0) {
            utenteLoggato.setMontePremi(0.0);
            esitoTesto += " - ATTENZIONE: Il tuo credito è esaurito!";
        }

        Utente utenteSalvato = utenteRepository.save(utenteLoggato);

        return new RisultatoPartitaDTO(utenteSalvato, esitoTesto);
    }
}
