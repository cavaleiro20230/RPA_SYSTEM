-- Dados iniciais para o sistema RPA
USE RPA_FISCAL_SYSTEM;

-- Configurações fiscais para 2024
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

-- Serviços comuns
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

-- Prestadores de exemplo
INSERT INTO prestadores (nome, cpf, endereco, cidade, estado, telefone, email, atividade_principal) VALUES
('João Silva Santos', '123.456.789-01', 'Rua das Flores, 123', 'São Paulo', 'SP', '(11) 99999-1234', 'joao@email.com', 'Desenvolvedor de Sistemas'),
('Maria Oliveira Costa', '987.654.321-02', 'Av. Paulista, 456', 'São Paulo', 'SP', '(11) 88888-5678', 'maria@email.com', 'Consultora Empresarial'),
('Pedro Souza Lima', '456.789.123-03', 'Rua do Comércio, 789', 'Rio de Janeiro', 'RJ', '(21) 77777-9012', 'pedro@email.com', 'Designer Gráfico');

-- Tomadores de exemplo
INSERT INTO tomadores (razao_social, nome_fantasia, cnpj_cpf, endereco, cidade, estado, telefone, email, regime_tributario) VALUES
('Tech Solutions Ltda', 'TechSol', '12.345.678/0001-90', 'Rua da Tecnologia, 100', 'São Paulo', 'SP', '(11) 3333-4444', 'contato@techsol.com.br', 'LUCRO_PRESUMIDO'),
('Consultoria Empresarial S/A', 'ConsultEmp', '98.765.432/0001-10', 'Av. dos Negócios, 200', 'São Paulo', 'SP', '(11) 2222-3333', 'financeiro@consultemp.com.br', 'LUCRO_REAL'),
('Inovação Digital ME', 'InovaDig', '11.222.333/0001-44', 'Rua da Inovação, 300', 'Belo Horizonte', 'MG', '(31) 1111-2222', 'rh@inovadig.com.br', 'SIMPLES_NACIONAL');

-- RPAs de exemplo
INSERT INTO rpas (
    numero, prestador_id, tomador_id, servico_id, data_emissao, 
    descricao_servico, valor_bruto, status
) VALUES
('RPA-2024-001', 1, 1, 1, '2024-01-15', 'Desenvolvimento de sistema web para gestão de estoque', 5000.00, 'EMITIDO'),
('RPA-2024-002', 2, 2, 6, '2024-01-20', 'Consultoria em processos empresariais - Janeiro/2024', 3000.00, 'PAGO'),
('RPA-2024-003', 3, 3, 1, '2024-01-25', 'Criação de identidade visual e material gráfico', 2500.00, 'EMITIDO');
