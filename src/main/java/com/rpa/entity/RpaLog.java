package com.rpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "rpa_logs")
@Data
public class RpaLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "execution_id", nullable = false)
    private RpaExecution execution;
    
    @Column(name = "log_level")
    @Enumerated(EnumType.STRING)
    private LogLevel logLevel;
    
    @Column(columnDefinition = "NVARCHAR(MAX)")
    private String message;
    
    private LocalDateTime timestamp = LocalDateTime.now();
    
    @Column(name = "step_name")
    private String stepName;
    
    public enum LogLevel {
        INFO, WARN, ERROR, DEBUG
    }
}
