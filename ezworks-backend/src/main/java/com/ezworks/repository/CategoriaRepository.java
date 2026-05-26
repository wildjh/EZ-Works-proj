package com.ezworks.repository;

import com.ezworks.domain.job.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Short> {

    List<Categoria> findByActivaTrueOrderByNombreAsc();
}
