package com.rpa.fiscal.service;

import com.rpa.fiscal.entity.ConfiguracaoFiscal;
import com.rpa.fiscal.entity.Rpa;
import com.rpa.fiscal.repository.ConfiguracaoFiscalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CalculoFiscalService {
    
    private final ConfiguracaoFiscalRepository configuracaoRepository;
    
    public void calcularImpostos(Rpa rpa) {
        ConfiguracaoFiscal config = getConfiguracaoVigente();
        
        // Calcular INSS
        calcularInss(rpa, config);
        
        // Calcular IRRF (após dedução do INSS)
        calcularIrrf(rpa, config);
        
        // Calcular ISS
        calcularIss(rpa);
        
        // Calcular totais
        calcularTotais(rpa);
        
        log.info("Impostos calculados para RPA {}: INSS={}, IRRF={}, ISS={}", 
                rpa.getNumero(), rpa.getValorInss(), rpa.getValorIrrf(), rpa.getValorIss());
    }
    
    private void calcularInss(Rpa rpa, ConfiguracaoFiscal config) {
        BigDecimal valorBruto = rpa.getValorBruto();
        BigDecimal valorInss = BigDecimal.ZERO;
        BigDecimal aliquota = BigDecimal.ZERO;
        
        // Verificar se prestador é MEI (isento de INSS)
        if (rpa.getPrestador().getIsMei()) {
            rpa.setBaseCalculoInss(BigDecimal.ZERO);
            rpa.setAliquotaInss(BigDecimal.ZERO);
            rpa.setValorInss(BigDecimal.ZERO);
            return;
        }
        
        // Aplicar faixas progressivas do INSS
        if (valorBruto.compareTo(config.getFaixa1InssLimite()) <= 0) {
            aliquota = config.getFaixa1InssAliquota();
            valorInss = valorBruto.multiply(aliquota).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (valorBruto.compareTo(config.getFaixa2InssLimite()) <= 0) {
            aliquota = config.getFaixa2InssAliquota();
            valorInss = valorBruto.multiply(aliquota).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (valorBruto.compareTo(config.getFaixa3InssLimite()) <= 0) {
            aliquota = config.getFaixa3InssAliquota();
            valorInss = valorBruto.multiply(aliquota).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            // Acima do teto - aplicar sobre o teto
            aliquota = config.getFaixa3InssAliquota();
            valorInss = config.getTetoInss().multiply(aliquota).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        
        rpa.setBaseCalculoInss(valorBruto);
        rpa.setAliquotaInss(aliquota);
        rpa.setValorInss(valorInss);
    }
    
    private void calcularIrrf(Rpa rpa, ConfiguracaoFiscal config) {
        BigDecimal valorBruto = rpa.getValorBruto();
        BigDecimal valorInss = rpa.getValorInss() != null ? rpa.getValorInss() : BigDecimal.ZERO;
        
        // Base de cálculo = Valor Bruto - INSS
        BigDecimal baseCalculo = valorBruto.subtract(valorInss);
        
        BigDecimal valorIrrf = BigDecimal.ZERO;
        BigDecimal aliquota = BigDecimal.ZERO;
        
        // Aplicar faixas do IRRF
        if (baseCalculo.compareTo(config.getFaixa1IrrfLimite()) <= 0) {
            // Isento
            aliquota = BigDecimal.ZERO;
            valorIrrf = BigDecimal.ZERO;
        } else if (baseCalculo.compareTo(config.getFaixa2IrrfLimite()) <= 0) {
            aliquota = config.getFaixa2IrrfAliquota();
            valorIrrf = baseCalculo.multiply(aliquota).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else if (baseCalculo.compareTo(config.getFaixa3IrrfLimite()) <= 0) {
            aliquota = config.getFaixa3IrrfAliquota();
            valorIrrf = baseCalculo.multiply(aliquota).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        } else {
            aliquota = config.getFaixa4IrrfAliquota();
            valorIrrf = baseCalculo.multiply(aliquota).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        
        rpa.setBaseCalculoIrrf(baseCalculo);
        rpa.setAliquotaIrrf(aliquota);
        rpa.setValorIrrf(valorIrrf);
    }
    
    private void calcularIss(Rpa rpa) {
        BigDecimal valorBruto = rpa.getValorBruto();
        BigDecimal aliquotaIss = rpa.getServico().getAliquotaIss();
        
        // Verificar se há retenção de ISS
        if (!rpa.getServico().getRetencaoIss()) {
            rpa.setBaseCalculoIss(BigDecimal.ZERO);
            rpa.setAliquotaIss(BigDecimal.ZERO);
            rpa.setValorIss(BigDecimal.ZERO);
            return;
        }
        
        BigDecimal valorIss = valorBruto.multiply(aliquotaIss).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        rpa.setBaseCalculoIss(valorBruto);
        rpa.setAliquotaIss(aliquotaIss);
        rpa.setValorIss(valorIss);
    }
    
    private void calcularTotais(Rpa rpa) {
        BigDecimal valorInss = rpa.getValorInss() != null ? rpa.getValorInss() : BigDecimal.ZERO;
        BigDecimal valorIrrf = rpa.getValorIrrf() != null ? rpa.getValorIrrf() : BigDecimal.ZERO;
        BigDecimal valorIss = rpa.getValorIss() != null ? rpa.getValorIss() : BigDecimal.ZERO;
        BigDecimal outrosDescontos = rpa.getOutrosDescontos() != null ? rpa.getOutrosDescontos() : BigDecimal.ZERO;
        
        BigDecimal totalDescontos = valorInss.add(valorIrrf).add(valorIss).add(outrosDescontos);
        BigDecimal valorLiquido = rpa.getValorBruto().subtract(totalDescontos);
        
        rpa.setTotalDescontos(totalDescontos);
        rpa.setValorLiquido(valorLiquido);
    }
    
    private ConfiguracaoFiscal getConfiguracaoVigente() {
        int anoAtual = LocalDate.now().getYear();
        return configuracaoRepository.findByAnoVigenciaAndIsActiveTrue(anoAtual)
                .orElseThrow(() -> new RuntimeException("Configuração fiscal não encontrada para o ano " + anoAtual));
    }
}
