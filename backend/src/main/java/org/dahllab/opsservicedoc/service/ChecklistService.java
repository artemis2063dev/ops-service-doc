package org.dahllab.opsservicedoc.service;

import org.dahllab.opsservicedoc.dto.ChecklistDto;
import org.dahllab.opsservicedoc.dto.ChecklistItemDto;
import org.dahllab.opsservicedoc.model.Checklist;
import org.dahllab.opsservicedoc.model.ChecklistItem;
import org.dahllab.opsservicedoc.repository.ChecklistRepository;
import org.dahllab.opsservicedoc.util.ChecklistMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class ChecklistService {

    private final ChecklistRepository checklistRepository;

    public ChecklistService(ChecklistRepository checklistRepository) {
        this.checklistRepository = checklistRepository;
    }

    // GET /api/checklists - liefert alle Checklisten.
    public List<ChecklistDto> getAllChecklists() {
        return checklistRepository.findAll().stream()
                .map(ChecklistMapper::toDto)
                .toList();
    }

    // GET /api/checklists?ticketId=... - liefert nur die Checklisten
    // zu einem bestimmten Ticket.
    public List<ChecklistDto> getChecklistsByTicketId(String ticketId) {
        return checklistRepository.findByTicketId(ticketId).stream()
                .map(ChecklistMapper::toDto)
                .toList();
    }

    // GET /api/checklists/{id} - liefert genau eine Checkliste.
    // Existiert die ID nicht, wirft NoSuchElementException, die der
    // GlobalExceptionHandler in 404 übersetzt (gleiches Muster wie bei
    // TaskService).
    public ChecklistDto getChecklistById(String id) {
        Checklist result = checklistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Checkliste mit ID " + id + " nicht gefunden"));
        return ChecklistMapper.toDto(result);
    }

    // POST /api/checklists - legt eine neue Checkliste an. Ich vergebe
    // für jedes Item ohne ID eine neue UUID und setze erstelltAm
    // automatisch auf jetzt. abgeschlossenAm wird trotzdem einmal über
    // setzeAbschlussdatumWennAlleErledigt() geprüft, damit das Verhalten
    // konsistent mit updateChecklist() bleibt (falls schon beim Anlegen
    // alle Items als erledigt reinkommen).
    public ChecklistDto createChecklist(ChecklistDto checklistDto) {
        List<ChecklistItem> items = erzeugeItemsMitId(checklistDto.items());

        Checklist neueChecklist = new Checklist(
                null,
                checklistDto.ticketId(),
                checklistDto.titel(),
                items,
                LocalDateTime.now(),
                null
        );

        setzeAbschlussdatumWennAlleErledigt(neueChecklist);

        Checklist result = checklistRepository.save(neueChecklist);
        return ChecklistMapper.toDto(result);
    }

    // PUT /api/checklists/{id} - aktualisiert Titel und Items einer
    // bestehenden Checkliste (z.B. um Items abzuhaken). erstelltAm
    // bleibt unverändert. abgeschlossenAm wird bei jedem Update neu
    // bewertet: sind jetzt alle Items erledigt, wird es gesetzt; wird
    // ein Item wieder auf "nicht erledigt" zurückgesetzt, wird
    // abgeschlossenAm wieder auf null gesetzt (analog zur
    // erledigtAm-Logik bei Task).
    public ChecklistDto updateChecklist(String id, ChecklistDto checklistDto) {
        Checklist bestehendeChecklist = checklistRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Checkliste mit ID " + id + " nicht gefunden"));

        List<ChecklistItem> items = erzeugeItemsMitId(checklistDto.items());

        Checklist aktualisierteChecklist = new Checklist(
                bestehendeChecklist.getId(),
                checklistDto.ticketId(),
                checklistDto.titel(),
                items,
                bestehendeChecklist.getErstelltAm(),
                null
        );

        setzeAbschlussdatumWennAlleErledigt(aktualisierteChecklist);

        Checklist result = checklistRepository.save(aktualisierteChecklist);
        return ChecklistMapper.toDto(result);
    }

    // DELETE /api/checklists/{id} - löscht eine Checkliste. Ich prüfe
    // vorher explizit mit existsById(), damit ein Löschversuch auf eine
    // unbekannte ID sauber mit 404 beantwortet wird, statt dass
    // deleteById() stillschweigend nichts tut.
    public void deleteChecklist(String id) {
        if (!checklistRepository.existsById(id)) {
            throw new NoSuchElementException("Checkliste mit ID " + id + " nicht gefunden");
        }
        checklistRepository.deleteById(id);
    }

    // Erzeugt aus den ChecklistItemDtos echte ChecklistItem-Objekte und
    // vergibt dabei eine neue UUID für jedes Item, das noch keine ID
    // hat (z.B. weil es gerade neu im Frontend hinzugefügt wurde).
    private List<ChecklistItem> erzeugeItemsMitId(List<ChecklistItemDto> itemDtos) {
        return itemDtos.stream()
                .map(itemDto -> new ChecklistItem(
                        itemDto.id() != null ? itemDto.id() : UUID.randomUUID().toString(),
                        itemDto.beschreibung(),
                        itemDto.erledigt()
                ))
                .toList();
    }

    // Setzt abgeschlossenAm auf jetzt, wenn wirklich alle Items der
    // Checkliste erledigt sind - sonst bleibt es null. Ich prüfe das
    // zentral in dieser Methode, damit createChecklist() und
    // updateChecklist() nicht zwei unterschiedliche Implementierungen
    // derselben Logik pflegen müssen.
    private void setzeAbschlussdatumWennAlleErledigt(Checklist checklist) {
        boolean alleErledigt = checklist.getItems().stream().allMatch(ChecklistItem::isErledigt);
        if (alleErledigt) {
            checklist.setAbgeschlossenAm(LocalDateTime.now());
        }
    }
}