-- Script de criação e configuração do sistema RPA Fiscal para SQL Server
-- Verifica se o banco de dados existe e cria se necessário

USE master;
GO

-- Verifica se o banco de dados já existe
IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'RPA_FISCAL_SYSTEM')
BEGIN
    CREATE DATABASE RPA_FISCAL_SYSTEM;
    PRINT 'Banco de dados RPA_FISCAL_SYSTEM criado com sucesso.';
END
ELSE
BEGIN
    PRINT 'Banco de dados RPA_FISCAL_SYSTEM já existe.';
END
GO

USE RPA_FISCAL_SYSTEM;
GO

-- Verifica e cria tabelas se não existirem
IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[prestadores]') AND type in (N'U'))
BEGIN
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
    PRINT 'Tabela prestadores criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela prestadores já existe.';
END

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[tomadores]') AND type in (N'U'))
BEGIN
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
    PRINT 'Tabela tomadores criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela tomadores já existe.';
END

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[servicos]') AND type in (N'U'))
BEGIN
    -- Tabela de Serviços/Atividades
    CREATE TABLE servicos (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        codigo NVARCHAR(10) NOT NULL UNIQUE,
        descricao NVARCHAR(500) NOT NULL,
        aliquota_iss DECIMAL(5,2) NOT NULL,
        retencao_iss BIT DEFAULT 1,
        created_at DATETIME2 DEFAULT GETDATE()
    );
    PRINT 'Tabela servicos criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela servicos já existe.';
END

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[configuracoes_fiscais]') AND type in (N'U'))
BEGIN
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
    PRINT 'Tabela configuracoes_fiscais criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela configuracoes_fiscais já existe.';
END

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[rpas]') AND type in (N'U'))
BEGIN
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
        
        CONSTRAINT FK_rpas_prestador FOREIGN KEY (prestador_id) REFERENCES prestadores(id),
        CONSTRAINT FK_rpas_tomador FOREIGN KEY (tomador_id) REFERENCES tomadores(id),
        CONSTRAINT FK_rpas_servico FOREIGN KEY (servico_id) REFERENCES servicos(id)
    );
    PRINT 'Tabela rpas criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela rpas já existe.';
END

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[guias_recolhimento]') AND type in (N'U'))
BEGIN
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
        
        CONSTRAINT FK_guias_rpa FOREIGN KEY (rpa_id) REFERENCES rpas(id)
    );
    PRINT 'Tabela guias_recolhimento criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela guias_recolhimento já existe.';
END

IF NOT EXISTS (SELECT * FROM sys.objects WHERE object_id = OBJECT_ID(N'[dbo].[historico_alteracoes]') AND type in (N'U'))
BEGIN
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
    PRINT 'Tabela historico_alteracoes criada com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela historico_alteracoes já existe.';
END

-- Criação de índices para otimização de performance
PRINT 'Criando índices para otimização de performance...';

-- Índices para tabela rpas
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_rpas_prestador_id' AND object_id = OBJECT_ID('rpas'))
    CREATE INDEX IX_rpas_prestador_id ON rpas(prestador_id);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_rpas_tomador_id' AND object_id = OBJECT_ID('rpas'))
    CREATE INDEX IX_rpas_tomador_id ON rpas(tomador_id);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_rpas_data_emissao' AND object_id = OBJECT_ID('rpas'))
    CREATE INDEX IX_rpas_data_emissao ON rpas(data_emissao);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_rpas_status' AND object_id = OBJECT_ID('rpas'))
    CREATE INDEX IX_rpas_status ON rpas(status);

-- Índices para tabela guias_recolhimento
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_guias_rpa_id' AND object_id = OBJECT_ID('guias_recolhimento'))
    CREATE INDEX IX_guias_rpa_id ON guias_recolhimento(rpa_id);

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_guias_status' AND object_id = OBJECT_ID('guias_recolhimento'))
    CREATE INDEX IX_guias_status ON guias_recolhimento(status);

