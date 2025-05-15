import React from 'react';
import './LoginButton.css'; // Importa el archivo de estilos

function LoginButton() {
    const handleLoginWithGoogle = () => {
        window.location.href = '/oauth2/authorization/google'; // Redirige al endpoint de Spring Security
    };

    return (
        <button onClick={handleLoginWithGoogle} className="google-login-button">
            <img src="https://upload.wikimedia.org/wikipedia/commons/5/53/Google_%22G%22_Logo.svg" alt="Google logo" className="google-icon" />
            Iniciar sesión con Google
        </button>
    );
}

export default LoginButton;