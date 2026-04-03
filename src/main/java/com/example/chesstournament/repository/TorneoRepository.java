package com.example.chesstournament.repository;

import com.example.chesstournament.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TorneoRepository extends JpaRepository<Torneo, Long>, CustomTorneoRepository {

    public Optional<Torneo> findByDenominazione(String denominazione);

    public List<Torneo> findByUtenteCreazione_Id(Long idOrganizzatore);
}
