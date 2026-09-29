package org.dahllab.opsservicedoc.service;

import org.dahllab.opsservicedoc.dto.ChecklistTemplateDto;
import org.dahllab.opsservicedoc.model.ChecklistTemplate;
import org.dahllab.opsservicedoc.repository.ChecklistTemplateRepository;
import org.dahllab.opsservicedoc.util.ChecklistTemplateMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ChecklistTemplateService {

    private final ChecklistTemplateRepository checklistTemplateRepository;

    public ChecklistTemplateService(ChecklistTemplateRepository checklistTemplateRepository) {
        this.checklistTemplateRepository = checklistTemplateRepository;
    }

    // GET /api/checklist-templates - liefert alle Vorlagen.
    public List<ChecklistTemplateDto> getAllTemplates() {
        return checklistTemplateRepository.findAll().stream()
                .map(ChecklistTemplateMapper::toDto)
                .toList();
    }

    // GET /api/checklist-templates/{id} - liefert genau eine Vorlage.
    public ChecklistTemplateDto getTemplateById(String id) {
        ChecklistTemplate result = checklistTemplateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Checklisten-Vorlage mit ID " + id + " nicht gefunden"));
        return ChecklistTemplateMapper.toDto(result);
    }

    // POST /api/checklist-templates - legt eine neue Vorlage an.
    public ChecklistTemplateDto createTemplate(ChecklistTemplateDto templateDto) {
        ChecklistTemplate neueTemplate = new ChecklistTemplate(null, templateDto.name(), templateDto.itemBeschreibungen());
        ChecklistTemplate result = checklistTemplateRepository.save(neueTemplate);
        return ChecklistTemplateMapper.toDto(result);
    }

    // PUT /api/checklist-templates/{id} - aktualisiert Name und Punkte
    // einer bestehenden Vorlage.
    public ChecklistTemplateDto updateTemplate(String id, ChecklistTemplateDto templateDto) {
        ChecklistTemplate bestehendeTemplate = checklistTemplateRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Checklisten-Vorlage mit ID " + id + " nicht gefunden"));

        ChecklistTemplate aktualisierteTemplate = new ChecklistTemplate(
                bestehendeTemplate.getId(), templateDto.name(), templateDto.itemBeschreibungen());

        ChecklistTemplate result = checklistTemplateRepository.save(aktualisierteTemplate);
        return ChecklistTemplateMapper.toDto(result);
    }

    // DELETE /api/checklist-templates/{id} - löscht eine Vorlage.
    public void deleteTemplate(String id) {
        if (!checklistTemplateRepository.existsById(id)) {
            throw new NoSuchElementException("Checklisten-Vorlage mit ID " + id + " nicht gefunden");
        }
        checklistTemplateRepository.deleteById(id);
    }
}