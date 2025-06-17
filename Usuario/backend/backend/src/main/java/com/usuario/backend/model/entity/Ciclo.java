package com.usuario.backend.model.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ciclos")
public class Ciclo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "numero", nullable = false)
    private Integer numero; // 1, 2, 3, 4, 5, 6
    
    @Column(name = "nombre", nullable = false)
    private String nombre; // "Primer Ciclo", "Segundo Ciclo", etc.

    // Constructores
    public Ciclo() {}

    public Ciclo(Integer numero, String nombre) {
        this.numero = numero;
        this.nombre = nombre;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return "Ciclo{" +
                "id=" + id +
                ", numero=" + numero +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}