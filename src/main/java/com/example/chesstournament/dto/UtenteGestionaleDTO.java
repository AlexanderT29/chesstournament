package com.example.chesstournament.dto;

import com.example.chesstournament.model.Ruolo;
import com.example.chesstournament.model.StatoUtente;
import com.example.chesstournament.model.Utente;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtenteGestionaleDTO {

    private Long id;
    private String nome;
    private String cognome;
    private String username;
    private LocalDate dataRegistrazione;
    private StatoUtente stato;
    private Integer eloRating;
    private Double montePremi;
    private Long[] ruoliIds;


    private Set<TorneoDTO> torneiCreati = new HashSet<>();

    public UtenteGestionaleDTO() {}


    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }

    public void setNome(String nome) { this.nome = nome; }

    public String getCognome() { return cognome; }

    public void setCognome(String cognome) { this.cognome = cognome; }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public LocalDate getDataRegistrazione() { return dataRegistrazione; }

    public void setDataRegistrazione(LocalDate dataRegistrazione) { this.dataRegistrazione = dataRegistrazione; }

    public StatoUtente getStato() { return stato; }

    public void setStato(StatoUtente stato) { this.stato = stato; }

    public Integer getEloRating() { return eloRating; }

    public void setEloRating(Integer eloRating) { this.eloRating = eloRating; }

    public Double getMontePremi() { return montePremi; }

    public void setMontePremi(Double montePremi) { this.montePremi = montePremi; }

    public Long[] getRuoliIds() { return ruoliIds; }

    public void setRuoliIds(Long[] ruoliIds) { this.ruoliIds = ruoliIds; }

    public Set<TorneoDTO> getTorneiCreati() { return torneiCreati; }

    public void setTorneiCreati(Set<TorneoDTO> torneiCreati) { this.torneiCreati = torneiCreati; }



    /**
     * Converte il modello Utente in DTO completo di tornei gestiti.
     */
    public static UtenteGestionaleDTO buildDTOFromModel(Utente model) {
        UtenteGestionaleDTO dto = new UtenteGestionaleDTO();
        dto.setId(model.getId());
        dto.setNome(model.getNome());
        dto.setCognome(model.getCognome());
        dto.setUsername(model.getUsername());
        dto.setDataRegistrazione(model.getDataRegistrazione());
        dto.setStato(model.getStato());
        dto.setEloRating(model.getEloRating());
        dto.setMontePremi(model.getMontePremi());

        if (model.getRuoli() != null) {
            dto.setRuoliIds(model.getRuoli().stream()
                    .map(Ruolo::getId)
                    .toArray(Long[]::new));
        }

        if (model.getTorneiCreati() != null && !model.getTorneiCreati().isEmpty()) {
            dto.setTorneiCreati(model.getTorneiCreati().stream()
                    .map(t -> TorneoDTO.buildTorneoDTOFromModel(t, false))
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    /**
     * Converte il DTO in modello Utente per operazioni di persistenza.
     */
    public Utente buildModel() {
        Utente utente = new Utente();
        utente.setId(this.id);
        utente.setNome(this.nome);
        utente.setCognome(this.cognome);
        utente.setUsername(this.username);
        utente.setStato(this.stato);
        utente.setEloRating(this.eloRating);
        utente.setMontePremi(this.montePremi);
        if (this.ruoliIds != null) {
            Set<Ruolo> ruoli = new HashSet<>();
            for (Long rid : ruoliIds) {
                ruoli.add(new Ruolo(rid));
            }
            utente.setRuoli(ruoli);
        }

        return utente;
    }
}