-- Índices para tabela prestadores
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_prestadores_cpf' AND object_id = OBJECT_ID('prestadores'))
    CREATE INDEX IX_prestadores_cpf ON prestadores(cpf);

-- Índices para tabela tomadores
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_tomadores_cnpj_cpf' AND object_id = OBJECT_ID('tomadores'))
    CREATE INDEX IX_tomadores_cnpj_cpf ON tomadores(cnpj_cpf);

PRINT 'Índices criados com sucesso.';

-- Inserção de dados iniciais (se as tabelas estiverem vazias)
-- Configurações fiscais para 2024
IF NOT EXISTS (SELECT * FROM configuracoes_fiscais WHERE ano_vigencia = 2024)
BEGIN
    INSERT INTO configuracoes_fiscais (
        ano_vigencia, 
        faixa1_inss_limite, faixa1_inss_aliquota,
        faixa2_inss_limite, faixa2_inss_aliquota,
        faixa3_inss_limite, faixa3_inss_aliquota,
        teto_inss,
        faixa1_irrf_limite, faixa1_irrf_aliquota,
        faixa2_irrf_limite, faixa2_irrf_aliquota,
        faixa3_irrf_limite, faixa3_irrf_aliquota,
        faixa4_irrf_aliquota,
        deducao_dependente, deducao_inss,
        aliquota_iss_padrao
    ) VALUES (
        2024,
        1412.00, 7.5,
        2666.68, 9.0,
        4000.03, 12.0,
        7786.02,
        2112.00, 0.0,
        2826.65, 7.5,
        3751.05, 15.0,
        22.5,
        189.59, 1412.00,
        5.0
    );
    PRINT 'Configurações fiscais para 2024 inseridas com sucesso.';
END
ELSE
BEGIN
    PRINT 'Configurações fiscais para 2024 já existem.';
END

-- Serviços comuns
IF NOT EXISTS (SELECT * FROM servicos)
BEGIN
    INSERT INTO servicos (codigo, descricao, aliquota_iss, retencao_iss) VALUES
    ('01.01', 'Análise e desenvolvimento de sistemas', 2.00, 1),
    ('01.02', 'Programação', 2.00, 1),
    ('01.03', 'Processamento de dados e congêneres', 2.00, 1),
    ('01.04', 'Elaboração de programas de computadores', 2.00, 1),
    ('01.05', 'Licenciamento ou cessão de direito de uso de programas de computação', 2.00, 1),
    ('17.01', 'Assessoria ou consultoria de qualquer natureza', 5.00, 1),
    ('17.02', 'Análise, exame, pesquisa, coleta, compilação e fornecimento de dados e informações', 5.00, 1),
    ('17.03', 'Planejamento, coordenação, programação ou organização técnica', 5.00, 1),
    ('17.04', 'Elaboração de planos diretores, estudos de viabilidade', 5.00, 1),
    ('17.05', 'Elaboração de anteprojetos, projetos básicos e projetos executivos', 5.00, 1);
    PRINT 'Serviços comuns inseridos com sucesso.';
END
ELSE
BEGIN
    PRINT 'Tabela de serviços já possui dados.';
END

-- Criação de stored procedures úteis
PRINT 'Criando stored procedures úteis...';

-- Procedure para calcular impostos de um RPA
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_calcular_impostos_rpa')
    DROP PROCEDURE sp_calcular_impostos_rpa;
GO

