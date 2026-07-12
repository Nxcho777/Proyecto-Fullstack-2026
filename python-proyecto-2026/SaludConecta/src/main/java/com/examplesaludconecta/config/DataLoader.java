package com.examplesaludconecta.config;

import com.examplesaludconecta.model.Medico;
import com.examplesaludconecta.model.Paciente;
import com.examplesaludconecta.model.Tratamiento;
import com.examplesaludconecta.repository.MedicoRepository;
import com.examplesaludconecta.repository.PacienteRepository;
import com.examplesaludconecta.repository.TratamientoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Profile("datafaker")
public class DataLoader implements CommandLineRunner {

    private static final Logger log =
            LoggerFactory.getLogger(DataLoader.class);

    private final MedicoRepository medicoRepository;
    private final PacienteRepository pacienteRepository;
    private final TratamientoRepository tratamientoRepository;

    public DataLoader(
            MedicoRepository medicoRepository,
            PacienteRepository pacienteRepository,
            TratamientoRepository tratamientoRepository
    ) {
        this.medicoRepository = medicoRepository;
        this.pacienteRepository = pacienteRepository;
        this.tratamientoRepository = tratamientoRepository;
    }

    @Override
    public void run(String... args) {
        Faker faker = new Faker();
        Random random = new Random();

        cargarMedicos(faker);
        cargarPacientes(faker);
        cargarTratamientos(faker, random);

        log.info("Datos falsos cargados correctamente con DataFaker.");
    }

    private void cargarMedicos(Faker faker) {
        if (medicoRepository.count() > 0) {
            return;
        }

        String[] especialidades = {
                "Medicina General",
                "Pediatría",
                "Cardiología",
                "Dermatología",
                "Neurología",
                "Traumatología"
        };

        for (int i = 0; i < 10; i++) {
            Medico medico = new Medico();

            medico.setNombre(faker.name().firstName());
            medico.setApellido(faker.name().lastName());
            medico.setRut(generarRutValido(i));
            medico.setEspecialidad(especialidades[i % especialidades.length]);
            medico.setEmail(faker.internet().emailAddress());

            medicoRepository.save(medico);
        }
    }

    private void cargarPacientes(Faker faker) {
        if (pacienteRepository.count() > 0) {
            return;
        }

        for (int i = 0; i < 20; i++) {
            Paciente paciente = new Paciente();

            paciente.setNombre(faker.name().firstName());
            paciente.setApellido(faker.name().lastName());
            paciente.setRut(generarRutValido(i + 20));
            paciente.setCorreo(faker.internet().emailAddress());
            paciente.setTelefono("+569" + faker.number().digits(8));

            pacienteRepository.save(paciente);
        }
    }

    private void cargarTratamientos(Faker faker, Random random) {
        if (tratamientoRepository.count() > 0) {
            return;
        }

        String[] medicamentos = {
                "Paracetamol",
                "Ibuprofeno",
                "Amoxicilina",
                "Loratadina",
                "Omeprazol",
                "Metformina"
        };

        String[] dosis = {
                "1 comprimido cada 8 horas",
                "1 comprimido cada 12 horas",
                "5 ml cada 8 horas",
                "1 cápsula diaria",
                "2 comprimidos al día"
        };


        String[] diagnosticos = {
        "Gripe",
        "Bronquitis",
        "Hipertensión",
        "Diabetes",
        "Asma",
        "Migraña",
        "Infección respiratoria"
};

      for (int i = 0; i < 15; i++) {
            Tratamiento tratamiento = new Tratamiento();

            tratamiento.setDiagnostico(diagnosticos[random.nextInt(diagnosticos.length)]);
            tratamiento.setMedicamento(medicamentos[random.nextInt(medicamentos.length)]);
            tratamiento.setDosis(dosis[random.nextInt(dosis.length)]);
            tratamiento.setDuracionDias(faker.number().numberBetween(1, 30));

            tratamientoRepository.save(tratamiento);
        }
    }

    private String generarRutValido(int numero) {
        return "12.345." + String.format("%03d", numero) + "-K";
    }
}
