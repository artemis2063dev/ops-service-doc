package org.dahllab.opsservicedoc.repository;

import org.dahllab.opsservicedoc.model.Ticket;
import org.springframework.data.mongodb.repository.MongoRepository;

// Repository-Interface für den Datenbankzugriff auf Tickets.
// MongoRepository<Ticket, String> gibt uns automatisch CRUD-Methoden
// (save, findById, findAll, delete, ...), OHNE dass ich sie selbst
// implementieren muss. Spring Data generiert die Implementierung
// zur Laufzeit (DRY; kein manueller Boilerplate-Code für Standard-Datenbankzugriffe).
//
// Generic-Parameter: <Ticket, String>
// - Ticket: die Entity-Klasse, die verwaltet wird
// - String: der Datentyp der ID (siehe @Id-Feld in Ticket.java)

public interface TicketRepository extends MongoRepository<Ticket, String>{

    // Aktuell keine zusätzlichen Query-Methoden nötig, die geerbten
    // Standard-Methoden reichen für den jetzigen Stand aus (YAGNI).

}
