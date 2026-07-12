package com.examplesaludconecta.repository;

import com.examplesaludconecta.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

    boolean existsByRut(String rut);

    boolean existsByEmail(String email);

    boolean existsByRutAndIdNot(String rut, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);
}
