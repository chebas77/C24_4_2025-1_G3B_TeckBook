package com.usuario.backend.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "secciones")
public class Seccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nombre", nullable = false)
    private String nombre;
    
    @Column(name = "codigo", unique = true)
    private String codigo;
    
    @Column(name = "ciclo", nullable = false)
    private Integer ciclo;
    
    @Column(name = "carrera_id")
    private Long carreraId;

    // Constructores
    public Seccion() {}

    public Seccion(String nombre, String codigo, Integer ciclo, Long carreraId) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.ciclo = ciclo;
        this.carreraId = carreraId;
    }

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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Integer getCiclo() {
        return ciclo;
    }

    public void setCiclo(Integer ciclo) {
        this.ciclo = ciclo;
    }

    public Long getCarreraId() {
        return carreraId;
    }

    public void setCarreraId(Long carreraId) {
        this.carreraId = carreraId;
    }

    @Override
    public String toString() {
        return "Seccion{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", codigo='" + codigo + '\'' +
                ", ciclo=" + ciclo +
                ", carreraId=" + carreraId +
                '}';
    }
}