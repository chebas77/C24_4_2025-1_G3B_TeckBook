import React from 'react';
import './Footer.css';

function Footer() {
  const currentYear = new Date().getFullYear();

  return (
    <footer className="app-footer">
      <div className="container-fluid">
        <div className="row">
          <div className="col-12 text-center py-4">
            <p className="mb-0">
              © {currentYear} <strong>TecBook</strong> - Tecsup. 
              Todos los derechos reservados.
            </p>
          </div>
        </div>
      </div>
    </footer>
  );
}

export default Footer;