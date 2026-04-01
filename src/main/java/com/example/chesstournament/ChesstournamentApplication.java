package com.example.chesstournament;

import com.example.chesstournament.model.Ruolo;
import com.example.chesstournament.model.Utente;
import com.example.chesstournament.service.RuoloService;
import com.example.chesstournament.service.UtenteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

@SpringBootApplication
public class ChesstournamentApplication implements CommandLineRunner {

	final private UtenteService utenteService;
	final private RuoloService ruoloService;

	public ChesstournamentApplication(UtenteService utenteService, RuoloService ruoloService) {
		this.utenteService = utenteService;
		this.ruoloService = ruoloService;
	}

	public static void main(String[] args) {
		SpringApplication.run(ChesstournamentApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if (utenteService.cercaPerUsername("admin").isEmpty()) {
			Utente admin = new Utente("Alexander", "Tesoro", "admin", "admin", LocalDate.now());
			admin.getRuoli().add(ruoloService.cercaPerCodice(Ruolo.ROLE_ADMIN).orElseThrow(() -> new RuntimeException("Errore: Ruolo PLAYER non trovato nel database")));
			utenteService.inserisciNuovo(admin);

		}
	}
}








