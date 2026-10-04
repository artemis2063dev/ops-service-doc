import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'

import './index.css'

// Bootstrap-CSS global einbinden, nach index.css aber vor App.css -
// so kann ich einzelne Bootstrap-Stile in App.css gezielt überschreiben.
import 'bootstrap/dist/css/bootstrap.min.css'

import { AuthProvider } from './auth/AuthContext'
import App from './App.tsx'

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <BrowserRouter>
            <AuthProvider>
                <App />
            </AuthProvider>
        </BrowserRouter>
    </StrictMode>,
)