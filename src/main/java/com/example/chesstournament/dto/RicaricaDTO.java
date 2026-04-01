package com.example.chesstournament.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class RicaricaDTO {
    @NotNull(message = "L'importo è obbligatorio")
    @Min(value = 1, message = "La ricarica deve essere almeno di 1")
    private Double importo;

    public Double getImporto() { return importo; }
    public void setImporto(Double importo) { this.importo = importo; }
}
