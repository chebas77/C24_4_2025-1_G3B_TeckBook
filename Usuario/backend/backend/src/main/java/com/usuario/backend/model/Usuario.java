package com.usuario.backend.model;

    import jakarta.persistence.*;

    @Entity
    @Table(name = "usuarios")
    public class Usuario {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        
        private String nombre;
        private String apellidos;
        private String codigo;
        
        @Column(name = "correo_institucional")
        private String correoInstitucional;
        
        private String password;
        private String ciclo;
        private String rol;
        
        @Column(name = "departamento_id")
        private Long departamentoId;
        
        @Column(name = "carrera_id")
        private Long carreraId;
        
        @Column(name = "seccion_id")
        private Long seccionId;

        // Getters y Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getApellidos() {
            return apellidos;
        }

        public void setApellidos(String apellidos) {
            this.apellidos = apellidos;
        }

        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getCorreoInstitucional() {
            return correoInstitucional;
        }

        public void setCorreoInstitucional(String correoInstitucional) {
            this.correoInstitucional = correoInstitucional;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getCiclo() {
            return ciclo;
        }

        public void setCiclo(String ciclo) {
            this.ciclo = ciclo;
        }

        public String getRol() {
            return rol;
        }

        public void setRol(String rol) {
            this.rol = rol;
        }

        public Long getDepartamentoId() {
            return departamentoId;
        }

        public void setDepartamentoId(Long departamentoId) {
            this.departamentoId = departamentoId;
        }

        public Long getCarreraId() {
            return carreraId;
        }

        public void setCarreraId(Long carreraId) {
            this.carreraId = carreraId;
        }

        public Long getSeccionId() {
            return seccionId;
        }

        public void setSeccionId(Long seccionId) {
            this.seccionId = seccionId;
        }
    }