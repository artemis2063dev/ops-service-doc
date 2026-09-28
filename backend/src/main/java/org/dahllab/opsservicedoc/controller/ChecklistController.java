package org.dahllab.opsservicedoc.controller;

import jakarta.validation.Valid;
import org.dahllab.opsservicedoc.dto.ChecklistDto;
import org.dahllab.opsservicedoc.dto.CreateChecklistFromTemplateRequest;
import org.dahllab.opsservicedoc.service.ChecklistService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklists")
public class ChecklistController {

    private final ChecklistService checklistService;

    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    // GET /api/checklists - liefert alle Checklisten, optional gefiltert
    // nach ticketId (gleiches Muster wie bei TaskController).
    @GetMapping
    public List<ChecklistDto> getAllChecklists(@RequestParam(required = false) String ticketId) {
        if (ticketId != null) {
            return checklistService.getChecklistsByTicketId(ticketId);
        }
        return checklistService.getAllChecklists();
    }

    // GET /api/checklists/{id} - liefert genau eine Checkliste anhand
    // ihrer ID.
    @GetMapping("/{id}")
    public ChecklistDto getChecklistById(@PathVariable String id) {
        return checklistService.getChecklistById(id);
    }

    // POST /api/checklists - legt eine neue Checkliste an.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChecklistDto createChecklist(@Valid @RequestBody ChecklistDto checklistDto) {
        return checklistService.createChecklist(checklistDto);
    }

    // POST /api/checklists/from-template - legt eine neue Checkliste
    // anhand einer vorhandenen ChecklistTemplate an, statt die Items
    // manuell im Request mitzuschicken.
    @PostMapping("/from-template")
    @ResponseStatus(HttpStatus.CREATED)
    public ChecklistDto createChecklistFromTemplate(@Valid @RequestBody CreateChecklistFromTemplateRequest request) {
        return checklistService.createChecklistFromTemplate(request.ticketId(), request.templateId());
    }

    // PUT /api/checklists/{id} - aktualisiert Titel und Items, z.B. um
    // einzelne Punkte abzuhaken.
    @PutMapping("/{id}")
    public ChecklistDto updateChecklist(@PathVariable String id, @Valid @RequestBody ChecklistDto checklistDto) {
        return checklistService.updateChecklist(id, checklistDto);
    }

    // DELETE /api/checklists/{id} - löscht eine Checkliste.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteChecklist(@PathVariable String id) {
        checklistService.deleteChecklist(id);
    }
}
