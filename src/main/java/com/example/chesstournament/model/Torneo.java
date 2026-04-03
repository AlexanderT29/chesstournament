package com.example.chesstournament.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "torneo")
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "denominazione", unique = true, nullable = false)
    private String denominazione;
    @Column(name = "datacreazione")
    private LocalDate dataCreazione;
    @Enumerated(EnumType.STRING)
    @Column(name = "stato")
    private StatoTorneo stato;
    @Min(value = 0, message = "L'ELO minimo deve essere almeno 0")
    @Column(name = "elominimo", columnDefinition = "INTEGER CHECK (elominimo >= 0)")
    private Integer eloMinimo;
    @Min(value = 0, message = "La quota di iscrizione non può essere negativa")
    @Column(name = "quotaiscrizione", columnDefinition = "DOUBLE CHECK (quotaiscrizione >= 0)")
    private Double quotaIscrizione;
    @Min(value = 2, message = "Il torneo deve prevedere almeno 2 giocatori massimi")
    @Column(name = "maxgiocatori", columnDefinition = "INTEGER CHECK (maxgiocatori >= 2)")
    private Integer maxGiocatori;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utentecreazione_id")
    private Utente utenteCreazione;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "torneo")
    private Set<Utente> partecipanti = new HashSet<>(0);

    public Torneo() {
    }

    public Torneo(Long id, String denominazione, LocalDate dataCreazione, StatoTorneo stato, Integer eloMinimo, Double quotaIscrizione, Integer maxGiocatori, Utente utenteCreazione) {
        this.id = id;
        this.denominazione = denominazione;
        this.dataCreazione = dataCreazione;
        this.stato = stato;
        this.eloMinimo = eloMinimo;
        this.quotaIscrizione = quotaIscrizione;
        this.maxGiocatori = maxGiocatori;
        this.utenteCreazione = utenteCreazione;
    }

    public Torneo(String denominazione, LocalDate dataCreazione, StatoTorneo stato, Integer eloMinimo, Double quotaIscrizione, Integer maxGiocatori, Utente utenteCreazione) {
        this.denominazione = denominazione;
        this.dataCreazione = dataCreazione;
        this.stato = stato;
        this.eloMinimo = eloMinimo;
        this.quotaIscrizione = quotaIscrizione;
        this.maxGiocatori = maxGiocatori;
        this.utenteCreazione = utenteCreazione;
    }

    public Torneo(String denominazione, Integer eloMinimo, Double quotaIscrizione, Integer maxGiocatori) {
        this.denominazione = denominazione;
        this.eloMinimo = eloMinimo;
        this.quotaIscrizione = quotaIscrizione;
        this.maxGiocatori = maxGiocatori;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
    }

    public LocalDate getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDate dataCreazione) {
        this.dataCreazione = dataCreazione;
    }

    public StatoTorneo getStato() {
        return stato;
    }

    public void setStato(StatoTorneo stato) {
        this.stato = stato;
    }

    public Integer getEloMinimo() {
        return eloMinimo;
    }

    public void setEloMinimo(Integer eloMinimo) {
        this.eloMinimo = eloMinimo;
    }

    public Double getQuotaIscrizione() {
        return quotaIscrizione;
    }

    public void setQuotaIscrizione(Double quotaIscrizione) {
        this.quotaIscrizione = quotaIscrizione;
    }

    public Integer getMaxGiocatori() {
        return maxGiocatori;
    }

    public void setMaxGiocatori(Integer maxGiocatori) {
        this.maxGiocatori = maxGiocatori;
    }

    public Utente getUtenteCreazione() {
        return utenteCreazione;
    }

    public void setUtenteCreazione(Utente utenteCreazione) {
        this.utenteCreazione = utenteCreazione;
    }

    public Set<Utente> getPartecipanti() {
        return partecipanti;
    }

    public void setPartecipanti(Set<Utente> partecipanti) {
        this.partecipanti = partecipanti;
    }
}
