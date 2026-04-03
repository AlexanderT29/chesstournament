package com.example.chesstournament.dto;

import com.example.chesstournament.model.Utente;

public class RisultatoPartitaDTO {
    private Double utenteAggiornato;
    private String messaggioEsito;
    private Integer delta;

    public RisultatoPartitaDTO(Double utenteAggiornato, String messaggioEsito, Integer delta) {
        this.utenteAggiornato = utenteAggiornato;
        this.messaggioEsito = messaggioEsito;
        this.delta = delta;
    }

    public Double getUtenteAggiornato() { return utenteAggiornato; }
    public String getMessaggioEsito() { return messaggioEsito; }
    public Integer getDelta() { return delta; }

    public void setUtenteAggiornato(Double utenteAggiornato) { this.utenteAggiornato = utenteAggiornato; }
    public void setMessaggioEsito(String messaggioEsito) { this.messaggioEsito = messaggioEsito; }
    public void setDelta(Integer delta) { this.delta = delta; }
}
