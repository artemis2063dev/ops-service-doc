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

    // Liefert alle Ticktes als DTOs zurück.
    // Falls die Datenbank noch leer ist (z.B. beim allersten Start),
    // lege ich einmalig Mock-Daten an -praktisch zum Testen ohne
    // eigene Testdaten von Hand einfügen zu müssen.
    public List<TicketDto> getAlleTickets() {
        if (ticketRepository.count() == 0) {
            ticketRepository.saveAll(erzeugeMockTickets());

        }

        return ticketRepository.findAll()
                .stream()
                .map(TicketMapper::toDo)
                .toList();
    }

    // Erzeugt ein paar Beispiel-Tickets mit Star-Trek-Testdaten,
    // passend zu den bisherigen Projekten.
    // Bewusst als private Hilfsmethode innerhalb des Services gehalten,
    // da sie aktuell nur hier gebraucht wird (KISS: keine unnötige
    // eigene Klasse für einen einzigen Verwendungszweck).
    private List<Ticket> erzeugeMockTickets() {
        return List.of(
                new Ticket(
                        null,  //MongoDB generiert die ID automatisch beim Speichern
                        "GLPI-1001",
                        "Server Enterprise-01 Wartung",
                        "Geplantes Patching des vSphere-Clusters ausserhalb der Betriebszeiten",
                        TicketStatus.Neu,
                        "M. Scott",
                        SzenarioTyp.SERVER_WARTUNG,
                        LocalDateTime.now()
                ),
                new Ticket(
                        null,
                        "GLPI-1002",
                        "Backup-Check Enterprise-02",
                        "Wöchentliche Kontrolle der Backup-Jobs.",
                        TicketStatus.IN_BEARBEITUNG,
                        "N. Uhura",
                        SzenarioTyp.SERVER_WARTUNG,
                        LocalDateTime.now()
                )
        );
    }

}

