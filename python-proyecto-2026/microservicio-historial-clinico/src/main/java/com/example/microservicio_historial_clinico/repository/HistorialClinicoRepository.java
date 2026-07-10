package com.example.microservicio_historial_clinico.repository;

import com.example.microservicio_historial_clinico.model.HistorialClinico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistorialClinicoRepository extends JpaRepository<HistorialClinico, Long> {
}
