package com.rpa.fiscal.controller;

import com.rpa.fiscal.entity.Rpa;
import com.rpa.fiscal.service.RpaService;
import com.rpa.fiscal.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rpas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RpaController {
    
    private final RpaService rpaService;
    private final RelatorioService relatorioService;
    
    @GetMapping
    public Page<Rpa> listarRpas(Pageable pageable) {
        return rpaService.listarRpas(pageable);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Rpa> buscarRpa(@PathVariable Long id) {
        return rpaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Rpa criarRpa(@RequestBody Rpa rpa) {
        return rpaService.criarRpa(rpa);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Rpa> atualizarRpa(@PathVariable Long id, @RequestBody Rpa rpa) {
        try {
            Rpa rpaAtualizado = rpaService.atualizarRpa(id, rpa);
            return ResponseEntity.ok(rpaAtualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/pagar")
    public ResponseEntity<Void> marcarComoPago(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamento) {
        try {
            rpaService.marcarComoPago(id, dataPagamento);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelarRpa(@PathVariable Long id, @RequestParam String motivo) {
        try {
            rpaService.cancelarRpa(id, motivo);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/prestador/{prestadorId}")
    public List<Rpa> buscarPorPrestador(@PathVariable Long prestadorId) {
        return rpaService.buscarPorPrestador(prestadorId);
    }
    
    @GetMapping("/tomador/{tomadorId}")
    public List<Rpa> buscarPorTomador(@PathVariable Long tomadorId) {
        return rpaService.buscarPorTomador(tomadorId);
    }
    
    @GetMapping("/periodo")
    public List<Rpa> buscarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim) {
        return rpaService.buscarPorPeriodo(dataInicio, dataFim);
    }
    
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> gerarPdfRpa(@PathVariable Long id) {
        try {
            byte[] pdf = relatorioService.gerarRpaPdf(id);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=rpa-" + id + ".pdf")
                    .body(pdf);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
