package com.example.chesstournament.dto;

import com.example.chesstournament.model.StatoTorneo;
import com.example.chesstournament.model.Torneo;
import com.example.chesstournament.model.Utente;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TorneoDTO {

    private Long id;

    @NotBlank(message = "La denominazione non può essere vuota")
    private String denominazione;

    private LocalDate dataCreazione;

    private StatoTorneo stato;

    @NotNull(message = "L'ELO minimo è obbligatorio")
    @Min(value = 0, message = "L'ELO minimo non può essere negativo")
    private Integer eloMinimo;

    @NotNull(message = "La quota di iscrizione è obbligatoria")
    @Min(value = 0, message = "La quota di iscrizione non può essere negativa")
    private Double quotaIscrizione;

    @NotNull(message = "Il numero massimo di giocatori è obbligatorio")
    @Min(value = 2, message = "Il torneo deve avere almeno 2 giocatori")
    private Integer maxGiocatori;


    private Long utenteCreazioneId;

    private String usernameCreatore;

    private Integer numeroIscritti;

    private Set<Long> partecipantiIds;

    private Set<UtenteDTO> partecipantiDettaglio;

    public TorneoDTO() {
    }

    public TorneoDTO(Long id, String denominazione, LocalDate dataCreazione, StatoTorneo stato,
                     Integer eloMinimo, Double quotaIscrizione, Integer maxGiocatori) {
        this.id = id;
        this.denominazione = denominazione;
        this.dataCreazione = dataCreazione;
        this.stato = stato;
        this.eloMinimo = eloMinimo;
        this.quotaIscrizione = quotaIscrizione;
        this.maxGiocatori = maxGiocatori;
    }

    public TorneoDTO(String denominazione, LocalDate dataCreazione, StatoTorneo stato, Integer eloMinimo, Double quotaIscrizione, Integer maxGiocatori) {
        this.denominazione = denominazione;
        this.dataCreazione = dataCreazione;
        this.stato = stato;
        this.eloMinimo = eloMinimo;
        this.quotaIscrizione = quotaIscrizione;
        this.maxGiocatori = maxGiocatori;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDenominazione() { return denominazione; }
    public void setDenominazione(String denominazione) { this.denominazione = denominazione; }

    public LocalDate getDataCreazione() { return dataCreazione; }
    public void setDataCreazione(LocalDate dataCreazione) { this.dataCreazione = dataCreazione; }

    public StatoTorneo getStato() { return stato; }
    public void setStato(StatoTorneo stato) { this.stato = stato; }

    public Integer getEloMinimo() { return eloMinimo; }
    public void setEloMinimo(Integer eloMinimo) { this.eloMinimo = eloMinimo; }

    public Double getQuotaIscrizione() { return quotaIscrizione; }
    public void setQuotaIscrizione(Double quotaIscrizione) { this.quotaIscrizione = quotaIscrizione; }

    public Integer getMaxGiocatori() { return maxGiocatori; }
    public void setMaxGiocatori(Integer maxGiocatori) { this.maxGiocatori = maxGiocatori; }

    public Long getUtenteCreazioneId() { return utenteCreazioneId; }
    public void setUtenteCreazioneId(Long utenteCreazioneId) { this.utenteCreazioneId = utenteCreazioneId; }

    public String getUsernameCreatore() { return usernameCreatore; }
    public void setUsernameCreatore(String usernameCreatore) { this.usernameCreatore = usernameCreatore; }

    public Integer getNumeroIscritti() { return numeroIscritti; }
    public void setNumeroIscritti(Integer numeroIscritti) { this.numeroIscritti = numeroIscritti; }

    public Set<Long> getPartecipantiIds() { return partecipantiIds; }
    public void setPartecipantiIds(Set<Long> partecipantiIds) { this.partecipantiIds = partecipantiIds; }

    public Set<UtenteDTO> getPartecipantiDettaglio() {
        return partecipantiDettaglio;
    }

    public void setPartecipantiDettaglio(Set<UtenteDTO> partecipantiDettaglio) {
        this.partecipantiDettaglio = partecipantiDettaglio;
    }

    public Torneo buildTorneoModel(boolean includeCreatore) {
        Torneo result = new Torneo(
                this.denominazione,
                this.dataCreazione,
                this.stato,
                this.eloMinimo,
                this.quotaIscrizione,
                this.maxGiocatori,
                null
        );
        result.setId(this.id);

        if (includeCreatore && this.utenteCreazioneId != null) {
            Utente creatore = new Utente();
            creatore.setId(this.utenteCreazioneId);
            result.setUtenteCreazione(creatore);
        }

        return result;
    }

    public static TorneoDTO buildTorneoDTOFromModel(Torneo torneoModel, boolean includeDettagliPartecipanti) {
        TorneoDTO result = new TorneoDTO(
                torneoModel.getId(),
                torneoModel.getDenominazione(),
                torneoModel.getDataCreazione(),
                torneoModel.getStato(),
                torneoModel.getEloMinimo(),
                torneoModel.getQuotaIscrizione(),
                torneoModel.getMaxGiocatori()
        );

        if (torneoModel.getUtenteCreazione() != null) {
            result.setUtenteCreazioneId(torneoModel.getUtenteCreazione().getId());
            result.setUsernameCreatore(torneoModel.getUtenteCreazione().getUsername());
        }

        if (torneoModel.getPartecipanti() != null) {
            result.setNumeroIscritti(torneoModel.getPartecipanti().size());

            if (includeDettagliPartecipanti) {


                Set<UtenteDTO> dettagliMappati = torneoModel.getPartecipanti().stream()
                        .map(utente -> {
                            UtenteDTO dto = UtenteDTO.buildUtenteDTOFromModel(utente);
                            dto.setTorneo(null);
                            dto.setPassword(null);
                            dto.setRuoliIds(null);
                            dto.setMontePremi(null);
                            return dto;
                        })
                        .collect(Collectors.toSet());
                result.setPartecipantiDettaglio(dettagliMappati);
            }
        } else {
            result.setNumeroIscritti(0);
        }

        return result;
    }

    public Torneo buildTorneoNuovoModel(Utente utenteLoggato){
        Torneo result = new Torneo(
                this.denominazione,
                this.eloMinimo,
                this.quotaIscrizione,
                this.maxGiocatori
        );
        result.setId(this.id);
        result.setStato(StatoTorneo.APERTURA);
        result.setDataCreazione(LocalDate.now());
        result.setUtenteCreazione(utenteLoggato);
        result.setPartecipanti(new HashSet<>(0));

        return result;
    }
}
