package com.usuario.backend.repository;

import com.usuario.backend.model.entity.Anuncio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnuncioRepository extends JpaRepository<Anuncio, Long> {
    List<Anuncio> findByAulaIdAndActivoTrueOrderByFechaPublicacionDesc(Long aulaId);
}
