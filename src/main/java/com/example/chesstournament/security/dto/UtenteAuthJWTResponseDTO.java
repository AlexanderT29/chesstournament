package com.example.chesstournament.security.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UtenteAuthJWTResponseDTO {

    private String token;
    private String type;
    private String username;
    private List<String> roles;

    public UtenteAuthJWTResponseDTO(String accessToken, String username, List<String> roles) {
        this.token = accessToken;
        this.username = username;
        this.roles = roles;
        this.type = "Bearer";
    }

    public UtenteAuthJWTResponseDTO(String token, String username) {
        this.token = token;
        this.username = username;
    }

    public String getAccessToken() {
        return token;
    }

    public void setAccessToken(String accessToken) {
        this.token = accessToken;
    }

    public String getTokenType() {
        return type;
    }

    public void setTokenType(String tokenType) {
        this.type = tokenType;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getRoles() {
        return roles;
    }
}
