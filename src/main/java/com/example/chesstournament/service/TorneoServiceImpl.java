package com.example.chesstournament.service;

import com.example.chesstournament.dto.TorneoDTO;
import com.example.chesstournament.dto.TorneoRicercaDTO;
import com.example.chesstournament.model.Torneo;
import com.example.chesstournament.model.Utente;
import com.example.chesstournament.repository.TorneoRepository;
import com.example.chesstournament.repository.UtenteRepository;
import com.example.chesstournament.web.api.exception.NotFound404Exception;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TorneoServiceImpl implements TorneoService{

    @Autowired
    private TorneoRepository repository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Override
    public List<Torneo> listAllTorneiDiOrganizzatore(Long id) {
        return repository.findByUtenteCreazione_Id(id);
    }

    @Override
    public Optional<Torneo> cercaPerDenominazione(String denominazione) {
        return repository.findByDenominazione(denominazione);
    }

    @Override
    @Transactional
    public Torneo inserisciNuovo(Torneo torneoInstance) {
        return repository.save(torneoInstance);
    }

    @Override
    public Optional<Torneo> cercaTorneoPerId(Long id) {
        return repository.findById(id);
    }

    @Override
    @Transactional
    public Torneo aggiorna(Torneo torneo) {
        return repository.save(torneo);
    }

    @Override
    @Transactional
    public void cancellaTorneo(Long id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Torneo aggiornaTorneo(TorneoDTO torneoDTO, Long id) {
        Torneo torneoCercato = repository.findById(id).orElseThrow(() -> new NotFound404Exception("Torneo non trovato nel database!"));
        torneoCercato.setDenominazione(torneoDTO.getDenominazione());
        torneoCercato.setStato(torneoDTO.getStato());
        torneoCercato.setQuotaIscrizione(torneoDTO.getQuotaIscrizione());
        torneoCercato.setMaxGiocatori(torneoDTO.getMaxGiocatori());
        torneoCercato.setEloMinimo(torneoDTO.getEloMinimo());
        torneoCercato.setUtenteCreazione(utenteRepository.findById(torneoDTO.getUtenteCreazioneId())
                .orElseThrow(() -> new NotFound404Exception("Utente non trovato!")));

        return repository.save(torneoCercato);
    }

    @Override
    public List<Torneo> ricercaTorneo(TorneoRicercaDTO torneo) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Utente utenteLoggato = utenteRepository.findByUsername(username).orElseThrow(() -> new NotFound404Exception("Utente non trovato."));

        torneo.setEloMinimo(utenteLoggato.getEloRating());

        return repository.findByExample(torneo);
    }
}
