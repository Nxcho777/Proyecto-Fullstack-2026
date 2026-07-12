package com.examplesaludconecta.repository;

import com.examplesaludconecta.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    boolean existsByRut(String rut);

    boolean existsByCorreo(String correo);

    boolean existsByRutAndIdNot(String rut, Long id);

    boolean existsByCorreoAndIdNot(String correo, Long id);
}
