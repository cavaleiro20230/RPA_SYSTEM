package com.rpa.controller;

import com.rpa.entity.RpaProcess;
import com.rpa.service.RpaProcessService;
import com.rpa.service.RpaExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/processes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RpaProcessController {
    
    private final RpaProcessService processService;
    private final RpaExecutionService executionService;
    
    @GetMapping
    public Page<RpaProcess> getAllProcesses(Pageable pageable) {
        return processService.getAllProcesses(pageable);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RpaProcess> getProcess(@PathVariable Long id) {
        return processService.getProcessById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public RpaProcess createProcess(@RequestBody RpaProcess process) {
        return processService.createProcess(process);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<RpaProcess> updateProcess(@PathVariable Long id, @RequestBody RpaProcess process) {
        return ResponseEntity.ok(processService.updateProcess(id, process));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProcess(@PathVariable Long id) {
        processService.deleteProcess(id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/{id}/execute")
    public ResponseEntity<String> executeProcess(@PathVariable Long id) {
        RpaProcess process = processService.getProcessById(id)
            .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
        
        executionService.executeProcess(process, com.rpa.entity.RpaExecution.TriggerType.MANUAL);
        return ResponseEntity.ok("Processo iniciado com sucesso");
    }
    
    @GetMapping("/{id}/executions")
    public ResponseEntity<List<com.rpa.entity.RpaExecution>> getProcessExecutions(@PathVariable Long id) {
        return ResponseEntity.ok(executionService.getExecutionsByProcess(id));
    }
    
    @GetMapping("/search")
    public List<RpaProcess> searchProcesses(@RequestParam String query) {
        return processService.searchProcesses(query);
    }
}
