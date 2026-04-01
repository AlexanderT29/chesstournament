package com.example.chesstournament.service;

import com.example.chesstournament.model.Ruolo;

import java.util.Optional;

public interface RuoloService {

    public Optional<Ruolo> cercaPerCodice(String codice);
}
