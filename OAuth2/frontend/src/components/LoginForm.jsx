import React, { useState } from 'react';
import './LoginForm.css'; // Importa el archivo de estilos

function LoginForm() {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');

    const handleInputChange = (event) => {
        const { name, value } = event.target;
        if (name === 'username') {
            setUsername(value);
        } else if (name === 'password') {
            setPassword(value);
        }
    };

    const handleSubmit = (event) => {
        event.preventDefault();
        console.log('Enviando datos de formulario:', { username, password });
        // Aquí iría la lógica para enviar los datos al backend
    };

    return (
        <div className="login-form-container">
            <h2>Iniciar Sesión</h2>
            <form onSubmit={handleSubmit} className="login-form">
                <div className="form-group">
                    <label htmlFor="username">Usuario:</label>
                    <input
                        type="text"
                        id="username"
                        name="username"
                        value={username}
                        onChange={handleInputChange}
                        className="form-control"
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Contraseña:</label>
                    <input
                        type="password"
                        id="password"
                        name="password"
                        value={password}
                        onChange={handleInputChange}
                        className="form-control"
                    />
                </div>
                <button type="submit" className="btn btn-primary">Iniciar Sesión</button>
            </form>
        </div>
    );
}

export default LoginForm;