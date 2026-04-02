package com.example.chesstournament.service;

import com.example.chesstournament.model.Torneo;
import com.example.chesstournament.repository.TorneoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TorneoServiceImpl implements TorneoService{

    @Autowired
    private TorneoRepository repository;

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
}
