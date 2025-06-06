package com.rpa.fiscal.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tomadores")
@Data
public class Tomador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "razao_social", nullable = false)
    private String razaoSocial;
    
    @Column(name = "nome_fantasia")
    private String nomeFantasia;
    
    @Column(name = "cnpj_cpf", nullable = false, unique = true)
    private String cnpjCpf;
    
    @Column(name = "inscricao_estadual")
    private String inscricaoEstadual;
    
    @Column(name = "inscricao_municipal")
    private String inscricaoMunicipal;
    
    private String endereco;
    private String cep;
    private String cidade;
    private String estado;
    private String telefone;
    private String email;
    
    @Column(name = "regime_tributario")
    @Enumerated(EnumType.STRING)
    private RegimeTributario regimeTributario = RegimeTributario.LUCRO_PRESUMIDO;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.ATIVO;
    
    @OneToMany(mappedBy = "tomador", cascade = CascadeType.ALL)
    private List<Rpa> rpas;
    
    public enum RegimeTributario {
        SIMPLES_NACIONAL, LUCRO_PRESUMIDO, LUCRO_REAL, LUCRO_ARBITRADO
    }
    
    public enum Status {
        ATIVO, INATIVO, SUSPENSO
    }
}
