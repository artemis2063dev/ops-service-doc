package org.dahllab.opsservicedoc.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record ChecklistTemplateDto(
        String id,
        @NotBlank(message = "Name darf nicht leer sein")
        String name,
        @NotEmpty(message = "Eine Vorlage braucht mindestens einen Punkt")
        List<@NotBlank(message = "Ein Punkt darf nicht leer sein") String> itemBeschreibungen,
        // Nur lesend relevant - wird vom Service gesetzt
        // (createTemplate erzwingt immer false, updateTemplate behält
        // den bestehenden Wert bei) und von einem Request-Body IMMER
        // ignoriert, damit niemand über die API eine eigene Vorlage
        // nachträglich zur unlöschbaren Standard-Vorlage machen kann
        // oder umgekehrt. Fehlt das Feld im JSON (z.B. weil mein
        // Frontend es beim Anlegen/Bearbeiten gar nicht mitschickt),
        // setzt Jackson hier einfach false.
        boolean standard
) {
}