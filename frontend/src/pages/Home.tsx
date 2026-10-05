import { Link } from 'react-router-dom';
import { Button, Card, Col, Row } from 'react-bootstrap';
import { useAuth } from '../auth/AuthContext';
import { useGlpiUrl } from '../hooks/useGlpiUrl';

// Startseite: ohne Login nur ein kurzer Überblick + Login-Button, mit
// Login eine kurze Übersicht über die Arbeitsbereiche als
// Einstiegspunkte (Karten statt nur Navigationslinks, damit man - z.B.
// in der Präsentation - sofort sieht, was die App kann).
export function Home() {
    const { username, loginUrl } = useAuth();
    // Hook muss vor dem frühen return stehen (Rules of Hooks).
    const glpiUrl = useGlpiUrl(Boolean(username));

    if (!username) {
        return (
            <div className="text-center py-5">
                <h1>OpsServiceDoc</h1>
                <p className="lead">
                    Support-Workflow und IPD-Dokumentgenerator für Wartungseinsätze.
                </p>
                <Button href={loginUrl} size="lg" variant="primary">
                    Mit GitHub einloggen
                </Button>
            </div>
        );
    }

    const bereiche = [
        { titel: 'Tickets', text: 'Support-Tickets aus GLPI einsehen und synchronisieren.', link: '/tickets' },
        { titel: 'TaskPlanner', text: 'Arbeitsschritte zu einem Ticket erfassen und abhaken.', link: '/tasks' },
        { titel: 'Checklisten', text: 'Checklisten und Vorlagen für Wartungseinsätze verwalten.', link: '/checklisten' },
        { titel: 'IPD-Generator', text: 'IPD-Dokumente aus Tickets erzeugen und als PDF exportieren.', link: '/ipd' },
    ];

    return (
        <div className="py-4">
            <h1 className="mb-4">Willkommen zurück, {username}</h1>
            <Row xs={1} md={2} className="g-4">
                {bereiche.map((bereich) => (
                    <Col key={bereich.link}>
                        <Card className="h-100">
                            <Card.Body>
                                <Card.Title>{bereich.titel}</Card.Title>
                                <Card.Text>{bereich.text}</Card.Text>
                                {/* Ich nehme hier den React-Router-Link mit den
                                Bootstrap-Button-Klassen statt <Button as={Link}>:
                                die Kombination war für TypeScript nicht sauber
                                typisierbar (vorher stand hier ein 'as never'-Cast,
                                der tsc -b und damit npm run build brechen ließ).
                                Optisch ist es derselbe Button, der Link navigiert
                                weiterhin clientseitig ohne Seiten-Neuladen. */}
                                <Link to={bereich.link} className="btn btn-outline-primary">
                                    Öffnen
                                </Link>
                            </Card.Body>
                        </Card>
                    </Col>
                ))}
                {glpiUrl && (
                    <Col>
                        <Card className="h-100">
                            <Card.Body>
                                <Card.Title>GLPI</Card.Title>
                                <Card.Text>Das Ticketsystem GLPI direkt öffnen (neuer Tab).</Card.Text>
                                <a
                                    href={glpiUrl}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className="btn btn-outline-primary"
                                >
                                    GLPI öffnen ↗
                                </a>
                            </Card.Body>
                        </Card>
                    </Col>
                )}
            </Row>
        </div>
    );
}