package com.example.chesstournament.security.auth;

import com.example.chesstournament.dto.UtenteDTO;
import com.example.chesstournament.model.Ruolo;
import com.example.chesstournament.model.StatoUtente;
import com.example.chesstournament.model.Utente;
import com.example.chesstournament.security.JWTUtil;
import com.example.chesstournament.security.dto.UtenteAuthDTO;
import com.example.chesstournament.service.RuoloService;
import com.example.chesstournament.service.UtenteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collections;
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
    public Map<String, Object> loginHandler(@RequestBody UtenteAuthDTO body) {
        try {
            UsernamePasswordAuthenticationToken authInputToken = new UsernamePasswordAuthenticationToken(
                    body.getUsername(), body.getPassword());

            authManager.authenticate(authInputToken);

            String token = jwtUtil.generateToken(body.getUsername());

            return Collections.singletonMap("jwt-token", token);
        } catch (AuthenticationException authExc) {
            // Nota: L'eccezione verrà poi catturata dal tuo eventuale @ControllerAdvice
            throw new RuntimeException("Credenziali non valide");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerHandler(@Valid @RequestBody UtenteDTO body) {


        if (utenteService.cercaPerUsername(body.getUsername()).orElse(null) != null) {
            return ResponseEntity.badRequest().body("Errore: Username già in uso");
        }


        Utente nuovoUtente = new Utente();
        nuovoUtente.setNome(body.getNome());
        nuovoUtente.setCognome(body.getCognome());
        nuovoUtente.setUsername(body.getUsername());


        nuovoUtente.setPassword(passwordEncoder.encode(body.getPassword()));


        nuovoUtente.setDataRegistrazione(LocalDate.now());
        nuovoUtente.setStato(StatoUtente.ATTIVO);
        nuovoUtente.setEloRating(0);
        nuovoUtente.setMontePremi(0.0);


        Ruolo ruoloPlayer = ruoloService.cercaPerCodice(Ruolo.ROLE_PLAYER)
                .orElseThrow(() -> new RuntimeException("Errore: Ruolo PLAYER non trovato nel database"));

        nuovoUtente.getRuoli().add(ruoloPlayer);

        // 5. Salvataggio
        utenteService.inserisciNuovo(nuovoUtente);

        return ResponseEntity.ok(Collections.singletonMap("message", "Registrazione completata con successo"));
    }
}
