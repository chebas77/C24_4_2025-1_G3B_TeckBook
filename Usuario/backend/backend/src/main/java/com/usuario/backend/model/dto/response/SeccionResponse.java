package com.usuario.backend.model.dto.response;


public class SeccionResponse {
    
    private Integer id;
    private String letra;
    private Integer ciclo;
    private Integer carreraId;
    private String carreraNombre;
    private String carreraCodigo;
    private String periodoAcademico;
    private Integer capacidadMaxima;
    private Integer cantidadEstudiantes;
    private String codigoCompleto;

    // Constructores
    public SeccionResponse() {}

    // Getters y Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getLetra() { return letra; }
    public void setLetra(String letra) { this.letra = letra; }

    public Integer getCiclo() { return ciclo; }
    public void setCiclo(Integer ciclo) { this.ciclo = ciclo; }

    public Integer getCarreraId() { return carreraId; }
    public void setCarreraId(Integer carreraId) { this.carreraId = carreraId; }

    public String getCarreraNombre() { return carreraNombre; }
    public void setCarreraNombre(String carreraNombre) { this.carreraNombre = carreraNombre; }

    public String getCarreraCodigo() { return carreraCodigo; }
    public void setCarreraCodigo(String carreraCodigo) { this.carreraCodigo = carreraCodigo; }

    public String getPeriodoAcademico() { return periodoAcademico; }
    public void setPeriodoAcademico(String periodoAcademico) { this.periodoAcademico = periodoAcademico; }

    public Integer getCapacidadMaxima() { return capacidadMaxima; }
    public void setCapacidadMaxima(Integer capacidadMaxima) { this.capacidadMaxima = capacidadMaxima; }

    public Integer getCantidadEstudiantes() { return cantidadEstudiantes; }
    public void setCantidadEstudiantes(Integer cantidadEstudiantes) { this.cantidadEstudiantes = cantidadEstudiantes; }

    public String getCodigoCompleto() { return codigoCompleto; }
    public void setCodigoCompleto(String codigoCompleto) { this.codigoCompleto = codigoCompleto; }
}
