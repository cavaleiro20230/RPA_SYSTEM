package com.rpa.fiscal.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "guias_recolhimento")
@Data
public class GuiaRecolhimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "rpa_id", nullable = false)
    private Rpa rpa;
    
    @Column(name = "tipo_guia")
    @Enumerated(EnumType.STRING)
    private TipoGuia tipoGuia;
    
    @Column(name = "codigo_receita")
    private String codigoReceita;
    
    @Column(precision = 15, scale = 2)
    private BigDecimal valor;
    
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
    
    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;
    
    @Column(name = "numero_documento")
    private String numeroDocumento;
    
    @Enumerated(EnumType.STRING)
    private StatusGuia status = StatusGuia.PENDENTE;
    
    private String observacoes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    public enum TipoGuia {
        DARF, GPS, ISS, OUTROS
    }
    
    public enum StatusGuia {
        PENDENTE, PAGO, VENCIDO, CANCELADO
    }
}
