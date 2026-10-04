package org.dahllab.opsservicedoc.config;

import lombok.RequiredArgsConstructor;
import org.dahllab.opsservicedoc.model.ChecklistTemplate;
import org.dahllab.opsservicedoc.repository.ChecklistTemplateRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// Dieser CommandLineRunner läuft einmal automatisch beim Start der
// Anwendung (Spring Boot ruft run() auf, nachdem der ApplicationContext
// fertig aufgebaut ist) und legt meine zehn Standard-Checklisten-Vorlagen
// in MongoDB an, falls dort noch keine Vorlagen existieren. So muss ich
// die Vorlagen (Server, Netzwerk, Security, usw.) nicht jedes Mal von
// Hand im Frontend eintippen, sondern sie stehen im "Aus Vorlage"-
// Dropdown auf der Checklisten-Seite sofort zur Auswahl bereit.
//
// Die Inhalte stammen aus meiner eigenen Excel-Datei (OpsDoc_Checklisten.xlsx)
// mit den zehn Arbeitsblättern Server, Hardware, Netzwerk, Security,
// Client, Software_Deployment, VMware, Proxmox, Hyper-V und
// Backup_Restore - das ist die aktuell gepflegte Version (vier Blätter
// davon sind dort als "Überarbeitet" markiert, der Rest als "Original").
//
// Wichtiger Hinweis zum Datenmodell: ChecklistTemplate speichert pro
// Punkt nur einen einzigen String (itemBeschreibungen: List<String>) -
// es gibt kein separates Feld für "Phase" (z.B. "Vorbereitung",
// "Installation", "Security") oder für "Pflicht/Optional" aus meiner
// Excel-Tabelle. Damit diese Information trotzdem nicht verloren geht,
// codiere ich sie direkt mit in den Text hinein:
//   - Die Phase steht in eckigen Klammern vorangestellt, z.B.
//     "[Konfiguration] Domänenbeitritt ... durchführen"
//   - Ist ein Punkt in der Excel-Tabelle als "Optional" markiert (statt
//     "Ja" = Pflicht), hänge ich " (optional)" ans Ende an.
// Das baue ich unten mit der kleinen Hilfsmethode punkt(...) zusammen,
// damit ich beim eigentlichen Anlegen der Vorlagen nur noch Phase, Text
// und ggf. "optional" angeben muss, statt jedes Mal den fertigen String
// von Hand zu verketten.
@Component
@RequiredArgsConstructor
public class ChecklistTemplateSeeder implements CommandLineRunner {

    private final ChecklistTemplateRepository checklistTemplateRepository;

