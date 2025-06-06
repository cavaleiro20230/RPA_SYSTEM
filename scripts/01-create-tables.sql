-- Sistema de RPA (Recibo de Pagamento Autônomo)
CREATE DATABASE RPA_FISCAL_SYSTEM;
USE RPA_FISCAL_SYSTEM;

-- Tabela de Prestadores de Serviço (Autônomos)
CREATE TABLE prestadores (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome NVARCHAR(255) NOT NULL,
    cpf NVARCHAR(14) NOT NULL UNIQUE,
    rg NVARCHAR(20),
    endereco NVARCHAR(500),
    cep NVARCHAR(10),
    cidade NVARCHAR(100),
    estado NVARCHAR(2),
    telefone NVARCHAR(20),
    email NVARCHAR(255),
    banco NVARCHAR(100),
    agencia NVARCHAR(10),
    conta NVARCHAR(20),
    pix NVARCHAR(255),
    is_mei BIT DEFAULT 0,
    cnpj_mei NVARCHAR(18),
    atividade_principal NVARCHAR(255),
    aliquota_iss DECIMAL(5,2) DEFAULT 5.00,
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    status NVARCHAR(20) DEFAULT 'ATIVO'
);

-- Tabela de Tomadores de Serviço (Empresas/Pessoas Físicas)
CREATE TABLE tomadores (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    razao_social NVARCHAR(255) NOT NULL,
    nome_fantasia NVARCHAR(255),
    cnpj_cpf NVARCHAR(18) NOT NULL UNIQUE,
    inscricao_estadual NVARCHAR(20),
    inscricao_municipal NVARCHAR(20),
    endereco NVARCHAR(500),
    cep NVARCHAR(10),
    cidade NVARCHAR(100),
    estado NVARCHAR(2),
    telefone NVARCHAR(20),
    email NVARCHAR(255),
    regime_tributario NVARCHAR(50) DEFAULT 'LUCRO_PRESUMIDO',
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    status NVARCHAR(20) DEFAULT 'ATIVO'
);

-- Tabela de Serviços/Atividades
CREATE TABLE servicos (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    codigo NVARCHAR(10) NOT NULL UNIQUE,
    descricao NVARCHAR(500) NOT NULL,
    aliquota_iss DECIMAL(5,2) NOT NULL,
    retencao_iss BIT DEFAULT 1,
    created_at DATETIME2 DEFAULT GETDATE()
);

-- Tabela de RPAs
CREATE TABLE rpas (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    numero NVARCHAR(20) NOT NULL UNIQUE,
    prestador_id BIGINT NOT NULL,
    tomador_id BIGINT NOT NULL,
    servico_id BIGINT NOT NULL,
    data_emissao DATE NOT NULL,
    data_vencimento DATE,
    data_pagamento DATE,
    descricao_servico NVARCHAR(MAX) NOT NULL,
    valor_bruto DECIMAL(15,2) NOT NULL,
    
    -- Cálculos de INSS
    base_calculo_inss DECIMAL(15,2),
    aliquota_inss DECIMAL(5,2),
    valor_inss DECIMAL(15,2),
    
    -- Cálculos de IRRF
    base_calculo_irrf DECIMAL(15,2),
    aliquota_irrf DECIMAL(5,2),
    valor_irrf DECIMAL(15,2),
    
    -- Cálculos de ISS
    base_calculo_iss DECIMAL(15,2),
    aliquota_iss DECIMAL(5,2),
    valor_iss DECIMAL(15,2),
    
    -- Outros descontos
    outros_descontos DECIMAL(15,2) DEFAULT 0,
    descricao_outros_descontos NVARCHAR(255),
    
    -- Valores finais
    total_descontos DECIMAL(15,2),
    valor_liquido DECIMAL(15,2),
    
    status NVARCHAR(20) DEFAULT 'EMITIDO',
    observacoes NVARCHAR(MAX),
    created_at DATETIME2 DEFAULT GETDATE(),
    updated_at DATETIME2 DEFAULT GETDATE(),
    created_by NVARCHAR(100),
    
    FOREIGN KEY (prestador_id) REFERENCES prestadores(id),
    FOREIGN KEY (tomador_id) REFERENCES tomadores(id),
    FOREIGN KEY (servico_id) REFERENCES servicos(id)
);

-- Tabela de Guias de Recolhimento
CREATE TABLE guias_recolhimento (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    rpa_id BIGINT NOT NULL,
    tipo_guia NVARCHAR(20) NOT NULL, -- DARF, GPS, ISS
    codigo_receita NVARCHAR(10),
    valor DECIMAL(15,2) NOT NULL,
    data_vencimento DATE NOT NULL,
    data_pagamento DATE,
    numero_documento NVARCHAR(50),
    status NVARCHAR(20) DEFAULT 'PENDENTE',
    observacoes NVARCHAR(255),
    created_at DATETIME2 DEFAULT GETDATE(),
    
    FOREIGN KEY (rpa_id) REFERENCES rpas(id)
);

-- Tabela de Configurações Fiscais
CREATE TABLE configuracoes_fiscais (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    ano_vigencia INT NOT NULL,
    mes_vigencia INT,
    
    -- Faixas INSS
    faixa1_inss_limite DECIMAL(15,2),
    faixa1_inss_aliquota DECIMAL(5,2),
    faixa2_inss_limite DECIMAL(15,2),
    faixa2_inss_aliquota DECIMAL(5,2),
    faixa3_inss_limite DECIMAL(15,2),
    faixa3_inss_aliquota DECIMAL(5,2),
    teto_inss DECIMAL(15,2),
    
    -- Faixas IRRF
    faixa1_irrf_limite DECIMAL(15,2),
    faixa1_irrf_aliquota DECIMAL(5,2),
    faixa2_irrf_limite DECIMAL(15,2),
    faixa2_irrf_aliquota DECIMAL(5,2),
    faixa3_irrf_limite DECIMAL(15,2),
    faixa3_irrf_aliquota DECIMAL(5,2),
    faixa4_irrf_aliquota DECIMAL(5,2),
    
    -- Deduções IRRF
    deducao_dependente DECIMAL(15,2),
    deducao_inss DECIMAL(15,2),
    
    -- ISS padrão
    aliquota_iss_padrao DECIMAL(5,2),
    
    created_at DATETIME2 DEFAULT GETDATE(),
    is_active BIT DEFAULT 1
);

-- Tabela de Histórico de Alterações
CREATE TABLE historico_alteracoes (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    tabela NVARCHAR(50) NOT NULL,
    registro_id BIGINT NOT NULL,
    campo NVARCHAR(100),
    valor_anterior NVARCHAR(MAX),
    valor_novo NVARCHAR(MAX),
    usuario NVARCHAR(100),
    data_alteracao DATETIME2 DEFAULT GETDATE(),
    motivo NVARCHAR(255)
);

-- Índices para performance
CREATE INDEX IX_rpas_prestador_id ON rpas(prestador_id);
CREATE INDEX IX_rpas_tomador_id ON rpas(tomador_id);
CREATE INDEX IX_rpas_data_emissao ON rpas(data_emissao);
CREATE INDEX IX_rpas_status ON rpas(status);
CREATE INDEX IX_guias_rpa_id ON guias_recolhimento(rpa_id);
CREATE INDEX IX_guias_status ON guias_recolhimento(status);
CREATE INDEX IX_prestadores_cpf ON prestadores(cpf);
CREATE INDEX IX_tomadores_cnpj_cpf ON tomadores(cnpj_cpf);
