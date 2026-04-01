package com.example.chesstournament.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseBusta<T> {

    private Integer status;

    private T dati;

    private String messaggio;

    private String errore;

    public ResponseBusta(){}

    public ResponseBusta(Integer status, T dati, String messaggio) {
        this.status = status;
        this.dati = dati;
        this.messaggio = messaggio;
    }

    public ResponseBusta(Integer status, String errore) {
        this.status = status;
        this.errore = errore;
    }

    public static <T> ResponseBusta<T> success(Integer status, T dati, String messaggio){
        return new ResponseBusta<>(status, dati, messaggio);
    }

    public static <T> ResponseBusta<T> error(Integer status, String errore){
        return new ResponseBusta<>(status, errore);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public T getDati() {
        return dati;
    }

    public void setDati(T dati) {
        this.dati = dati;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public String getErrore() {
        return errore;
    }

    public void setErrore(String errore) {
        this.errore = errore;
    }
}
