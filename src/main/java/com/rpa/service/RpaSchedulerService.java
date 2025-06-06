package com.rpa.service;

import com.rpa.entity.RpaProcess;
import com.rpa.entity.RpaSchedule;
import com.rpa.repository.RpaProcessRepository;
import com.rpa.repository.RpaScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RpaSchedulerService {
    
    private final RpaProcessRepository processRepository;
    private final RpaScheduleRepository scheduleRepository;
    private final RpaExecutionService executionService;
    private final CronExpressionParser cronParser;
    
    @Scheduled(fixedRate = 60000) // Verifica a cada minuto
    public void checkScheduledProcesses() {
        log.debug("Verificando processos agendados...");
        
        List<RpaSchedule> activeSchedules = scheduleRepository.findByIsActiveTrue();
        LocalDateTime now = LocalDateTime.now();
        
        for (RpaSchedule schedule : activeSchedules) {
            if (schedule.getNextExecution() != null && 
                schedule.getNextExecution().isBefore(now)) {
                
                log.info("Executando processo agendado: {}", schedule.getProcess().getName());
                
                // Executar processo
                executionService.executeProcess(
                    schedule.getProcess(), 
                    com.rpa.entity.RpaExecution.TriggerType.SCHEDULED
                );
                
                // Calcular próxima execução
                LocalDateTime nextExecution = cronParser.getNextExecution(
                    schedule.getCronExpression(), now
                );
                
                schedule.setLastExecution(now);
                schedule.setNextExecution(nextExecution);
                scheduleRepository.save(schedule);
            }
        }
    }
    
    public void scheduleProcess(Long processId, String cronExpression) {
        RpaProcess process = processRepository.findById(processId)
            .orElseThrow(() -> new RuntimeException("Processo não encontrado"));
        
        RpaSchedule schedule = new RpaSchedule();
        schedule.setProcess(process);
        schedule.setCronExpression(cronExpression);
        schedule.setIsActive(true);
        schedule.setNextExecution(
            cronParser.getNextExecution(cronExpression, LocalDateTime.now())
        );
        
        scheduleRepository.save(schedule);
    }
    
    public void unscheduleProcess(Long processId) {
        List<RpaSchedule> schedules = scheduleRepository.findByProcessId(processId);
        schedules.forEach(schedule -> {
            schedule.setIsActive(false);
            scheduleRepository.save(schedule);
        });
    }
}
