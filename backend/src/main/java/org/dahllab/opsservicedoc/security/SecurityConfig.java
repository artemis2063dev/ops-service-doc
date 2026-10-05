package org.dahllab.opsservicedoc.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.beans.factory.annotation.Value;

// @Configuration: sagt Spring, dass diese Klasse Bean-Definitionen enthält,
// die beim Hochfahren der App geladen werden sollen
@Configuration
// @EnableWebSecurity: aktiviert Spring Security für die gesamte Web-Anwendung
@EnableWebSecurity
public class SecurityConfig {

    // URL meines Frontends, konfigurierbar über die Umgebungsvariable
    // FRONTEND_URL (siehe application.properties), mit localhost:5173
    // als Fallback für die lokale Entwicklung. So bleibt der Code
    // unverändert, wenn ich später auf einem echten Server hoste -
    // ich muss dann nur die Umgebungsvariable setzen, statt hier im
    // Code eine feste URL zu hinterlegen (Review-Hinweis von Mahir).
    @Value("${app.frontend-url}")
    private String frontendUrl;

    // @Bean: diese Methode liefert ein Objekt, das Spring verwaltet (hier die zentrale Security-Konfiguration)
    // SecurityFilterChain: die Kette von Filtern, die JEDE eingehende HTTP-Anfrage durchläuft,
    // bevor sie beim eigentlichen Controller ankommt
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF (Cross-Site Request Forgery)-Schutz deaktivieren.
                // Sinnvoll bei einer REST-API mit separatem Frontend (kein klassisches Server-Side-Rendering
                // mit Formularen), da CSRF-Tokens hier keinen praktischen Schutz bieten
                .csrf(AbstractHttpConfigurer::disable)

                // Regeln, WELCHE Endpoints WELCHEN Zugriffsschutz brauchen.
                // WICHTIG: Die Reihenfolge zählt! Spring prüft von oben nach unten
                // und nimmt die erste passende Regel - spezifische Regeln müssen VOR allgemeinen stehen
                .authorizeHttpRequests(req -> req
                        // ALLE Endpoints unter /api/ nur für eingeloggte Nutzer.
                        // Vorher hatte ich jeden Controller einzeln aufgelistet
                        // (tickets, tasks, ipd, auth/me) - dabei habe ich die
                        // Checklisten und Checklisten-Vorlagen vergessen, die
                        // dadurch ohne Login erreichbar waren (anyRequest().permitAll()
                        // unten). Mit dem Muster /api/** ist ein neuer Controller
                        // automatisch geschützt, und ich muss hier nichts mehr
                        // nachtragen. Login-Pfade (/oauth2/**, /login/**) liegen
                        // außerhalb von /api/ und bleiben dadurch zugänglich.
                        .requestMatchers("/api/**").authenticated()
                        // Alles andere (z.B. die Login-Weiterleitung selbst) für jeden zugänglich
                        .anyRequest().permitAll()
                )

                // Was passiert, wenn ein NICHT eingeloggter User auf eine geschützte Route zugreift?
                // Standardmäßig würde Spring Security versuchen, auf eine Login-Seite umzuleiten (HTML-Verhalten).
                // Da ich eine REST-API ist, will ich stattdessen einfach einen 401-Statuscode zurückgeben,
                // den das Frontend dann selbst auswerten kann (z.B. um zur Login-Seite zu routen)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        ))

                // Aktiviert den OAuth2-Login-Flow (GitHub, wie in application.properties konfiguriert).
                // defaultSuccessUrl: wohin der Browser nach erfolgreichem Login weitergeleitet wird -
                // hier zurück zu meinem laufenden React-Frontend
                .oauth2Login(o -> o.defaultSuccessUrl(frontendUrl + "/", true))

                // Wohin nach dem Logout weitergeleitet wird - ebenfalls zurück zum Frontend
                .logout(logout -> logout.logoutSuccessUrl(frontendUrl + "/"));

        // Baut die konfigurierte Filterkette und gibt sie an Spring zurück
        return http.build();
    }
}