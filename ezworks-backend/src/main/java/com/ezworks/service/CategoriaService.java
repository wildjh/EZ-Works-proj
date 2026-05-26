package com.ezworks.service;

import com.ezworks.domain.job.Categoria;
import com.ezworks.dto.job.CategoriaRequest;
import com.ezworks.dto.job.CategoriaResponse;
import com.ezworks.exception.ApiException;
import com.ezworks.repository.CategoriaRepository;
import com.ezworks.util.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listarActivas() {
        return categoriaRepository.findByActivaTrueOrderByNombreAsc().stream()
                .map(Mapper::toCategoria)
                .toList();
    }

    @Transactional
    public CategoriaResponse crear(CategoriaRequest req) {
        if (categoriaRepository.findAll().stream().anyMatch(c -> c.getNombre().equalsIgnoreCase(req.getNombre()))) {
            throw new ApiException(HttpStatus.CONFLICT, "La categoría ya existe");
        }
        Categoria c = categoriaRepository.save(Categoria.builder()
                .nombre(req.getNombre().trim())
                .activa(true)
                .build());
        return Mapper.toCategoria(c);
    }
}
