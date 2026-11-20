package com.sis414.sis_414_parcial.service;

import com.sis414.sis_414_parcial.dto.DepartamentoDto;
import com.sis414.sis_414_parcial.entity.Departamento;
import com.sis414.sis_414_parcial.repository.DepartamentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DepartamentoService {
    private final DepartamentoRepository repository;

    public DepartamentoService(DepartamentoRepository repository) { this.repository = repository; }

    @Transactional(readOnly = true)
    public List<DepartamentoDto> findAll() {
        return repository.findAll().stream()
                .filter(d -> DepartamentosBolivia.valido(d.getDescripcion()))
                .map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public DepartamentoDto findById(Long id) {
        Departamento d = repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Departamento no encontrado: " + id));
        if (!DepartamentosBolivia.valido(d.getDescripcion())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Expedido no válido: " + id);
        }
        return toDto(d);
    }

    public DepartamentoDto toDto(Departamento d) {
        return new DepartamentoDto(d.getId(), d.getCodigo(), d.getDescripcion(), d.getSigla());
    }
}
