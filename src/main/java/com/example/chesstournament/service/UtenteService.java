package com.example.chesstournament.service;


import com.example.chesstournament.dto.RisultatoPartitaDTO;
import com.example.chesstournament.dto.UtenteDTO;
import com.example.chesstournament.model.Ruolo;
import com.example.chesstournament.model.Torneo;
import com.example.chesstournament.model.Utente;
import com.example.chesstournament.security.dto.UtenteGestioneInfoJWTResponseDTO;
import com.example.chesstournament.security.dto.UtenteInfoJWTResponseDTO;

import java.util.List;
import java.util.Optional;

public interface UtenteService {

    public Utente inserisciNuovo(Utente utenteInstance);

    public List<Utente> listAllUtenti();

    public Optional<Utente> cercaPerUsername(String username);

    public Utente aggiorna(Utente utenteAggiornato);

    public Optional<Utente> cercaPerId(Long id);

    public List<UtenteInfoJWTResponseDTO> convertiInUtenteInfoJWT(List<Utente> utenti);

    public Utente creaUtenteDaUtenteDTO(UtenteDTO utenteDTO);

    public Utente salvaUtenteDaDTO(UtenteGestioneInfoJWTResponseDTO utente, Long id);

    public Utente iscriviAlTorneo(Long idTorneo);

    public void abbandonaTorneo();

    public RisultatoPartitaDTO giocaPartita(Long idTorneo);



}
