package com.usuario.backend.model.dto.response;

import com.usuario.backend.model.entity.core.Usuario.RolUsuario;
import java.time.LocalDateTime;

public class UsuarioResponse {
    
    private Integer id;
    private String nombres;
    private String apellido;
    private String email;
    private RolUsuario rol;
    private Integer carreraId;
    private String carreraNombre;
    private Integer departamentoId;
    private String departamentoNombre;
    private String profileImageUrl;
    private String telefono;
    private LocalDateTime fechaRegistro;
    private Boolean activo;

    // Constructores
    public UsuarioResponse() {}

    public UsuarioResponse(Integer id, String nombres, String apellido, String email, RolUsuario rol) {
        this.id = id;
        this.nombres = nombres;
        this.apellido = apellido;
        this.email = email;
        this.rol = rol;
    }

    // Métodos de utilidad
    public String getNombreCompleto() {
        return nombres + " " + apellido;
    }

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public RolUsuario getRol() { return rol; }
    public void setRol(RolUsuario rol) { this.rol = rol; }

    public Integer getCarreraId() { return carreraId; }
    public void setCarreraId(Integer carreraId) { this.carreraId = carreraId; }

    public String getCarreraNombre() { return carreraNombre; }
    public void setCarreraNombre(String carreraNombre) { this.carreraNombre = carreraNombre; }

    public Integer getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(Integer departamentoId) { this.departamentoId = departamentoId; }

    public String getDepartamentoNombre() { return departamentoNombre; }
    public void setDepartamentoNombre(String departamentoNombre) { this.departamentoNombre = departamentoNombre; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}