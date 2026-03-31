package com.example.chesstournament.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "utente")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "cognome", nullable = false)
    private String cognome;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "dataregistrazione")
    private LocalDate dataRegistrazione;

    @Min(value = 0, message = "L'ELO rating non può essere negativo")
    @Column(name = "elorating", columnDefinition = "INTEGER CHECK (elorating >= 0)")
    private Integer eloRating;

    @Min(value = 0, message = "Il montepremi non può scendere sotto lo zero")
    @Column(name = "montepremi", columnDefinition = "DOUBLE CHECK (montepremi >= 0)")
    private Double montePremi;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato")
    private StatoUtente stato;

    @ManyToMany
    @JoinTable(name = "utente_ruolo", joinColumns = @JoinColumn(name = "utente_id", referencedColumnName = "ID"), inverseJoinColumns = @JoinColumn(name = "ruolo_id", referencedColumnName = "ID"))
    private Set<Ruolo> ruoli = new HashSet<>(0);

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id")
    private Torneo torneo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "utenteCreazione")
    Set<Torneo> torneiCreati = new HashSet<>();

    public Utente() {
    }

    public Utente(Long id, String nome, String cognome, String username, String password, LocalDate dataRegistrazione, Integer eloRating, Double montePremi, StatoUtente stato, Torneo torneo) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.dataRegistrazione = dataRegistrazione;
        this.eloRating = eloRating;
        this.montePremi = montePremi;
        this.stato = stato;
        this.torneo = torneo;
    }

    public Utente(Long id, String nome, String cognome, String username, String password, LocalDate dataRegistrazione, Integer eloRating, Double montePremi, StatoUtente stato, Set<Ruolo> ruoli) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.dataRegistrazione = dataRegistrazione;
        this.eloRating = eloRating;
        this.montePremi = montePremi;
        this.stato = stato;
        this.ruoli = ruoli;
    }

    public Utente(String nome, String cognome, String username, String password, LocalDate dataRegistrazione, Integer eloRating, Double montePremi, StatoUtente stato) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.password = password;
        this.dataRegistrazione = dataRegistrazione;
        this.eloRating = eloRating;
        this.montePremi = montePremi;
        this.stato = stato;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getDataRegistrazione() {
        return dataRegistrazione;
    }

    public void setDataRegistrazione(LocalDate dataRegistrazione) {
        this.dataRegistrazione = dataRegistrazione;
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

    public StatoUtente getStato() {
        return stato;
    }

    public void setStato(StatoUtente stato) {
        this.stato = stato;
    }

    public Set<Ruolo> getRuoli() {
        return ruoli;
    }

    public void setRuoli(Set<Ruolo> ruoli) {
        this.ruoli = ruoli;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public Set<Torneo> getTorneiCreati() {
        return torneiCreati;
    }

    public void setTorneiCreati(Set<Torneo> torneiCreati) {
        this.torneiCreati = torneiCreati;
    }
}
