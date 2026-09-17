package org.dahllab.opsservicedoc.service;

import org.dahllab.opsservicedoc.dto.TicketDto;
import org.dahllab.opsservicedoc.model.SzenarioTyp;
import org.dahllab.opsservicedoc.model.Ticket;
import org.dahllab.opsservicedoc.model.TicketStatus;
import org.dahllab.opsservicedoc.repository.TicketRepository;
import org.dahllab.opsservicedoc.util.TicketMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

// @Service: markiert diese Klasse als Spring-verwaltete Business-Logik-Komponente.
// Enthält die eigentliche Anwendungslogik, der Controller soll nur
// HTTP-Anfragen entgegennehmen/weiterleiten, nicht selbst Logik enthalten
// (Single Responsibility Principle).
@Service
public class TicketService {

    // Zugriff auf die Datenbank über das Repository.
    // "final" + Konstruktor-Injection statt @Autowired auf dem Feld:
    // macht die Abhängigkeit unveränderkich und explizit sichtbar,
    // erleichtert ausserdem das Testen mit Mockito.
    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    //
    //
    //
    //
    public List<TicketDto> getAlleTickets() {
        if (ticketRepository.count() == 0) {
            ticketRepository.saveAll(erzeugeMockTickets());

        }

        return ticketRepository.findAll()
                .stream()
                .map(TicketMapper::toDo)
                .toList();
    }
}
