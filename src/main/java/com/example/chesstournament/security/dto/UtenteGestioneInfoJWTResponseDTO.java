package com.example.chesstournament.security.dto;

import com.example.chesstournament.dto.TorneoDTO;
import com.example.chesstournament.model.StatoUtente;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtenteGestioneInfoJWTResponseDTO {
    private String nome;
    private String cognome;
    private String username;
    private LocalDate dataRegistrazione;
    private StatoUtente stato;
    private List<String> roles;
    private TorneoDTO torneo;
    private Integer eloRating;
    private Double montePremi;
    private Set<TorneoDTO> torneiCreati;

    public UtenteGestioneInfoJWTResponseDTO() {
    }

    public UtenteGestioneInfoJWTResponseDTO(String nome, String cognome, String username, LocalDate dataRegistrazione, List<String> roles, TorneoDTO torneo, Integer eloRating, Double montePremi, Set<TorneoDTO> torneiCreati) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.dataRegistrazione = dataRegistrazione;
        this.roles = roles;
        this.torneo = torneo;
        this.eloRating = eloRating;
        this.montePremi = montePremi;
        this.torneiCreati = torneiCreati;
    }

    public UtenteGestioneInfoJWTResponseDTO(String nome, String cognome, String username, LocalDate dataRegistrazione, StatoUtente stato, List<String> roles, TorneoDTO torneo, Integer eloRating, Double montePremi, Set<TorneoDTO> torneiCreati) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.dataRegistrazione = dataRegistrazione;
        this.stato = stato;
        this.roles = roles;
        this.torneo = torneo;
        this.eloRating = eloRating;
        this.montePremi = montePremi;
        this.torneiCreati = torneiCreati;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDate getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(LocalDate dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public TorneoDTO getTorneo() {
        return torneo;
    }

    public void setTorneo(TorneoDTO torneo) {
        this.torneo = torneo;
    }

    public Integer getEloRating() {
        return eloRating;
    }

    public void setEloRating(Integer eloRating) {
        this.eloRating = eloRating;
    }

    public Double getMontePremi() {
        return montePremi;
    }

    public void setMontePremi(Double montePremi) {
        this.montePremi = montePremi;
    }

    public Set<TorneoDTO> getTorneiCreati() {
        return torneiCreati;
    }

    public void setTorneiCreati(Set<TorneoDTO> torneiCreati) {
        this.torneiCreati = torneiCreati;
    }

    public StatoUtente getStato() {
        return stato;
    }

    public void setStato(StatoUtente stato) {
        this.stato = stato;
    }
}
