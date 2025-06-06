package com.rpa.service;

import com.rpa.entity.RpaExecution;
import com.rpa.entity.RpaLog;
import com.rpa.entity.RpaProcess;
import com.rpa.repository.RpaExecutionRepository;
import com.rpa.repository.RpaLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class RpaExecutionService {
    
    private final RpaExecutionRepository executionRepository;
    private final RpaLogRepository logRepository;
    private final RpaScriptExecutor scriptExecutor;
    
    @Async
    @Transactional
    public CompletableFuture<RpaExecution> executeProcess(RpaProcess process, RpaExecution.TriggerType triggerType) {
        RpaExecution execution = new RpaExecution();
        execution.setProcess(process);
        execution.setStatus(RpaExecution.ExecutionStatus.RUNNING);
        execution.setStartTime(LocalDateTime.now());
        execution.setTriggeredBy(triggerType);
        
        execution = executionRepository.save(execution);
        
        try {
            logInfo(execution, "Iniciando execução do processo: " + process.getName());
            
            // Executar o script
            String result = scriptExecutor.execute(process.getScriptContent(), process.getScriptType());
            
            execution.setStatus(RpaExecution.ExecutionStatus.SUCCESS);
            execution.setResultMessage(result);
            logInfo(execution, "Processo executado com sucesso");
            
        } catch (Exception e) {
            log.error("Erro na execução do processo {}: {}", process.getName(), e.getMessage(), e);
            execution.setStatus(RpaExecution.ExecutionStatus.FAILED);
            execution.setErrorMessage(e.getMessage());
            logError(execution, "Erro na execução: " + e.getMessage());
        } finally {
            execution.setEndTime(LocalDateTime.now());
            execution.setDurationMs(
                java.time.Duration.between(execution.getStartTime(), execution.getEndTime()).toMillis()
            );
            executionRepository.save(execution);
        }
        
        return CompletableFuture.completedFuture(execution);
    }
    
    public List<RpaExecution> getExecutionsByProcess(Long processId) {
        return executionRepository.findByProcessIdOrderByStartTimeDesc(processId);
    }
    
    public List<RpaExecution> getRunningExecutions() {
        return executionRepository.findByStatus(RpaExecution.ExecutionStatus.RUNNING);
    }
    
    public long getRunningExecutionsCount() {
        return executionRepository.countRunningExecutions();
    }
    
    @Transactional
    public void cancelExecution(Long executionId) {
        RpaExecution execution = executionRepository.findById(executionId)
            .orElseThrow(() -> new RuntimeException("Execução não encontrada"));
        
        if (execution.getStatus() == RpaExecution.ExecutionStatus.RUNNING) {
            execution.setStatus(RpaExecution.ExecutionStatus.CANCELLED);
            execution.setEndTime(LocalDateTime.now());
            execution.setDurationMs(
                java.time.Duration.between(execution.getStartTime(), execution.getEndTime()).toMillis()
            );
            executionRepository.save(execution);
            logInfo(execution, "Execução cancelada pelo usuário");
        }
    }
    
    private void logInfo(RpaExecution execution, String message) {
        saveLog(execution, RpaLog.LogLevel.INFO, message);
    }
    
    private void logError(RpaExecution execution, String message) {
        saveLog(execution, RpaLog.LogLevel.ERROR, message);
    }
    
    private void saveLog(RpaExecution execution, RpaLog.LogLevel level, String message) {
        RpaLog log = new RpaLog();
        log.setExecution(execution);
        log.setLogLevel(level);
        log.setMessage(message);
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);
    }
}
