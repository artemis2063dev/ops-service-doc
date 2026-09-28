package org.dahllab.opsservicedoc.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

// @RestControllerAdvice: fängt Exceptions projektweit ab, für ALLE
// Controller gleichzeitig - so muss ich die Fehlerbehandlung nicht in
// jedem einzelnen Controller (Ticket, Task, ...) wiederholen (DRY).
//
// Bisher wurde eine NoSuchElementException (geworfen von
// TicketService/TaskService, wenn eine ID nicht existiert) von Spring
// ungefangen bis nach oben durchgereicht und landete als 500 Internal
// Server Error - fachlich falsch, denn "ID nicht gefunden" ist ein
// Klientenfehler (falsche Anfrage), kein Serverfehler. Diese Klasse
// übersetzt die Exception deshalb korrekt in 404 Not Found.
@RestControllerAdvice
public class GlobalExceptionHandler {

    // @ExceptionHandler: greift, sobald IRGENDEIN Controller-Methodenaufruf
    // eine NoSuchElementException wirft (egal ob aus TicketService oder
    // TaskService). Ich gebe absichtlich nur die Fehlermeldung als Body
    // zurück, keine komplette Exception/Stacktrace - das würde interne
    // Details nach außen preisgeben, die den Client nichts angehen.
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
