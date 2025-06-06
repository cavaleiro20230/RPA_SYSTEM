package com.rpa.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rpa_processes")
@Data
public class RpaProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String description;
    
    @Column(name = "script_content", columnDefinition = "NVARCHAR(MAX)")
    private String scriptContent;
    
    @Column(name = "script_type")
    private String scriptType;
    
    @Enumerated(EnumType.STRING)
    private ProcessStatus status = ProcessStatus.ACTIVE;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(name = "created_by")
    private String createdBy;
    
    @Column(name = "cron_expression")
    private String cronExpression;
    
    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
    private List<RpaExecution> executions;
    
    @OneToMany(mappedBy = "process", cascade = CascadeType.ALL)
    private List<RpaSchedule> schedules;
    
    public enum ProcessStatus {
        ACTIVE, INACTIVE, DELETED
    }
}
