package org.dahllab.opsservicedoc.security;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController: kombiniert @Controller + @ResponseBody -
// jede Methode gibt direkt Daten zurück (hier: Text/JSON), kein HTML-Template
@RestController
// Basis-Pfad für alle Endpoints dieser Klasse: alle Routen beginnen mit /api/auth
@RequestMapping("/api/auth")
public class LoginController {

    // GET-Endpoint unter /api/auth/me
    @GetMapping("/me")
    // @AuthenticationPrincipal: Spring Security injiziert hier automatisch
    // das OAuth2User-Objekt des GERADE EINGELOGGTEN Nutzers (aus der Session).
    // Das funktioniert nur, weil die SecurityConfig diesen Endpoint als "authenticated()" markiert hat -
    // ohne gültigen Login wäre "user" hier null bzw. der Request würde vorher schon mit 401 abgelehnt
    public String getMe(@AuthenticationPrincipal OAuth2User user) {
        return user
                .getAttributes()          // alle Rohdaten, die GitHub über den Nutzer zurückgibt (Map<String, Object>)
                .get("login")             // "login" ist bei GitHub das Feld für den Username (z.B. "artemis2063dev")
                .toString();              // Kommentar vom Coach: getName() würde stattdessen die interne GitHub-ID liefern, nicht den Usernamen
    }
}