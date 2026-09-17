package org.dahllab.opsservicedoc.controller;

import org.dahllab.opsservicedoc.dto.TicketDto;
import org.dahllab.opsservicedoc.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// REST-Controller für alles rund um Tickets.
// Nimmt HTTP-Anfragen entgegen und delegiert die eigentliche Arbeit
// an den TicketService, enthält selbst KEINE Business-Logik
// (Single Responsibility: Controller = Schnittstelle, Service = Logik).
@RestController
@RequestMapping
public class TicketController {


    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // GET /api/tickets, liefert alle Tickets zurück.
    // Durch die SecurityConfig bereits als "authenticated()" geschützt,
    // hier also keine zusätzliche Prüfug nötig (DRY: Security-Regeln
    // zentral in einer Klasse, nicht in jedem Controller wiederholt).
    @GetMapping
    public List<TicketDto> getAlleTickets() {
        return ticketService.getAlleTickets();
    }
}
