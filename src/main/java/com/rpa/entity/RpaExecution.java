package com.rpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rpa_executions")
@Data
public class RpaExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "process_id", nullable = false)
    private RpaProcess process;
    
    @Enumerated(EnumType.STRING)
    private ExecutionStatus status;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_ms")
    private Long durationMs;
    
    @Column(name = "result_message", columnDefinition = "NVARCHAR(MAX)")
    private String resultMessage;
    
    @Column(name = "error_message", columnDefinition = "NVARCHAR(MAX)")
    private String errorMessage;
    
    @Column(name = "execution_log", columnDefinition = "NVARCHAR(MAX)")
    private String executionLog;
    
    @Column(name = "triggered_by")
    @Enumerated(EnumType.STRING)
    private TriggerType triggeredBy;
    
    @OneToMany(mappedBy = "execution", cascade = CascadeType.ALL)
    private List<RpaLog> logs;
    
    public enum ExecutionStatus {
        RUNNING, SUCCESS, FAILED, CANCELLED
    }
    
    public enum TriggerType {
        MANUAL, SCHEDULED, API
    }
}
