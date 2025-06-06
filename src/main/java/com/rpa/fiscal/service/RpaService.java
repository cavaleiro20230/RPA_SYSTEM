package com.rpa.fiscal.service;

import com.rpa.fiscal.entity.Rpa;
import com.rpa.fiscal.entity.GuiaRecolhimento;
import com.rpa.fiscal.repository.RpaRepository;
import com.rpa.fiscal.repository.GuiaRecolhimentoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RpaService {
    
    private final RpaRepository rpaRepository;
    private final GuiaRecolhimentoRepository guiaRepository;
    private final CalculoFiscalService calculoFiscalService;
    private final GuiaRecolhimentoService guiaService;
    
    @Transactional
    public Rpa criarRpa(Rpa rpa) {
        // Gerar número do RPA
        rpa.setNumero(gerarNumeroRpa());
        
        // Calcular impostos
        calculoFiscalService.calcularImpostos(rpa);
        
        // Salvar RPA
        rpa = rpaRepository.save(rpa);
        
        // Gerar guias de recolhimento
        guiaService.gerarGuiasRecolhimento(rpa);
        
        log.info("RPA {} criado com sucesso para prestador {}", rpa.getNumero(), rpa.getPrestador().getNome());
        
        return rpa;
    }
    
    @Transactional
    public Rpa atualizarRpa(Long id, Rpa rpaAtualizado) {
        Rpa rpaExistente = rpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RPA não encontrado"));
        
        // Atualizar campos
        rpaExistente.setDescricaoServico(rpaAtualizado.getDescricaoServico());
        rpaExistente.setValorBruto(rpaAtualizado.getValorBruto());
        rpaExistente.setDataVencimento(rpaAtualizado.getDataVencimento());
        rpaExistente.setObservacoes(rpaAtualizado.getObservacoes());
        
        // Recalcular impostos
        calculoFiscalService.calcularImpostos(rpaExistente);
        
        return rpaRepository.save(rpaExistente);
    }
    
    public Page<Rpa> listarRpas(Pageable pageable) {
        return rpaRepository.findAll(pageable);
    }
    
    public Optional<Rpa> buscarPorId(Long id) {
        return rpaRepository.findById(id);
    }
    
    public List<Rpa> buscarPorPrestador(Long prestadorId) {
        return rpaRepository.findByPrestadorIdOrderByDataEmissaoDesc(prestadorId);
    }
    
    public List<Rpa> buscarPorTomador(Long tomadorId) {
        return rpaRepository.findByTomadorIdOrderByDataEmissaoDesc(tomadorId);
    }
    
    public List<Rpa> buscarPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return rpaRepository.findByDataEmissaoBetween(dataInicio, dataFim);
    }
    
    @Transactional
    public void marcarComoPago(Long id, LocalDate dataPagamento) {
        Rpa rpa = rpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RPA não encontrado"));
        
        rpa.setDataPagamento(dataPagamento);
        rpa.setStatus(Rpa.StatusRpa.PAGO);
        
        rpaRepository.save(rpa);
        
        log.info("RPA {} marcado como pago em {}", rpa.getNumero(), dataPagamento);
    }
    
    @Transactional
    public void cancelarRpa(Long id, String motivo) {
        Rpa rpa = rpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RPA não encontrado"));
        
        rpa.setStatus(Rpa.StatusRpa.CANCELADO);
        rpa.setObservacoes((rpa.getObservacoes() != null ? rpa.getObservacoes() + "\n" : "") + 
                          "CANCELADO: " + motivo);
        
        rpaRepository.save(rpa);
        
        log.info("RPA {} cancelado. Motivo: {}", rpa.getNumero(), motivo);
    }
    
    private String gerarNumeroRpa() {
        String ano = String.valueOf(LocalDate.now().getYear());
        String mes = String.format("%02d", LocalDate.now().getMonthValue());
        
        Long proximoNumero = rpaRepository.countByDataEmissaoYear(LocalDate.now().getYear()) + 1;
        
        return String.format("RPA-%s-%s-%04d", ano, mes, proximoNumero);
    }
    
    public BigDecimal calcularTotalImpostosRetidos(LocalDate dataInicio, LocalDate dataFim) {
        List<Rpa> rpas = buscarPorPeriodo(dataInicio, dataFim);
        
        return rpas.stream()
                .map(rpa -> {
                    BigDecimal inss = rpa.getValorInss() != null ? rpa.getValorInss() : BigDecimal.ZERO;
                    BigDecimal irrf = rpa.getValorIrrf() != null ? rpa.getValorIrrf() : BigDecimal.ZERO;
                    BigDecimal iss = rpa.getValorIss() != null ? rpa.getValorIss() : BigDecimal.ZERO;
                    return inss.add(irrf).add(iss);
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
