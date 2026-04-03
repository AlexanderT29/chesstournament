package com.example.chesstournament.dto;

import com.example.chesstournament.model.StatoTorneo;

public class TorneoRicercaDTO {

    private String denominazione;

    private StatoTorneo stato;

    private Integer eloMinimo;

    private Double quotaIscrizione;

    private Integer maxGiocatori;

    private Long utenteCreazioneId;

    public TorneoRicercaDTO() {
    }

    public String getDenominazione() {
        return denominazione;
    }

    public void setDenominazione(String denominazione) {
        this.denominazione = denominazione;
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

    public Long getUtenteCreazioneId() {
        return utenteCreazioneId;
    }

    public void setUtenteCreazioneId(Long utenteCreazioneId) {
        this.utenteCreazioneId = utenteCreazioneId;
    }
}
