package com.ankr.topFinances.model.repository;

import com.ankr.topFinances.model.entity.Lancamento;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long> {
    
}