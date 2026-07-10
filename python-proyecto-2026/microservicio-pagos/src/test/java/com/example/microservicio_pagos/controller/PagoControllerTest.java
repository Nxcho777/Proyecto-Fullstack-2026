package com.example.microservicio_pagos.controller;

import com.example.microservicio_pagos.assemblers.PagoAssembler;
import com.example.microservicio_pagos.model.Pago;
import com.example.microservicio_pagos.service.PagoService;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PagoController.class)
@AutoConfigureMockMvc(addFilters = false)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PagoService pagoService;

    @MockitoBean
    private PagoAssembler pagoAssembler;

    @Test
    @DisplayName("Debe listar pagos con enlaces HATEOAS")
    void shouldListarPagosConHateoas() throws Exception {
        Pago pago = crearPago(1L);
        EntityModel<Pago> modelo = crearModelo(pago);

        when(pagoService.listarPagos()).thenReturn(List.of(pago));
        when(pagoAssembler.toModel(pago)).thenReturn(modelo);

        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/pagos")))
                .andExpect(jsonPath("$._embedded").exists());

        verify(pagoService, times(1)).listarPagos();
        verify(pagoAssembler, times(1)).toModel(pago);
    }

    @Test
    @DisplayName("Debe obtener pago por ID")
    void shouldObtenerPagoPorId() throws Exception {
        Long id = 1L;
        Pago pago = crearPago(id);
        EntityModel<Pago> modelo = crearModelo(pago);

        when(pagoService.obtenerPagoPorId(id)).thenReturn(Optional.of(pago));
        when(pagoAssembler.toModel(pago)).thenReturn(modelo);

        mockMvc.perform(get("/api/pagos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$._links.self.href").exists());

        verify(pagoService, times(1)).obtenerPagoPorId(id);
        verify(pagoAssembler, times(1)).toModel(pago);
    }

    @Test
    @DisplayName("Debe retornar 404 si no existe")
    void shouldReturnNotFoundWhenPagoNoExiste() throws Exception {
        Long id = 99L;

        when(pagoService.obtenerPagoPorId(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/pagos/{id}", id))
                .andExpect(status().isNotFound());

        verify(pagoService, times(1)).obtenerPagoPorId(id);
        verify(pagoAssembler, never()).toModel(any(Pago.class));
    }

    @Test
    @DisplayName("Debe crear pago")
    void shouldCrearPago() throws Exception {
        Pago pago = crearPago(1L);
        EntityModel<Pago> modelo = crearModelo(pago);
        String json = """
{
  "pacienteId": 1,
  "monto": 25000.0,
  "metodoPago": "TARJETA",
  "estado": "PAGADO",
  "fechaPago": "2026-07-10"
}
                """;

        when(pagoService.guardarPago(any(Pago.class))).thenReturn(pago);
        when(pagoAssembler.toModel(pago)).thenReturn(modelo);

        mockMvc.perform(post("/api/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        verify(pagoService, times(1)).guardarPago(any(Pago.class));
        verify(pagoAssembler, times(1)).toModel(pago);
    }

    @Test
    @DisplayName("Debe eliminar pago existente")
    void shouldEliminarPagoExistente() throws Exception {
        Long id = 1L;
        when(pagoService.eliminarPago(id)).thenReturn(true);

        mockMvc.perform(delete("/api/pagos/{id}", id))
                .andExpect(status().isNoContent());

        verify(pagoService, times(1)).eliminarPago(id);
    }

    @Test
    @DisplayName("Debe retornar 404 al eliminar pago inexistente")
    void shouldReturnNotFoundWhenEliminarPagoNoExiste() throws Exception {
        Long id = 99L;
        when(pagoService.eliminarPago(id)).thenReturn(false);

        mockMvc.perform(delete("/api/pagos/{id}", id))
                .andExpect(status().isNotFound());

        verify(pagoService, times(1)).eliminarPago(id);
    }

    private EntityModel<Pago> crearModelo(Pago pago) {
        return EntityModel.of(
                pago,
                linkTo(methodOn(PagoController.class).obtenerPagoPorId(pago.getId())).withSelfRel()
        );
    }

    private Pago crearPago(Long id) {
        Pago pago = new Pago();
        pago.setId(id);
        pago.setPacienteId(1L);
        pago.setMonto(25000.0);
        pago.setMetodoPago("TARJETA");
        pago.setEstado("PAGADO");
        pago.setFechaPago(String.valueOf("2026-07-10"));
        return pago;
    }
}
