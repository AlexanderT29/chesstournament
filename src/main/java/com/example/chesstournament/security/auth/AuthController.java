package com.example.chesstournament.security.auth;

import com.example.chesstournament.dto.UtenteDTO;
import com.example.chesstournament.model.Ruolo;
import com.example.chesstournament.model.StatoUtente;
import com.example.chesstournament.model.Utente;
import com.example.chesstournament.security.JWTUtil;
import com.example.chesstournament.security.dto.ResponseBusta;
import com.example.chesstournament.security.dto.UtenteAuthDTO;
import com.example.chesstournament.security.dto.UtenteAuthJWTResponseDTO;
import com.example.chesstournament.service.RuoloService;
import com.example.chesstournament.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JWTUtil jwtUtil;
    private final AuthenticationManager authManager;
    private final UtenteService utenteService;
    private final PasswordEncoder passwordEncoder;
    private final RuoloService ruoloService;

    public AuthController(JWTUtil jwtUtil, AuthenticationManager authManager, UtenteService utenteService, PasswordEncoder passwordEncoder, RuoloService ruoloService) {
        this.jwtUtil = jwtUtil;
        this.authManager = authManager;
        this.utenteService = utenteService;
        this.passwordEncoder = passwordEncoder;
        this.ruoloService = ruoloService;

    }

    @PostMapping("/login")
    public ResponseEntity<ResponseBusta<UtenteAuthJWTResponseDTO>> loginHandler(@RequestBody UtenteAuthDTO body) {
        try {
            UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
                    body.getUsername(), body.getPassword());

            Authentication auth = authManager.authenticate(authInputToken);

            String token = jwtUtil.generateToken(body.getUsername());

            List<String> ruoli = auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            UtenteAuthJWTResponseDTO payloadDati = new UtenteAuthJWTResponseDTO(
                    token,
                    body.getUsername()
            );

            ResponseBusta<UtenteAuthJWTResponseDTO> bustaSuccesso = ResponseBusta.success(200, payloadDati, "Login effettuato");

            return ResponseEntity.ok(bustaSuccesso);

        } catch (AuthenticationException authExc) {
            authExc.printStackTrace();
            System.out.println(body.getUsername() + " " + body.getPassword());
            // Nota: L'eccezione verrà poi catturata dal tuo eventuale @ControllerAdvice
            ResponseBusta<UtenteAuthJWTResponseDTO> bustaErrore = ResponseBusta.error(401, "Credenziali non valide o utente inesistente");
            return ResponseEntity.status(401).body(bustaErrore);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseBusta<String>> registerHandler(@Valid @RequestBody UtenteDTO body) {


        if (utenteService.cercaPerUsername(body.getUsername()).orElse(null) != null) {
            ResponseBusta<String> bustaErrore = ResponseBusta.error(400, "Errore: username già in uso");
            return ResponseEntity.badRequest().body(bustaErrore);
        }


        Utente nuovoUtente = new Utente();
        nuovoUtente.setNome(body.getNome());
        nuovoUtente.setCognome(body.getCognome());
        nuovoUtente.setUsername(body.getUsername());


        nuovoUtente.setPassword(body.getPassword());


        nuovoUtente.setDataRegistrazione(LocalDate.now());
        nuovoUtente.setStato(StatoUtente.ATTIVO);
        nuovoUtente.setEloRating(0);
        nuovoUtente.setMontePremi(0.0);


        Ruolo ruoloPlayer = ruoloService.cercaPerCodice(Ruolo.ROLE_PLAYER)
                .orElseThrow(() -> new RuntimeException("Errore: Ruolo PLAYER non trovato nel database"));

        nuovoUtente.getRuoli().add(ruoloPlayer);


        utenteService.inserisciNuovo(nuovoUtente);

        ResponseBusta<String> bustaSuccesso = ResponseBusta.success(200, null, "Registrazione completata con successo");

        return ResponseEntity.ok(bustaSuccesso);
    }
}
