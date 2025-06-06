package com.rpa.fiscal.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rpas")
@Data
public class Rpa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String numero;
    
    @ManyToOne
    @JoinColumn(name = "prestador_id", nullable = false)
    private Prestador prestador;
    
    @ManyToOne
    @JoinColumn(name = "tomador_id", nullable = false)
    private Tomador tomador;
    
    @ManyToOne
    @JoinColumn(name = "servico_id", nullable = false)
    private Servico servico;
    
    @Column(name = "data_emissao", nullable = false)
    private LocalDate dataEmissao;
    
    @Column(name = "data_vencimento")
    private LocalDate dataVencimento;
    
    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;
    
    @Column(name = "descricao_servico", columnDefinition = "NVARCHAR(MAX)")
    private String descricaoServico;
    
    @Column(name = "valor_bruto", precision = 15, scale = 2)
    private BigDecimal valorBruto;
    
    // Cálculos INSS
    @Column(name = "base_calculo_inss", precision = 15, scale = 2)
    private BigDecimal baseCalculoInss;
    
    @Column(name = "aliquota_inss", precision = 5, scale = 2)
    private BigDecimal aliquotaInss;
    
    @Column(name = "valor_inss", precision = 15, scale = 2)
    private BigDecimal valorInss;
    
    // Cálculos IRRF
    @Column(name = "base_calculo_irrf", precision = 15, scale = 2)
    private BigDecimal baseCalculoIrrf;
    
    @Column(name = "aliquota_irrf", precision = 5, scale = 2)
    private BigDecimal aliquotaIrrf;
    
    @Column(name = "valor_irrf", precision = 15, scale = 2)
    private BigDecimal valorIrrf;
    
    // Cálculos ISS
    @Column(name = "base_calculo_iss", precision = 15, scale = 2)
    private BigDecimal baseCalculoIss;
    
    @Column(name = "aliquota_iss", precision = 5, scale = 2)
    private BigDecimal aliquotaIss;
    
    @Column(name = "valor_iss", precision = 15, scale = 2)
    private BigDecimal valorIss;
    
    // Outros descontos
    @Column(name = "outros_descontos", precision = 15, scale = 2)
    private BigDecimal outrosDescontos = BigDecimal.ZERO;
    
    @Column(name = "descricao_outros_descontos")
    private String descricaoOutrosDescontos;
    
    // Valores finais
    @Column(name = "total_descontos", precision = 15, scale = 2)
    private BigDecimal totalDescontos;
    
    @Column(name = "valor_liquido", precision = 15, scale = 2)
    private BigDecimal valorLiquido;
    
    @Enumerated(EnumType.STRING)
    private StatusRpa status = StatusRpa.EMITIDO;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String observacoes;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "created_by")
    private String createdBy;
    
    @OneToMany(mappedBy = "rpa", cascade = CascadeType.ALL)
    private List<GuiaRecolhimento> guias;
    
    public enum StatusRpa {
        EMITIDO, PAGO, CANCELADO, VENCIDO
    }
}
