package com.example.chesstournament.repository;

import com.example.chesstournament.model.StatoUtente;
import com.example.chesstournament.model.Utente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UtenteRepository extends JpaRepository<Utente, Long> {

    @EntityGraph(attributePaths = { "ruoli" })
    public Optional<Utente> findByUsername(String username);

    @Query("from Utente u left join fetch u.ruoli where u.id = ?1")
    public Optional<Utente> findByIdConRuoli(Long id);

    @EntityGraph(attributePaths = { "ruoli" })
    Utente findByUsernameAndPasswordAndStato(String username,String password, StatoUtente stato);

    @Query("from Utente u  left join fetch u.torneiCreati where u.id = ?1")
    Optional<Utente> findUtenteConTornei(Long id);
}
