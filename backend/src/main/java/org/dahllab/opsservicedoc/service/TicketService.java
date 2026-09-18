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
import java.util.NoSuchElementException;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public List<TicketDto> getAllTickets() {
        if (ticketRepository.count() == 0) {
            ticketRepository.saveAll(erzeugeMockTickets());
        }

        return ticketRepository.findAll()
                .stream()
                .map(TicketMapper::toDto)
                .toList();
    }

    // Liefert ein einzelnes Ticket anhand seiner ID.
    // orElseThrow(): wirft eine NoSuchElementException, falls die ID
    // nicht existiert - einfache, eingebaute Java-Lösung statt einer
    // eigenen Exception-Klasse (KISS, solange kein spezielleres
    // Fehlerverhalten gebraucht wird).
    public TicketDto getTicketById(String id) {
        return ticketRepository.findById(id)
                .map(TicketMapper::toDto)
                .orElseThrow(() -> new NoSuchElementException("Ticket mit ID " + id + " nicht gefunden"));
    }

    // Legt ein neues Ticket an. Der eingehende TicketDto enthält noch
    // keine ID (die generiert MongoDB automatisch) und kein erstelltAm
    // (das setzen wir hier zentral auf "jetzt") - der Aufrufer muss sich
    // also nicht selbst um diese technischen Details kümmern.
    public TicketDto createTicket(TicketDto neuesTicket) {
        Ticket ticket = new Ticket(
                null,              // MongoDB generiert die ID
                null,              // glpiTicketId: beim manuellen Anlegen noch unbekannt
                neuesTicket.titel(),
                neuesTicket.beschreibung(),
                neuesTicket.status(),
                neuesTicket.techniker(),
                neuesTicket.szenarioTyp(),
                LocalDateTime.now()
        );

        Ticket gespeichertesTicket = ticketRepository.save(ticket);
        return TicketMapper.toDto(gespeichertesTicket);
    }

    // Aktualisiert ein bestehendes Ticket. Prüft zuerst, ob die ID existiert
    // (sonst NoSuchElementException, wie schon bei getTicketById), und
    // überschreibt dann alle Felder außer der ID selbst - die ID bleibt
    // erhalten, damit wir dasselbe Dokument in MongoDB aktualisieren statt
    // versehentlich ein neues anzulegen.
    //
    // BEKANNTE EINSCHRÄNKUNG: glpiTicketId wird hier auf null gesetzt und
    // geht damit bei jedem Update verloren. Muss angepasst werden, sobald
    // der GLPI-Connector steht - dann bestehende glpiTicketId aus der DB
    // laden und beibehalten statt zu überschreiben.
    public TicketDto updateTicket(String id, TicketDto aktualisiertesTicket) {
        if (!ticketRepository.existsById(id)) {
            throw new NoSuchElementException("Ticket mit ID " + id + " nicht gefunden");
        }

        Ticket ticket = new Ticket(
                id,
                null,
                aktualisiertesTicket.titel(),
                aktualisiertesTicket.beschreibung(),
                aktualisiertesTicket.status(),
                aktualisiertesTicket.techniker(),
                aktualisiertesTicket.szenarioTyp(),
                aktualisiertesTicket.erstelltAm()
        );

        Ticket gespeichertesTicket = ticketRepository.save(ticket);
        return TicketMapper.toDto(gespeichertesTicket);
    }

    private List<Ticket> erzeugeMockTickets() {
        return List.of(
                new Ticket(null, "GLPI-1001", "Server Enterprise-01 Wartung",
                        "Geplantes Patching des vSphere-Clusters außerhalb der Betriebszeiten.",
                        TicketStatus.NEU, "M. Scott", SzenarioTyp.SERVER_WARTUNG, LocalDateTime.now()),
                new Ticket(null, "GLPI-1002", "Backup-Check Enterprise-02",
                        "Wöchentliche Kontrolle der Backup-Jobs.",
                        TicketStatus.IN_BEARBEITUNG, "N. Uhura", SzenarioTyp.SERVER_WARTUNG, LocalDateTime.now())
        );
    }
}