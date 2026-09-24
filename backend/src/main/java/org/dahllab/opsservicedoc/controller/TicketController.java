package org.dahllab.opsservicedoc.controller;

import jakarta.validation.Valid;
import org.dahllab.opsservicedoc.dto.TicketDto;
import org.dahllab.opsservicedoc.service.TicketService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// REST-Controller für alles rund um Tickets.
// Nimmt HTTP-Anfragen entgegen und delegiert die eigentliche Arbeit
// an den TicketService, enthält selbst KEINE Business-Logik
// (Single Responsibility: Controller = Schnittstelle, Service = Logik).
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // GET /api/tickets - liefert alle Tickets zurück.
    // Durch die SecurityConfig bereits als "authenticated()" geschützt,
    // hier also keine zusätzliche Prüfung nötig (DRY: Security-Regeln
    // zentral in einer Klasse, nicht in jedem Controller wiederholt).
    @GetMapping
    public List<TicketDto> getAllTickets() {
        return ticketService.getAllTickets();
    }

    // GET /api/tickets/{id} - liefert genau EIN Ticket anhand seiner ID.
    // Falls die ID nicht existiert, wirft der Service eine Exception,
    // die wir hier (noch) nicht extra abfangen - Spring gibt in dem Fall
    // automatisch einen 500er zurück. Ein sauberes 404-Handling (z.B. mit
    // @RestControllerAdvice) wäre ein sinnvoller nächster Ausbauschritt,
    // aber laut YAGNI erst dann bauen, wenn er tatsächlich gebraucht wird.
    @GetMapping("/{id}")
    public TicketDto getTicketById(@PathVariable String id) {
        return ticketService.getTicketById(id);
    }

    // POST /api/tickets - legt ein neues Ticket manuell an.
    // @Valid: löst die Bean-Validation-Prüfungen aus (siehe @NotBlank
    // auf titel im Ticket-Model).
    // @ResponseStatus(CREATED): gibt korrekt 201 statt dem Standard-200
    // zurück, wie es sich für einen erfolgreichen POST gehört
    // (REST-Konvention, wie auch schon bei deinem Todo-Backend gemacht).
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketDto createTicket(@Valid @RequestBody TicketDto neuesTicket) {
        return ticketService.createTicket(neuesTicket);
    }

    // PUT /api/tickets/{id} - aktualisiert ein bestehendes Ticket vollständig
    // (z.B. um den Status zu ändern, wenn ein Techniker die Bearbeitung abschließt).
    // Anders als PATCH überschreibt PUT laut REST-Konvention alle Felder mit
    // den übergebenen Werten - der Aufrufer muss also das komplette Ticket
    // mitschicken, nicht nur das geänderte Feld.
    @PutMapping("/{id}")
    public TicketDto updateTicket(@PathVariable String id, @Valid @RequestBody TicketDto aktualisiertesTicket) {
        return ticketService.updateTicket(id, aktualisiertesTicket);
    }

    // POST /api/tickets/sync-glpi - stößt den manuellen Import aus GLPI an.
    // Bewusst als eigener Endpoint statt automatisch beim Start: so behalte
    // ich die Kontrolle, WANN der Sync passiert (relevant, da jeder Aufruf
    // aktuell neue Tickets anlegt statt zu aktualisieren, siehe Kommentar
    // in TicketService.syncFromGlpi()).
    @PostMapping("/sync-glpi")
    public List<TicketDto> syncFromGlpi() {
        return ticketService.syncFromGlpi();
    }
}