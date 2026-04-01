package com.example.chesstournament.service;


import com.example.chesstournament.model.Utente;

import java.util.List;
import java.util.Optional;

public interface UtenteService {

    public Utente inserisciNuovo(Utente utenteInstance);

    public List<Utente> listAllUtenti();

    public Optional<Utente> cercaPerUsername(String username);


}
