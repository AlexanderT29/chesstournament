package com.example.chesstournament.dto;

import com.example.chesstournament.model.Ruolo;
import com.example.chesstournament.model.StatoUtente;
import com.example.chesstournament.model.Torneo;
import com.example.chesstournament.model.Utente;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtenteDTO {

    private Long id;

    @NotBlank(message = "Il nome non può essere vuoto")
    private String nome;

    @NotBlank(message = "Il cognome non può essere vuoto")
    private String cognome;

    @NotBlank(message = "L'username non può essere vuoto")
    @Size(min = 3, max = 20, message = "L'username deve essere tra {min} e {max} caratteri")
    private String username;

    @NotBlank(message = "La password non può essere vuota")
    private String password;

    private LocalDate dataRegistrazione;

    @Min(value = 0, message = "L'ELO rating non può essere negativo")
    private Integer eloRating;

    @Min(value = 0, message = "Il montepremi non può scendere sotto lo zero")
    private Double montePremi;

    private StatoUtente stato;

    private Long[] ruoliIds;

    // Sostituito Long torneoId con TorneoDTO
    private TorneoDTO torneo;

    public UtenteDTO() {
    }

    public UtenteDTO(Long id, String nome, String cognome, String username, StatoUtente stato, Integer eloRating, Double montePremi) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.stato = stato;
        this.eloRating = eloRating;
        this.montePremi = montePremi;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getDataRegistrazione() { return dataRegistrazione; }
    public void setDataRegistrazione(LocalDate dataRegistrazione) { this.dataRegistrazione = dataRegistrazione; }

    public Integer getEloRating() { return eloRating; }
    public void setEloRating(Integer eloRating) { this.eloRating = eloRating; }

    public Double getMontePremi() { return montePremi; }
    public void setMontePremi(Double montePremi) { this.montePremi = montePremi; }

    public StatoUtente getStato() { return stato; }
    public void setStato(StatoUtente stato) { this.stato = stato; }

    public Long[] getRuoliIds() { return ruoliIds; }
    public void setRuoliIds(Long[] ruoliIds) { this.ruoliIds = ruoliIds; }

    public TorneoDTO getTorneo() { return torneo; }
    public void setTorneo(TorneoDTO torneo) { this.torneo = torneo; }

    public Utente buildUtenteModel(boolean includeRuoli) {
        Utente result = new Utente(this.nome, this.cognome, this.username, this.password,
                this.dataRegistrazione, this.eloRating, this.montePremi, this.stato);
        result.setId(this.id);

        if (includeRuoli && this.ruoliIds != null) {
            Set<Ruolo> ruoli = new HashSet<>();
            for (Long rId : this.ruoliIds) {
                ruoli.add(new Ruolo(rId));
            }
            result.setRuoli(ruoli);
        }

        // Controllo se il DTO del torneo è presente e ha un ID
        if (this.torneo != null && this.torneo.getId() != null) {
            Torneo t = new Torneo();
            t.setId(this.torneo.getId());
            result.setTorneo(t);
        }

        return result;
    }

    public static UtenteDTO buildUtenteDTOFromModel(Utente utenteModel) {
        UtenteDTO result = new UtenteDTO(
                utenteModel.getId(),
                utenteModel.getNome(),
                utenteModel.getCognome(),
                utenteModel.getUsername(),
                utenteModel.getStato(),
                utenteModel.getEloRating(),
                utenteModel.getMontePremi()
        );

        if (utenteModel.getDataRegistrazione() != null) {
            result.setDataRegistrazione(utenteModel.getDataRegistrazione());
        }

        if (utenteModel.getRuoli() != null && !utenteModel.getRuoli().isEmpty()) {
            result.ruoliIds = utenteModel.getRuoli().stream().map(Ruolo::getId).toArray(Long[]::new);
        }

        // Sfruttiamo il metodo esistente in TorneoDTO passando 'false' per evitare i partecipanti
        if (utenteModel.getTorneo() != null) {
            result.torneo = TorneoDTO.buildTorneoDTOFromModel(utenteModel.getTorneo(), false);
        }

        return result;
    }

    public Utente buildNuovoUtenteModel() {
        Utente result = new Utente(this.nome, this.cognome, this.username, this.password,
                this.dataRegistrazione);
        result.setMontePremi(0.0);
        result.setEloRating(0);
        result.setStato(StatoUtente.ATTIVO);
        return result;
    }
}
