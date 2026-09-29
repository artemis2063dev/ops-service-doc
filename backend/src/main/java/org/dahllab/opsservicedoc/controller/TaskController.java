package org.dahllab.opsservicedoc.controller;

import jakarta.validation.Valid;
import org.dahllab.opsservicedoc.dto.TaskDto;
import org.dahllab.opsservicedoc.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// @RestController: markiert diese Klasse als REST-Endpunkt, jede
// Rückgabe wird automatisch als JSON serialisiert.
// Enthält bewusst KEINE Business-Logik (die steckt im TaskService),
// der Controller nimmt nur HTTP-Anfragen entgegen, reicht sie weiter
// und gibt das Ergebnis zurück (Single Responsibility Principle).
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    // Konstruktor-Injection statt @Autowired auf dem Feld, macht die
    // Abhängigkeit unveränderlich und explizit sichtbar.
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Optionaler Query-Parameter ticketId: ohne Parameter liefere ich alle
    // Tasks, mit Parameter nur die Tasks zu genau diesem Ticket, so
    // brauche ich keinen zweiten Endpunkt für den Filter.
    @GetMapping
    public List<TaskDto> getAllTasks(@RequestParam(required = false) String ticketId) {
        if (ticketId != null) {
            return taskService.getTasksByTicketId(ticketId);
        }
        return taskService.getAllTasks();
    }

    // Liefert einen einzelnen Task anhand seiner ID. Falls die ID nicht
    // existiert, wirft der Service eine NoSuchElementException, die der
    // globale GlobalExceptionHandler abfängt und sauber in 404 Not Found
    // übersetzt (siehe exception/GlobalExceptionHandler.java).
    @GetMapping("/{id}")
    public TaskDto getTaskById(@PathVariable String id) {
        return taskService.getTaskById(id);
    }

    // Legt einen neuen Task an. @Valid löst die Validierung im TaskDto aus
    // (z.B. @NotBlank auf thema), bei ungültigen Daten antwortet Spring
    // automatisch mit 400 Bad Request, bevor die Methode überhaupt läuft.
    // @ResponseStatus(CREATED): erfolgreiches Anlegen liefert 201 statt
    // des Standard-200, wie es sich für POST gehört.
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(@Valid @RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    // Aktualisiert einen bestehenden Task komplett (alle Felder werden
    // überschrieben). Die ID kommt aus der URL, nicht aus dem Body,
    // so kann ich nicht versehentlich die ID eines anderen Tasks überschreiben.
    @PutMapping("/{id}")
    public TaskDto updateTask(@PathVariable String id, @Valid @RequestBody TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    // Löscht einen Task endgültig. @ResponseStatus(NO_CONTENT): bei
    // erfolgreichem Löschen gibt es keinen Response-Body, nur 204,
    // das ist der übliche REST-Standard für DELETE.
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
    }
}