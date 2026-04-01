package com.example.chesstournament.repository;

import com.example.chesstournament.model.Ruolo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RuoloRepository extends JpaRepository<Ruolo, Long> {

    public Optional<Ruolo> findByCodice(String codice);
}