    @Override
    public void run(String... args) {
        // Guard gegen doppeltes Einfügen: Ohne diese Prüfung würde bei
        // jedem Neustart der Anwendung ein weiterer Satz der zehn
        // Vorlagen angelegt werden. Da ich hier nur beim allerersten
        // Start (leere Collection) etwas tun will, reicht eine einfache
        // count()-Prüfung - für eigene, zusätzlich von Hand angelegte
        // Vorlagen greift das nicht mehr ein, sobald schon mindestens
        // eine Vorlage existiert.
        if (checklistTemplateRepository.count() > 0) {
            return;
        }

        List<ChecklistTemplate> vorlagen = new ArrayList<>();

        vorlagen.add(vorlage("Server",
                punkt("Vorbereitung", "Servername nach Namenskonvention festlegen"),
                punkt("Vorbereitung", "IP-Adresse, Subnetz, Gateway und DNS festlegen"),
                punkt("Vorbereitung", "Rolle/Zweck des Servers dokumentieren"),
                punkt("Vorbereitung", "Betriebssystem und Edition festlegen"),
                punkt("Installation", "Betriebssystem installieren"),
                punkt("Installation", "Aktivierung/Lizenzstatus prüfen"),
                punkt("Konfiguration", "Hostname und Netzwerkkonfiguration setzen"),
                punkt("Konfiguration", "Zeitserver/NTP konfigurieren und Zeit prüfen"),
                punkt("Konfiguration", "Domänenbeitritt bzw. Verzeichnisanbindung durchführen", true),
                punkt("Konfiguration", "Benutzer, Gruppen und Berechtigungen konfigurieren", true),
                punkt("Konfiguration", "Freigaben und Dateisystemberechtigungen konfigurieren", true),
                punkt("Security", "Firewall aktiv und passend konfiguriert"),
                punkt("Security", "Patch-/Update-Stand vollständig"),
                punkt("Security", "Hardening durchgeführt (Remote-Zugriff absichern, veraltete Auth-Protokolle deaktivieren, TLS 1.2+, lokalen Administrator absichern)"),
                punkt("Security", "Lokale Administratorrechte geprüft, Passwortlösung für lokale Admin-Konten aktiv"),
                punkt("Security", "Zertifikate nur aus vertrauenswürdigen Domänen zugelassen", true),
                punkt("Betrieb", "Eventlogs/Systemlogs auf Fehler geprüft"),
                punkt("Betrieb", "Monitoring angebunden und Funktion geprüft"),
                punkt("Betrieb", "Backup angebunden und erster Lauf erfolgreich"),
                punkt("Betrieb", "Restore-/Wiederherstellungstest geplant oder durchgeführt"),
                punkt("Betrieb", "Server in Configuration Management Database (CMDB) erfasst"),
                punkt("Betrieb", "Server im IP-Adress-/Inventar-Tool erfasst", true),
                punkt("Security", "Kritisches System ggf. in Datei-Integritäts-/Audit-Überwachung aufgenommen", true),
                punkt("Security", "Rollenbasierte Remote-Zugriffsgruppen zugewiesen (nach Zuständigkeit)", true),
                punkt("Abnahme", "Remotezugriff/Betriebszugriff geprüft", true),
                punkt("Abnahme", "Dokumentation vollständig"),
                punkt("Abnahme", "Fachlicher Funktionstest erfolgreich"),
                punkt("Abnahme", "Übergabe/Abnahme dokumentiert (inkl. Verantwortlicher und Datum)")
        ));

        vorlagen.add(vorlage("Hardware",
                punkt("Inventar", "Hersteller, Modell und Seriennummer erfassen"),
                punkt("Inventar", "Asset-Tag/Inventarnummer erfassen", true),
                punkt("Standort", "Standort, Raum, Rack und Höheneinheit dokumentieren"),
                punkt("Strom", "Redundante Netzteile vorhanden und angeschlossen", true),
                punkt("Strom", "USV-/PDU-Anbindung geprüft", true),
                punkt("Hardware", "CPU-Konfiguration geprüft"),
                punkt("Hardware", "RAM-Bestückung und erkannte Kapazität geprüft"),
                punkt("Storage", "Datenträgeranzahl, Typ und Kapazität geprüft"),
                punkt("Storage", "RAID-Level/Storage-Konfiguration festgelegt", true),
                punkt("Storage", "RAID/Controller-Status fehlerfrei"),
                punkt("Firmware", "BIOS/UEFI-Version geprüft/aktualisiert"),
                punkt("Firmware", "RAID-, NIC- und Controller-Firmware geprüft"),
                punkt("Management", "Out-of-Band-Management konfiguriert", true),
                punkt("Management", "Management-IP, DNS und Zugriff dokumentiert", true),
                punkt("Netzwerk", "NICs und physische Verkabelung geprüft"),
                punkt("Netzwerk", "Switchports/VLANs dokumentiert"),
                punkt("Diagnose", "Herstellerdiagnose/Hardwaretest erfolgreich"),
                punkt("Installation", "Ziel-OS oder Hypervisor installiert"),
                punkt("Betrieb", "Monitoring/Hardware-Alerts angebunden", true),
                punkt("Betrieb", "Garantie-/Supportstatus dokumentiert", true),
                punkt("Abnahme", "Burn-in/Funktionstest abgeschlossen"),
                punkt("Abnahme", "Hardwaredokumentation vollständig")
        ));

        vorlagen.add(vorlage("Netzwerk",
                punkt("Übersicht", "Netzwerkskizze mit allen Komponenten vorhanden (Arbeitsplatzrechner, Server, Switches, Router, Drucker, mobile Geräte)"),
                punkt("Übersicht", "Bei jedem Gerät dokumentiert: Zuständiger, Standort, gespeicherte Daten", true),
                punkt("Zugang", "Netzwerkanschlüsse und -dosen physisch abgesichert bzw. deaktiviert wenn ungenutzt"),
                punkt("Zugang", "Standardzugangsdaten auf allen Netzwerkkomponenten geändert"),
                punkt("Zugang", "Server-/Fileserver-Standort zusätzlich abgesichert (abschließbarer Raum, regelmäßige Scans)", true),
                punkt("Security", "Firewall nur auf notwendige Zugriffe beschränkt, Regeln regelmäßig geprüft"),
                punkt("Security", "Gäste-WLAN vom internen Netzwerk getrennt"),
                punkt("Security", "WLAN-Verschlüsselung auf stärkstem verfügbaren Standard, SSID-Broadcast deaktiviert"),
                punkt("Security", "WLAN-Zugang auf bestimmte Gerätekennungen beschränkt (sofern praktikabel)", true),
                punkt("Betrieb", "Zugriffe auf Server, Router und Switches protokolliert"),
                punkt("Betrieb", "Ungenutzte Ports deaktiviert", true),
                punkt("Abnahme", "IP-Vergabe und Subnetzstruktur dokumentiert")
        ));

        vorlagen.add(vorlage("Security",
                punkt("Organisation", "IT-Sicherheitsrichtlinie vorhanden und Mitarbeitende unterwiesen", true),
                punkt("Organisation", "Regelmäßige Security-Awareness-Schulungen (z. B. Phishing, Passwortsicherheit)", true),
                punkt("Zugang", "Individuelle Benutzerkonten für alle Nutzer, getrennte Admin-/Benutzerkonten"),
                punkt("Zugang", "Starke Passwortrichtlinie erzwungen (Mindestlänge, Komplexität)"),
                punkt("Zugang", "Zwei-Faktor-Authentifizierung eingesetzt (mind. für kritische Systeme/VPN)"),
                punkt("Zugang", "Verfahren zur Deaktivierung von Konten ausgeschiedener Mitarbeitender vorhanden"),
                punkt("Endgerät", "Antivirus-/Endpoint-Schutz auf allen Geräten aktiv und aktuell"),
                punkt("Endgerät", "Patch-Management für Betriebssystem und Anwendungen eingerichtet"),
                punkt("Endgerät", "Festplattenverschlüsselung auf mobilen Geräten aktiv"),
                punkt("Endgerät", "Externe Geräte/USB-Anschlüsse kontrolliert oder eingeschränkt", true),
                punkt("Remote", "Fernzugriff nur über abgesicherte VPN-Verbindung"),
                punkt("Kommunikation", "Spam-/Virenfilter für E-Mail aktiv, verdächtige Anhänge werden markiert/blockiert", true),
                punkt("Kommunikation", "Meldeweg für Phishing-Versuche vorhanden", true),
                punkt("Cloud", "Cloud-Dienste datenschutzkonform genutzt, Verträge mit Auftragsverarbeitern vorhanden", true),
                punkt("Monitoring", "Sicherheitsrelevante Ereignisse zentral protokolliert (Log-Management/SIEM)", true),
                punkt("Notfall", "Notfallplan für Sicherheitsvorfälle vorhanden, Zuständigkeiten geklärt", true),
                punkt("Notfall", "Notfallmanagement regelmäßig getestet", true),
                punkt("Audit", "Regelmäßige interne/externe Audits durchgeführt, Ergebnisse dokumentiert", true)
        ));

        vorlagen.add(vorlage("Client",
                punkt("Inventar", "Gerätename und Benutzerzuordnung erfasst"),
                punkt("Installation", "Betriebssystem installiert und aktiviert"),
                punkt("Updates", "Betriebssystem vollständig aktualisiert"),
                punkt("Updates", "Automatisches Patchmanagement aktiviert", true),
                punkt("Security", "Client-Firewall aktiv"),
                punkt("Security", "Virenschutz/Endpoint Protection aktiv und aktuell"),
                punkt("Security", "Festplattenverschlüsselung aktiviert", true),
                punkt("Security", "Bildschirmsperre konfiguriert"),
                punkt("Identity", "Benutzerkonto eingerichtet"),
                punkt("Identity", "2FA eingerichtet, falls erforderlich", true),
                punkt("Netzwerk", "LAN/WLAN und DNS funktionieren"),
                punkt("Remote", "VPN eingerichtet, falls erforderlich", true),
                punkt("Daten", "Netzlaufwerke/Dateiablagen erreichbar", true),
                punkt("Software", "Standardsoftware installiert"),
                punkt("Software", "Individuelle Anwendungen installiert und getestet", true),
                punkt("Peripherie", "Drucker/Scanner/sonstige Geräte geprüft", true),
                punkt("Management", "Client in Management-/Inventarsystem aufgenommen", true),
                punkt("Backup", "Benutzerdaten-/Profilstrategie geprüft", true),
                punkt("Abnahme", "Benutzeranmeldung und Funktionstest erfolgreich"),
                punkt("Abnahme", "Übergabe dokumentiert")
        ));

        vorlagen.add(vorlage("Software-Deployment",
                punkt("Planung", "Softwareprodukt, Version und Zielgruppe festgelegt"),
                punkt("Planung", "Systemvoraussetzungen geprüft"),
                punkt("Planung", "Lizenz-/Nutzungsbedingungen geklärt"),
                punkt("Planung", "Abhängigkeiten/Runtime/DB-Anforderungen dokumentiert", true),
                punkt("Planung", "Pilot-/Testgruppe definiert", true),
                punkt("Paketierung", "Installationsmedium/Paket geprüft"),
                punkt("Paketierung", "Silent-/unattended Installation verfügbar bzw. getestet", true),
                punkt("Paketierung", "Installationsparameter dokumentiert"),
                punkt("Paketierung", "Konfigurationsdateien/Policies vorbereitet", true),
                punkt("Security", "Quelle/Signatur/Hash des Installationspakets geprüft", true),
                punkt("Deployment", "Deployment-Methode festgelegt"),
                punkt("Deployment", "Zielgeräte/-systeme ausgewählt"),
                punkt("Deployment", "Wartungsfenster/Termin geplant", true),
                punkt("Deployment", "Backup/Snapshot vor Änderung geprüft", true),
                punkt("Deployment", "Installation erfolgreich"),
                punkt("Test", "Dienst/Anwendung startet fehlerfrei"),
                punkt("Test", "Kernfunktionen getestet"),
                punkt("Test", "Berechtigungen und Zugriff geprüft"),
                punkt("Monitoring", "Logs/Monitoring nach Deployment geprüft", true),
                punkt("Rollback", "Rollback-/Deinstallationsweg dokumentiert"),
                punkt("Abnahme", "Pilot-/Fachtest erfolgreich", true),
                punkt("Abnahme", "Deployment dokumentiert und abgeschlossen")
        ));

        vorlagen.add(vorlage("VMware",
                punkt("Vorbereitung", "IPD erstellen und Downtime mit Kunde abstimmen"),
                punkt("Planung", "Ziel-vCenter/Datacenter/Cluster/Host festgelegt"),
                punkt("Planung", "VM-Name nach Namenskonvention festgelegt"),
                punkt("Planung", "Gastbetriebssystem und Version festgelegt"),
                punkt("Ressourcen", "vCPU-Anzahl festgelegt"),
                punkt("Ressourcen", "RAM-Größe festgelegt"),
                punkt("Storage", "Datastore ausgewählt"),
                punkt("Storage", "Virtuelle Disk(s) und Größe festgelegt"),
                punkt("Storage", "Provisioning-Art festgelegt", true),
                punkt("Netzwerk", "Portgruppe/Netzwerk ausgewählt"),
                punkt("Netzwerk", "VLAN/Zielsegment geprüft", true),
                punkt("Firmware", "Firmware/UEFI und Secure Boot geprüft", true),
                punkt("Security", "vTPM/VBS bei Bedarf konfiguriert", true),
                punkt("Installation", "ISO oder Template ausgewählt"),
                punkt("Installation", "Gastbetriebssystem installiert bzw. Patch eingespielt"),
                punkt("Integration", "VMware Tools/Open VM Tools installiert und aktuell"),
                punkt("Netzwerk", "IP, Gateway und DNS im Gast konfiguriert"),
                punkt("Identity", "Domänenbeitritt/Verzeichnisanbindung durchgeführt", true),
                punkt("Updates", "Gast-OS aktualisiert"),
                punkt("Backup", "VM in Backup aufgenommen bzw. aktuelles Backup vor Wartung vorhanden"),
                punkt("Monitoring", "VM/Gast in Monitoring aufgenommen", true),
                punkt("Betrieb", "Snapshot vor Wartung erstellt, nach erfolgreichem Test wieder gelöscht", true),
                punkt("Security", "VM-/vCenter-Berechtigungen geprüft"),
                punkt("Abnahme", "Neustart, Netzwerk und Kernfunktionen getestet"),
                punkt("Abnahme", "VM-Dokumentation vollständig"),
                punkt("Abnahme", "Planned Activity Ticket dokumentiert")
        ));

        vorlagen.add(vorlage("Proxmox",
                punkt("Planung", "Ziel-Node/Cluster festgelegt"),
                punkt("Planung", "VM-ID und Name festgelegt"),
                punkt("Planung", "Gastbetriebssystem/OS-Typ ausgewählt"),
                punkt("Ressourcen", "CPU/Sockets/Cores festgelegt"),
                punkt("Ressourcen", "RAM/Ballooning festgelegt"),
                punkt("Storage", "Storage-Ziel ausgewählt"),
                punkt("Storage", "Disk-Größe und Bus/Controller festgelegt"),
                punkt("Netzwerk", "Bridge ausgewählt"),
                punkt("Netzwerk", "VLAN-Tag/Segment geprüft", true),
                punkt("Firmware", "BIOS/UEFI/Machine Type festgelegt", true),
                punkt("Security", "Secure Boot/TPM bei Bedarf konfiguriert", true),
                punkt("Installation", "ISO oder Template ausgewählt"),
                punkt("Installation", "Gastbetriebssystem installiert"),
                punkt("Integration", "QEMU Guest Agent installiert und aktiviert"),
                punkt("Netzwerk", "IP, Gateway und DNS im Gast konfiguriert"),
                punkt("Security", "Proxmox Firewall/VM Firewall geprüft", true),
                punkt("Updates", "Gast-OS aktualisiert"),
                punkt("Backup", "Backup-Job/Ziel für VM eingerichtet"),
                punkt("Backup", "Erster Backup-Lauf erfolgreich"),
                punkt("Monitoring", "VM/Gast in Monitoring aufgenommen", true),
                punkt("Security", "Rollen/Berechtigungen geprüft"),
                punkt("Abnahme", "Neustart, Netzwerk und Kernfunktionen getestet"),
                punkt("Abnahme", "VM-Dokumentation vollständig")
        ));

        vorlagen.add(vorlage("Hyper-V",
                punkt("Planung", "Ziel-Host/Cluster festgelegt"),
                punkt("Planung", "VM-Name und Speicherort festgelegt"),
                punkt("Planung", "Gastbetriebssystem und Kompatibilität geprüft"),
                punkt("Planung", "VM-Generation gewählt; Gen 2 bevorzugt wenn unterstützt"),
                punkt("Ressourcen", "vCPU-Anzahl festgelegt"),
                punkt("Ressourcen", "RAM/Dynamic Memory festgelegt"),
                punkt("Storage", "VHDX-Speicherort und Größe festgelegt"),
                punkt("Netzwerk", "Virtuellen Switch ausgewählt"),
                punkt("Netzwerk", "VLAN/Netzwerksegment geprüft", true),
                punkt("Security", "Secure Boot geprüft/aktiviert wenn unterstützt", true),
                punkt("Security", "vTPM/Verschlüsselung bei Bedarf konfiguriert", true),
                punkt("Installation", "ISO/VHDX/Deploymentquelle ausgewählt"),
                punkt("Installation", "Gastbetriebssystem installiert"),
                punkt("Integration", "Hyper-V Integration Services aktiv/aktuell"),
                punkt("Netzwerk", "IP, Gateway und DNS im Gast konfiguriert"),
                punkt("Identity", "Domänenbeitritt/Verzeichnisanbindung durchgeführt", true),
                punkt("Updates", "Gast-OS aktualisiert"),
                punkt("Backup", "VM in Backup aufgenommen"),
                punkt("Betrieb", "Checkpoints geprüft und Bereinigungsstrategie festgelegt", true),
                punkt("Monitoring", "VM/Gast in Monitoring aufgenommen", true),
                punkt("Security", "Host-/VM-Berechtigungen geprüft"),
                punkt("Abnahme", "Neustart, Netzwerk und Kernfunktionen getestet"),
                punkt("Abnahme", "VM-Dokumentation vollständig")
        ));

        vorlagen.add(vorlage("Backup & Restore",
                punkt("Planung", "Zu sichernde Systeme/Daten festgelegt"),
                punkt("Planung", "RPO festgelegt"),
                punkt("Planung", "RTO festgelegt"),
                punkt("Planung", "Backup-Methode/Typ festgelegt"),
                punkt("Ziel", "Backup-Ziel und Kapazität geprüft"),
                punkt("Ziel", "Backup-Kopie getrennt vom Produktionsnetz vorhanden"),
                punkt("Security", "Backup-Zugriffe/Berechtigungen eingeschränkt"),
                punkt("Security", "Verschlüsselung der Backups geprüft", true),
                punkt("Job", "Backup-Job eingerichtet"),
                punkt("Job", "Zeitplan und Aufbewahrung konfiguriert"),
                punkt("Job", "Applikationskonsistenz/VSS o. ä. geprüft", true),
                punkt("Job", "Erster vollständiger Lauf erfolgreich"),
                punkt("Monitoring", "Fehlerbenachrichtigung/Monitoring eingerichtet"),
                punkt("Restore", "Restore-Verfahren dokumentiert"),
                punkt("Restore", "Datei-/Objekt-Restore getestet"),
                punkt("Restore", "System-/VM-Restore getestet oder geplant", true),
                punkt("Restore", "Restore-Ergebnis fachlich geprüft"),
                punkt("Notfall", "Offsite/Offline/immutable Kopie geprüft", true),
                punkt("Notfall", "Notfallkontakte und Eskalation dokumentiert", true),
                punkt("Abnahme", "Backup- und Restore-Dokumentation vollständig")
        ));

        checklistTemplateRepository.saveAll(vorlagen);
    }

