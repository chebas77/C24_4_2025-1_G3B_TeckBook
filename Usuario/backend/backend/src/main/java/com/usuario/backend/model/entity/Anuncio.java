package com.usuario.backend.model.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "anuncios")
public class Anuncio {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 255)
    private String titulo;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;
    
    @Column(nullable = false)
    private String tipo;
    
    @Column(name = "archivo_url", length = 500)
    private String archivoUrl;
    
    @Column(name = "archivo_nombre", length = 255)
    private String archivoNombre;
    
    @Column(name = "archivo_tipo", length = 100)
    private String archivoTipo;
    
    @Column(name = "archivo_tamaño")
    private Long archivoTamaño;
    
    @Column(length = 100)
    private String categoria;
    
    @Column(columnDefinition = "JSON")
    private String etiquetas; // Guardamos como JSON string
    
    @Column(name = "permite_comentarios")
    private Boolean permiteComentarios = true;
    
    @Column(name = "total_likes")
    private Integer totalLikes = 0;
    
    @Column(name = "total_comentarios")
    private Integer totalComentarios = 0;
    
    @Column(name = "aula_id", nullable = false)
    private Integer aulaId;
    
    @Column(name = "autor_id", nullable = false)
    private Integer autorId;
    
    @Column(name = "fecha_publicacion")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaPublicacion;
    
    @Column(name = "fecha_edicion")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime fechaEdicion;
    
    private Boolean activo = true;
    
    private Boolean fijado = false;
    
    @Column(name = "es_general", nullable = false)
    private Boolean esGeneral = false;
    
    // Constructores
    public Anuncio() {}
    
    public Anuncio(String titulo, String contenido, Integer aulaId, Integer autorId) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.aulaId = aulaId;
        this.autorId = autorId;
        this.fechaPublicacion = LocalDateTime.now();
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    
    public String getArchivoUrl() { return archivoUrl; }
    public void setArchivoUrl(String archivoUrl) { this.archivoUrl = archivoUrl; }
    
    public String getArchivoNombre() { return archivoNombre; }
    public void setArchivoNombre(String archivoNombre) { this.archivoNombre = archivoNombre; }
    
    public String getArchivoTipo() { return archivoTipo; }
    public void setArchivoTipo(String archivoTipo) { this.archivoTipo = archivoTipo; }
    
    public Long getArchivoTamaño() { return archivoTamaño; }
    public void setArchivoTamaño(Long archivoTamaño) { this.archivoTamaño = archivoTamaño; }
    
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    
    public String getEtiquetas() { return etiquetas; }
    public void setEtiquetas(String etiquetas) { this.etiquetas = etiquetas; }
    
    public Boolean getPermiteComentarios() { return permiteComentarios; }
    public void setPermiteComentarios(Boolean permiteComentarios) { this.permiteComentarios = permiteComentarios; }
    
    public Integer getTotalLikes() { return totalLikes; }
    public void setTotalLikes(Integer totalLikes) { this.totalLikes = totalLikes; }
    
    public Integer getTotalComentarios() { return totalComentarios; }
    public void setTotalComentarios(Integer totalComentarios) { this.totalComentarios = totalComentarios; }
    
    public Integer getAulaId() { return aulaId; }
    public void setAulaId(Integer aulaId) { this.aulaId = aulaId; }
    
    public Integer getAutorId() { return autorId; }
    public void setAutorId(Integer autorId) { this.autorId = autorId; }
    
    public LocalDateTime getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDateTime fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }
    
    public LocalDateTime getFechaEdicion() { return fechaEdicion; }
    public void setFechaEdicion(LocalDateTime fechaEdicion) { this.fechaEdicion = fechaEdicion; }
    
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
    
    public Boolean getFijado() { return fijado; }
    public void setFijado(Boolean fijado) { this.fijado = fijado; }
    
    public Boolean getEsGeneral() { return esGeneral; }
    public void setEsGeneral(Boolean esGeneral) { this.esGeneral = esGeneral; }
}