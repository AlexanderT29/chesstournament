package com.example.chesstournament.service;

import com.example.chesstournament.model.Ruolo;
import com.example.chesstournament.repository.RuoloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RuoloServiceImpl implements RuoloService{

    @Autowired
    private RuoloRepository ruoloRepository;

    @Override
    public Optional<Ruolo> cercaPerCodice(String codice) {
        return ruoloRepository.findByCodice(codice);
    }
}
