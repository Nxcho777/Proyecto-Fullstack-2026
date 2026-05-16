package com.examplesaludconecta.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.examplesaludconecta.model.Medico;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, Long> {

}