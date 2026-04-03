package com.example.chesstournament.repository;

import com.example.chesstournament.dto.TorneoRicercaDTO;
import com.example.chesstournament.model.Torneo;

import java.util.List;

public interface CustomTorneoRepository {

    List<Torneo> findByExample(TorneoRicercaDTO example);
}
