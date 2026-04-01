package com.example.chesstournament.service;

import com.example.chesstournament.model.StatoUtente;
import com.example.chesstournament.model.Utente;
import com.example.chesstournament.repository.UtenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UtenteServiceImpl implements UtenteService{

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UtenteRepository utenteRepository;

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
}