    // Baut eine einzelne Vorlage aus Namen + einer beliebigen Anzahl an
    // bereits fertig formatierten Punkt-Strings (siehe punkt(...) unten).
    // Die id lasse ich bewusst null - die vergibt MongoDB automatisch
    // beim Speichern, genauso wie es ChecklistTemplateService.createTemplate
    // beim manuellen Anlegen über die Oberfläche auch macht.
    // Das letzte Argument (true) markiert alle zehn hier erzeugten
    // Vorlagen als "standard" - dadurch verweigert
    // ChecklistTemplateService.deleteTemplate das Löschen, siehe
    // Kommentar dort und am standard-Feld in ChecklistTemplate selbst.
    private ChecklistTemplate vorlage(String name, String... punkte) {
        return new ChecklistTemplate(null, name, List.of(punkte), true);
    }

    // Pflicht-Punkt: Phase wird in eckigen Klammern vorangestellt.
    private String punkt(String phase, String text) {
        return punkt(phase, text, false);
    }

    // Überladung für optionale Punkte (in meiner Excel-Tabelle als
    // "Optional" statt "Ja" markiert) - hängt zusätzlich " (optional)"
    // an, damit das beim Abhaken der Checkliste im Frontend sofort
    // erkennbar bleibt.
    private String punkt(String phase, String text, boolean optional) {
        String basis = "[" + phase + "] " + text;
        return optional ? basis + " (optional)" : basis;
    }
}