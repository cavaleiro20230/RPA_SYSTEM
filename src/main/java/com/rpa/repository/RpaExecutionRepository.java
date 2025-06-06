package com.rpa.repository;

import com.rpa.entity.RpaExecution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RpaExecutionRepository extends JpaRepository<RpaExecution, Long> {
    List<RpaExecution> findByProcessIdOrderByStartTimeDesc(Long processId);
    
    List<RpaExecution> findByStatus(RpaExecution.ExecutionStatus status);
    
    Page<RpaExecution> findByStartTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    @Query("SELECT COUNT(e) FROM RpaExecution e WHERE e.status = 'RUNNING'")
    long countRunningExecutions();
    
    @Query("SELECT e FROM RpaExecution e WHERE e.status = 'SUCCESS' AND e.startTime >= :since")
    List<RpaExecution> findSuccessfulExecutionsSince(LocalDateTime since);
}
