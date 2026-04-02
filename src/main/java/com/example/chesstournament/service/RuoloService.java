package com.example.chesstournament.service;

import com.example.chesstournament.model.Ruolo;

import java.util.List;
import java.util.Optional;

public interface RuoloService {

    public Optional<Ruolo> cercaPerCodice(String codice);

    public List<Ruolo> cercaTuttiPerId(Iterable<Long> ids);
}
