import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import { fetchCurrentUser } from '../api/api';

// URL, unter der mein Backend läuft. Für den GitHub-Login und das
// Logout brauche ich eine ECHTE Browser-Weiterleitung (kein fetch),
// weil GitHub den Nutzer über mehrere Seiten hinweg umleitet - das lässt
// sich nicht per JavaScript-Request nachbilden, nur per Navigation.
// Im Dev-Betrieb läuft das Backend fest auf Port 8080.
const BACKEND_URL = 'http://localhost:8080';

interface AuthContextValue {
    username: string | null;
    loading: boolean;
    loginUrl: string;
    logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
    const [username, setUsername] = useState<string | null>(null);
    const [loading, setLoading] = useState(true);

    // Beim ersten Laden der App prüfe ich einmal, ob schon eine gültige
    // Session besteht (z.B. weil der Nutzer die Seite neu lädt, nachdem
    // er sich vorher schon eingeloggt hat).
    useEffect(() => {
        fetchCurrentUser()
            .then(setUsername)
            .catch(() => setUsername(null))
            .finally(() => setLoading(false));
    }, []);

    // Logout ist bei Spring Security standardmäßig ein POST auf /logout.
    // Ich baue mir dafür dynamisch ein unsichtbares Formular statt eines
    // fetch-Aufrufs, weil der Browser nach dem Logout per Redirect auf
    // logoutSuccessUrl (siehe SecurityConfig) weitergeleitet wird - das
    // funktioniert nur mit einer echten Formular-Navigation, nicht mit fetch.
    function logout() {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = `${BACKEND_URL}/logout`;
        document.body.appendChild(form);
        form.submit();
    }

    const value: AuthContextValue = {
        username,
        loading,
        loginUrl: `${BACKEND_URL}/oauth2/authorization/github`,
        logout,
    };

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// Eigener Hook statt useContext(AuthContext) überall, damit ich an
// zentraler Stelle prüfen kann, ob der Hook auch wirklich innerhalb
// eines AuthProvider verwendet wird.
export function useAuth(): AuthContextValue {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth muss innerhalb eines AuthProvider verwendet werden');
    }
    return context;
}