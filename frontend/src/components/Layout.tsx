import { Link, NavLink, Outlet } from 'react-router-dom';
import { Button, Container, Nav, Navbar } from 'react-bootstrap';
import { useAuth } from '../auth/AuthContext';
import { useGlpiUrl } from '../hooks/useGlpiUrl';

// Gemeinsames Grundgerüst für alle Seiten: Navigation oben, darunter
// der jeweilige Seiteninhalt über <Outlet /> (React-Router rendert hier
// die aktuell passende Route hinein, siehe App.tsx).
export function Layout() {
    const { username, loading, logout } = useAuth();
    const glpiUrl = useGlpiUrl(Boolean(username));

    return (
        <>
            <Navbar expand="md" className="app-navbar" variant="dark">
                <Container fluid>
                    <Navbar.Brand as={Link} to="/">
                        OpsServiceDoc
                    </Navbar.Brand>
                    <Navbar.Toggle aria-controls="main-nav" />
                    <Navbar.Collapse id="main-nav">
                        {/* Die Navigationspunkte zeige ich nur an, wenn jemand
                  eingeloggt ist - ohne Login würden die Links ohnehin nur
                  in einen 401 laufen (siehe SecurityConfig). */}
                        {username && (
                            <Nav className="me-auto">
                                <Nav.Link as={NavLink} to="/tickets">Tickets</Nav.Link>
                                <Nav.Link as={NavLink} to="/tasks">TaskPlanner</Nav.Link>
                                <Nav.Link as={NavLink} to="/checklisten">Checklisten</Nav.Link>
                                <Nav.Link as={NavLink} to="/ipd">IPD-Generator</Nav.Link>
                                {/* GLPI ist ein externes System: öffnet in neuem Tab. */}
                                {glpiUrl && (
                                    <Nav.Link href={glpiUrl} target="_blank" rel="noopener noreferrer">
                                        GLPI ↗
                                    </Nav.Link>
                                )}
                            </Nav>
                        )}
                        <Nav>
                            {/* Der Login-Button steht nur noch in der Mitte der
                            Startseite (Home.tsx), nicht mehr hier in der Navbar. */}
                            {!loading && username && (
                                <div className="d-flex align-items-center gap-3">
                                    <span className="text-light">Angemeldet als {username}</span>
                                    <Button variant="outline-light" size="sm" onClick={logout}>
                                        Logout
                                    </Button>
                                </div>
                            )}
                        </Nav>
                    </Navbar.Collapse>
                </Container>
            </Navbar>

            <Container fluid className="app-content">
                <Outlet />
            </Container>
        </>
    );
}