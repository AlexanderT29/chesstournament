package com.example.chesstournament.dto;

import com.example.chesstournament.model.Utente;

public class RisultatoPartitaDTO {
    private Utente utenteAggiornato;
    private String messaggioEsito;

    public RisultatoPartitaDTO(Utente utenteAggiornato, String messaggioEsito) {
        this.utenteAggiornato = utenteAggiornato;
        this.messaggioEsito = messaggioEsito;
    }

    public Utente getUtenteAggiornato() { return utenteAggiornato; }
    public String getMessaggioEsito() { return messaggioEsito; }
}
