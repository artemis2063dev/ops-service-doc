package org.dahllab.opsservicedoc.controller;

import jakarta.validation.Valid;
import org.dahllab.opsservicedoc.dto.ChecklistTemplateDto;
import org.dahllab.opsservicedoc.service.ChecklistTemplateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checklist-templates")
public class ChecklistTemplateController {

    private final ChecklistTemplateService checklistTemplateService;

    public ChecklistTemplateController(ChecklistTemplateService checklistTemplateService) {
        this.checklistTemplateService = checklistTemplateService;
    }

    // GET /api/checklist-templates - liefert alle Vorlagen, z.B. damit
    // das Frontend eine Auswahlliste anzeigen kann, aus der ich beim
    // Anlegen einer Checkliste eine Vorlage auswähle.
    @GetMapping
    public List<ChecklistTemplateDto> getAllTemplates() {
        return checklistTemplateService.getAllTemplates();
    }

    // GET /api/checklist-templates/{id} - liefert genau eine Vorlage
    // anhand ihrer ID. Existiert die ID nicht, wirft der Service eine
    // NoSuchElementException, die der GlobalExceptionHandler in 404
    // übersetzt (gleiches Muster wie bei Ticket/Task/Checklist).
    @GetMapping("/{id}")
    public ChecklistTemplateDto getTemplateById(@PathVariable String id) {
        return checklistTemplateService.getTemplateById(id);
    }

    // POST /api/checklist-templates - legt eine neue Vorlage an, die
    // ich später über ChecklistService.createChecklistFromTemplate()
    // wiederverwenden kann.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChecklistTemplateDto createTemplate(@Valid @RequestBody ChecklistTemplateDto templateDto) {
        return checklistTemplateService.createTemplate(templateDto);
    }

    // PUT /api/checklist-templates/{id} - aktualisiert Name und
    // Item-Beschreibungen einer bestehenden Vorlage.
    @PutMapping("/{id}")
    public ChecklistTemplateDto updateTemplate(@PathVariable String id, @Valid @RequestBody ChecklistTemplateDto templateDto) {
        return checklistTemplateService.updateTemplate(id, templateDto);
    }

    // DELETE /api/checklist-templates/{id} - löscht eine Vorlage.
    // Bestehende Checklisten, die schon aus dieser Vorlage erzeugt
    // wurden, bleiben davon unberührt, da die Items dort eingebettet
    // und nicht mehr mit der Vorlage verknüpft sind.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTemplate(@PathVariable String id) {
        checklistTemplateService.deleteTemplate(id);
    }
}