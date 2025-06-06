package com.rpa.fiscal.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "prestadores")
@Data
public class Prestador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(nullable = false, unique = true)
    private String cpf;
    
    private String rg;
    private String endereco;
    private String cep;
    private String cidade;
    private String estado;
    private String telefone;
    private String email;
    private String banco;
    private String agencia;
    private String conta;
    private String pix;
    
    @Column(name = "is_mei")
    private Boolean isMei = false;
    
    @Column(name = "cnpj_mei")
    private String cnpjMei;
    
    @Column(name = "atividade_principal")
    private String atividadePrincipal;
    
    @Column(name = "aliquota_iss", precision = 5, scale = 2)
    private Double aliquotaIss = 5.0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVO;
    
    @OneToMany(mappedBy = "prestador", cascade = CascadeType.ALL)
    private List<Rpa> rpas;
    
    public enum Status {
        ATIVO, INATIVO, SUSPENSO
    }
}