CREATE PROCEDURE sp_calcular_impostos_rpa
    @rpa_id BIGINT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @valor_bruto DECIMAL(15,2);
    DECLARE @prestador_id BIGINT;
    DECLARE @is_mei BIT;
    DECLARE @aliquota_iss DECIMAL(5,2);
    DECLARE @retencao_iss BIT;
    
    -- Obter dados do RPA
    SELECT 
        @valor_bruto = r.valor_bruto,
        @prestador_id = r.prestador_id,
        @aliquota_iss = s.aliquota_iss,
        @retencao_iss = s.retencao_iss
    FROM rpas r
    INNER JOIN servicos s ON r.servico_id = s.id
    WHERE r.id = @rpa_id;
    
    -- Obter dados do prestador
    SELECT @is_mei = is_mei FROM prestadores WHERE id = @prestador_id;
    
    -- Obter configurações fiscais vigentes
    DECLARE @faixa1_inss_limite DECIMAL(15,2);
    DECLARE @faixa1_inss_aliquota DECIMAL(5,2);
    DECLARE @faixa2_inss_limite DECIMAL(15,2);
    DECLARE @faixa2_inss_aliquota DECIMAL(5,2);
    DECLARE @faixa3_inss_limite DECIMAL(15,2);
    DECLARE @faixa3_inss_aliquota DECIMAL(5,2);
    DECLARE @teto_inss DECIMAL(15,2);
    DECLARE @faixa1_irrf_limite DECIMAL(15,2);
    DECLARE @faixa1_irrf_aliquota DECIMAL(5,2);
    DECLARE @faixa2_irrf_limite DECIMAL(15,2);
    DECLARE @faixa2_irrf_aliquota DECIMAL(5,2);
    DECLARE @faixa3_irrf_limite DECIMAL(15,2);
    DECLARE @faixa3_irrf_aliquota DECIMAL(5,2);
    DECLARE @faixa4_irrf_aliquota DECIMAL(5,2);
    
    SELECT TOP 1
        @faixa1_inss_limite = faixa1_inss_limite,
        @faixa1_inss_aliquota = faixa1_inss_aliquota,
        @faixa2_inss_limite = faixa2_inss_limite,
        @faixa2_inss_aliquota = faixa2_inss_aliquota,
        @faixa3_inss_limite = faixa3_inss_limite,
        @faixa3_inss_aliquota = faixa3_inss_aliquota,
        @teto_inss = teto_inss,
        @faixa1_irrf_limite = faixa1_irrf_limite,
        @faixa1_irrf_aliquota = faixa1_irrf_aliquota,
        @faixa2_irrf_limite = faixa2_irrf_limite,
        @faixa2_irrf_aliquota = faixa2_irrf_aliquota,
        @faixa3_irrf_limite = faixa3_irrf_limite,
        @faixa3_irrf_aliquota = faixa3_irrf_aliquota,
        @faixa4_irrf_aliquota = faixa4_irrf_aliquota
    FROM configuracoes_fiscais
    WHERE is_active = 1
    ORDER BY ano_vigencia DESC;
    
    -- Calcular INSS
    DECLARE @valor_inss DECIMAL(15,2) = 0;
    DECLARE @aliquota_inss DECIMAL(5,2) = 0;
    
    IF @is_mei = 0 -- Não é MEI
    BEGIN
        IF @valor_bruto <= @faixa1_inss_limite
        BEGIN
            SET @aliquota_inss = @faixa1_inss_aliquota;
            SET @valor_inss = ROUND(@valor_bruto * @aliquota_inss / 100, 2);
        END
        ELSE IF @valor_bruto <= @faixa2_inss_limite
        BEGIN
            SET @aliquota_inss = @faixa2_inss_aliquota;
            SET @valor_inss = ROUND(@valor_bruto * @aliquota_inss / 100, 2);
        END
        ELSE IF @valor_bruto <= @faixa3_inss_limite
        BEGIN
            SET @aliquota_inss = @faixa3_inss_aliquota;
            SET @valor_inss = ROUND(@valor_bruto * @aliquota_inss / 100, 2);
        END
        ELSE
        BEGIN
            SET @aliquota_inss = @faixa3_inss_aliquota;
            SET @valor_inss = ROUND(@teto_inss * @aliquota_inss / 100, 2);
        END
    END
    
    -- Calcular IRRF
    DECLARE @base_calculo_irrf DECIMAL(15,2) = @valor_bruto - @valor_inss;
    DECLARE @valor_irrf DECIMAL(15,2) = 0;
    DECLARE @aliquota_irrf DECIMAL(5,2) = 0;
    
    IF @base_calculo_irrf <= @faixa1_irrf_limite
    BEGIN
        SET @aliquota_irrf = @faixa1_irrf_aliquota;
        SET @valor_irrf = 0;
    END
    ELSE IF @base_calculo_irrf <= @faixa2_irrf_limite
    BEGIN
        SET @aliquota_irrf = @faixa2_irrf_aliquota;
        SET @valor_irrf = ROUND(@base_calculo_irrf * @aliquota_irrf / 100, 2);
    END
    ELSE IF @base_calculo_irrf <= @faixa3_irrf_limite
    BEGIN
        SET @aliquota_irrf = @faixa3_irrf_aliquota;
        SET @valor_irrf = ROUND(@base_calculo_irrf * @aliquota_irrf / 100, 2);
    END
    ELSE
    BEGIN
        SET @aliquota_irrf = @faixa4_irrf_aliquota;
        SET @valor_irrf = ROUND(@base_calculo_irrf * @aliquota_irrf / 100, 2);
    END
    
    -- Calcular ISS
    DECLARE @valor_iss DECIMAL(15,2) = 0;
    
    IF @retencao_iss = 1
    BEGIN
        SET @valor_iss = ROUND(@valor_bruto * @aliquota_iss / 100, 2);
    END
    
    -- Calcular totais
    DECLARE @total_descontos DECIMAL(15,2) = @valor_inss + @valor_irrf + @valor_iss;
    DECLARE @valor_liquido DECIMAL(15,2) = @valor_bruto - @total_descontos;
    
    -- Atualizar RPA
    UPDATE rpas SET
        base_calculo_inss = @valor_bruto,
        aliquota_inss = @aliquota_inss,
        valor_inss = @valor_inss,
        
        base_calculo_irrf = @base_calculo_irrf,
        aliquota_irrf = @aliquota_irrf,
        valor_irrf = @valor_irrf,
        
        base_calculo_iss = CASE WHEN @retencao_iss = 1 THEN @valor_bruto ELSE 0 END,
        aliquota_iss = CASE WHEN @retencao_iss = 1 THEN @aliquota_iss ELSE 0 END,
        valor_iss = @valor_iss,
        
        total_descontos = @total_descontos,
        valor_liquido = @valor_liquido,
        
        updated_at = GETDATE()
    WHERE id = @rpa_id;
    
    -- Retornar os valores calculados
    SELECT
        @valor_bruto AS valor_bruto,
        @valor_inss AS valor_inss,
        @valor_irrf AS valor_irrf,
        @valor_iss AS valor_iss,
        @total_descontos AS total_descontos,
        @valor_liquido AS valor_liquido;
