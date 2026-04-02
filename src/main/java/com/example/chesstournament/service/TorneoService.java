package com.example.chesstournament.service;

import com.example.chesstournament.model.Torneo;
import com.example.chesstournament.model.Utente;

import java.util.List;
import java.util.Optional;

public interface TorneoService {

    public Optional<Torneo> cercaPerDenominazione(String denominazione);

    public Torneo inserisciNuovo(Torneo torneoInstance);

    public List<Torneo> listAllTorneiDiOrganizzatore(Long id);

    public Optional<Torneo> cercaTorneoPerId(Long id);

    public Torneo aggiorna(Torneo torneo);

    public void cancellaTorneo(Long id);
}
