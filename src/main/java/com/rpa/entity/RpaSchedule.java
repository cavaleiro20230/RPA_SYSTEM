package com.rpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "rpa_schedules")
@Data
public class RpaSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "process_id", nullable = false)
    private RpaProcess process;
    
    @Column(name = "cron_expression")
    private String cronExpression;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "next_execution")
    private LocalDateTime nextExecution;
    
    @Column(name = "last_execution")
    private LocalDateTime lastExecution;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}
