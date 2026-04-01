package com.example.chesstournament.security.dto;

import com.example.chesstournament.dto.TorneoDTO;
import com.example.chesstournament.model.Torneo;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtenteInfoJWTResponseDTO {
    private String nome;
    private String cognome;
    private String username;
    private LocalDate dataRegistrazione;
    private List<String> roles;
    private TorneoDTO torneo;
    private Integer eloRating;
    private Double montePremi;


    public UtenteInfoJWTResponseDTO(String nome, String cognome, String username, String email, List<String> roles) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.roles = roles;
    }


    public UtenteInfoJWTResponseDTO(String nome, String cognome, String username, LocalDate dataRegistrazione, List<String> roles, TorneoDTO torneo, Integer eloRating, Double montePremi) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.dataRegistrazione = dataRegistrazione;
        this.roles = roles;
        this.torneo = torneo;
        this.eloRating = eloRating;
        this.montePremi = montePremi;
    }

    public UtenteInfoJWTResponseDTO(String nome, String cognome, String username, Double montePremi) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.montePremi = montePremi;
    }

    public UtenteInfoJWTResponseDTO(String nome, String cognome, String username) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
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

    public LocalDate getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(LocalDate dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
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
}
