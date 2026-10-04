// Zentrale Fetch-Hilfsfunktion für alle API-Calls ans Backend.
// Warum ich das hier bündle, statt überall "fetch(...)" einzeln
// aufzurufen:
// - "credentials: 'include'" muss ich bei JEDEM Call mitschicken, sonst
//   schickt der Browser das Session-Cookie nicht mit, und jede Anfrage
//   wäre für Spring Security "nicht eingeloggt".
// - Ich werfe bei einer Fehlerantwort (4xx/5xx) eine echte Exception mit
//   Statuscode, damit ich in den Komponenten mit try/catch sauber auf
//   Fehler reagieren kann, statt jedes Mal manuell response.ok zu prüfen.

export class ApiError extends Error {
    status: number;

    constructor(status: number, message: string) {
        super(message);
        this.status = status;
    }
}

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
    const response = await fetch(path, {
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json',
            ...options.headers,
        },
        ...options,
    });

    if (!response.ok) {
        // Ich lese den Fehlertext mit, falls das Backend einen liefert (z.B.
        // vom GlobalExceptionHandler), sonst reicht der reine Statuscode.
        const errorText = await response.text().catch(() => '');
        throw new ApiError(response.status, errorText || response.statusText);
    }

    // 204 No Content (z.B. nach DELETE) hat keinen Body - response.json()
    // würde hier mit einem Parse-Fehler abbrechen, deshalb prüfe ich das
    // vorher ab.
    if (response.status === 204) {
        return undefined as T;
    }

    return response.json() as Promise<T>;
}

export const api = {
    get: <T>(path: string) => request<T>(path, { method: 'GET' }),
    post: <T>(path: string, body?: unknown) =>
        request<T>(path, { method: 'POST', body: body !== undefined ? JSON.stringify(body) : undefined }),
    put: <T>(path: string, body: unknown) =>
        request<T>(path, { method: 'PUT', body: JSON.stringify(body) }),
    delete: (path: string) => request<void>(path, { method: 'DELETE' }),
};

// Spezialfall /api/auth/me: liefert reinen Text (den GitHub-Usernamen),
// kein JSON - deshalb hier als eigene Funktion statt über "api.get".
export async function fetchCurrentUser(): Promise<string | null> {
    const response = await fetch('/api/auth/me', { credentials: 'include' });
    if (response.status === 401) {
        return null;
    }
    if (!response.ok) {
        throw new ApiError(response.status, response.statusText);
    }
    return response.text();
}

// --- Ergänzung für src/api/types.ts: ans Ende der Datei anhängen ---

export type IpdDocumentStatus = 'ENTWURF' | 'ABGESCHLOSSEN';
export const IPD_DOCUMENT_STATUS_LABELS: Record<IpdDocumentStatus, string> = {
    ENTWURF: 'Entwurf',
    ABGESCHLOSSEN: 'Abgeschlossen',
};

// Feldreihenfolge entspricht IpdDocumentDto.java im Backend, damit ich
// beim Abgleich nicht durcheinanderkomme.
export interface IpdDocumentDto {
    id: string;
    ticketId: string;
    status: IpdDocumentStatus;
    titel: string;
    techniker: string;
    szenarioTyp: SzenarioTyp;
    kunde: string | null;
    ansprechpartnerKunde: string | null;
    zeitraum: string | null;
    ausgangslage: string | null;
    anforderungen: string | null;
    infrastrukturUebersicht: string | null;
    serverUndVms: string | null;
    netzwerk: string | null;
    rollenUndVerantwortlichkeiten: string | null;
    backupKonzept: string | null;
    securityUeberlegungen: string | null;
    // Wird vom Backend automatisch aus den erledigten Tasks des Tickets
    // zusammengebaut - im Formular zeige ich das nur read-only an.
    durchgefuehrteSchritte: string | null;
    entscheidungen: string | null;
    risikenUndAnnahmen: string | null;
    rollbackPlan: string | null;
    // Wird vom Backend automatisch aus den Checklisten des Tickets
    // ermittelt - ebenfalls nur read-only.
    qualitaetssicherungAbgeschlossen: boolean;
    erstelltAm: string;
    aktualisiertAm: string;
}

// Nur die Felder, die ich im Bearbeiten-Formular wirklich anbiete -
// ticketId/techniker/szenarioTyp/durchgefuehrteSchritte/
// qualitaetssicherungAbgeschlossen ignoriert IpdDocumentService.updateIpdDocument()
// ohnehin serverseitig, die muss ich also gar nicht erst im Request
// mitschicken (siehe handleSpeichern in IpdDocumentPage für den
// vollständigen DTO, den ich trotzdem ans Backend schicken muss).
export type IpdDocumentFormData = Pick<
    IpdDocumentDto,
    | 'titel'
    | 'kunde'
    | 'ansprechpartnerKunde'
    | 'zeitraum'
    | 'ausgangslage'
    | 'anforderungen'
    | 'infrastrukturUebersicht'
    | 'serverUndVms'
    | 'netzwerk'
    | 'rollenUndVerantwortlichkeiten'
    | 'backupKonzept'
    | 'securityUeberlegungen'
    | 'entscheidungen'
    | 'risikenUndAnnahmen'
    | 'rollbackPlan'
    | 'status'
>;