END
GO

-- Procedure para gerar guias de recolhimento
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'P' AND name = 'sp_gerar_guias_recolhimento')
    DROP PROCEDURE sp_gerar_guias_recolhimento;
GO

CREATE PROCEDURE sp_gerar_guias_recolhimento
    @rpa_id BIGINT
AS
BEGIN
    SET NOCOUNT ON;
    
    DECLARE @valor_inss DECIMAL(15,2);
    DECLARE @valor_irrf DECIMAL(15,2);
    DECLARE @valor_iss DECIMAL(15,2);
    DECLARE @data_emissao DATE;
    
    -- Obter dados do RPA
    SELECT 
        @valor_inss = valor_inss,
        @valor_irrf = valor_irrf,
        @valor_iss = valor_iss,
        @data_emissao = data_emissao
    FROM rpas
    WHERE id = @rpa_id;
    
    -- Excluir guias existentes para este RPA
    DELETE FROM guias_recolhimento WHERE rpa_id = @rpa_id;
    
    -- Calcular datas de vencimento
    DECLARE @vencimento_inss DATE = DATEADD(MONTH, 1, DATEFROMPARTS(YEAR(@data_emissao), MONTH(@data_emissao), 20));
    DECLARE @vencimento_irrf DATE = DATEADD(MONTH, 1, DATEFROMPARTS(YEAR(@data_emissao), MONTH(@data_emissao), 20));
    DECLARE @vencimento_iss DATE = DATEADD(MONTH, 1, DATEFROMPARTS(YEAR(@data_emissao), MONTH(@data_emissao), 10));
    
    -- Gerar guia INSS se houver valor
    IF @valor_inss > 0
    BEGIN
        INSERT INTO guias_recolhimento (
            rpa_id, tipo_guia, codigo_receita, valor, data_vencimento, status
        ) VALUES (
            @rpa_id, 'GPS', '1007', @valor_inss, @vencimento_inss, 'PENDENTE'
        );
    END
    
    -- Gerar guia IRRF se houver valor
    IF @valor_irrf > 0
    BEGIN
        INSERT INTO guias_recolhimento (
            rpa_id, tipo_guia, codigo_receita, valor, data_vencimento, status
        ) VALUES (
            @rpa_id, 'DARF', '0588', @valor_irrf, @vencimento_irrf, 'PENDENTE'
        );
    END
    
    -- Gerar guia ISS se houver valor
    IF @valor_iss > 0
    BEGIN
        INSERT INTO guias_recolhimento (
            rpa_id, tipo_guia, codigo_receita, valor, data_vencimento, status
        ) VALUES (
            @rpa_id, 'ISS', NULL, @valor_iss, @vencimento_iss, 'PENDENTE'
        );
    END
    
    -- Retornar as guias geradas
    SELECT * FROM guias_recolhimento WHERE rpa_id = @rpa_id;
