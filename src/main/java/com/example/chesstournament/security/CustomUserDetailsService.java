package com.example.chesstournament.security;

import java.util.Collection;

import com.example.chesstournament.model.StatoUtente;
import com.example.chesstournament.model.Utente;
import com.example.chesstournament.repository.UtenteRepository; // Assicurati che il path sia corretto
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utente user = utenteRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username " + username + " non trovato"));

        // Verifica se l'utente è attivo tramite l'enum
        boolean isAttivo = user.getStato() == StatoUtente.ATTIVO;

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                isAttivo, // enabled
                true,     // accountNonExpired
                true,     // credentialsNonExpired
                true,     // accountNonLocked
                getAuthorities(user));
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(Utente user) {
        String[] userRoles = user.getRuoli().stream().map(role -> role.getCodice()).toArray(String[]::new);
        return AuthorityUtils.createAuthorityList(userRoles);
    }
}