END
GO

-- Trigger para atualizar data de modificação
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_rpas_update')
    DROP TRIGGER trg_rpas_update;
GO

CREATE TRIGGER trg_rpas_update
ON rpas
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE rpas
    SET updated_at = GETDATE()
    FROM rpas r
    INNER JOIN inserted i ON r.id = i.id;
END
GO

-- Trigger para atualizar data de modificação em prestadores
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_prestadores_update')
    DROP TRIGGER trg_prestadores_update;
GO

CREATE TRIGGER trg_prestadores_update
ON prestadores
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE prestadores
    SET updated_at = GETDATE()
    FROM prestadores p
    INNER JOIN inserted i ON p.id = i.id;
END
GO

-- Trigger para atualizar data de modificação em tomadores
IF EXISTS (SELECT * FROM sys.triggers WHERE name = 'trg_tomadores_update')
    DROP TRIGGER trg_tomadores_update;
GO

CREATE TRIGGER trg_tomadores_update
ON tomadores
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;
    
    UPDATE tomadores
    SET updated_at = GETDATE()
    FROM tomadores t
    INNER JOIN inserted i ON t.id = i.id;
END
GO

-- Função para gerar número de RPA
IF EXISTS (SELECT * FROM sys.objects WHERE type = 'FN' AND name = 'fn_gerar_numero_rpa')
    DROP FUNCTION fn_gerar_numero_rpa;
GO

CREATE FUNCTION fn_gerar_numero_rpa()
RETURNS NVARCHAR(20)
AS
BEGIN
    DECLARE @ano CHAR(4) = CAST(YEAR(GETDATE()) AS CHAR(4));
    DECLARE @mes CHAR(2) = RIGHT('0' + CAST(MONTH(GETDATE()) AS VARCHAR(2)), 2);
    
    DECLARE @proximo_numero INT = (
        SELECT COUNT(*) + 1
        FROM rpas
        WHERE YEAR(data_emissao) = YEAR(GETDATE())
    );
    
    RETURN 'RPA-' + @ano + '-' + @mes + '-' + RIGHT('0000' + CAST(@proximo_numero AS VARCHAR(4)), 4);
END
GO

PRINT 'Stored procedures e funções criadas com sucesso.';
PRINT 'Script de configuração do SQL Server concluído com sucesso!';
GO