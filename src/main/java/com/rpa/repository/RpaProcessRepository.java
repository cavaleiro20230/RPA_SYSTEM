package com.rpa.repository;

import com.rpa.entity.RpaProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RpaProcessRepository extends JpaRepository<RpaProcess, Long> {
    List<RpaProcess> findByStatus(RpaProcess.ProcessStatus status);
    
    @Query("SELECT p FROM RpaProcess p WHERE p.status = 'ACTIVE' AND p.cronExpression IS NOT NULL")
    List<RpaProcess> findActiveScheduledProcesses();
    
    List<RpaProcess> findByNameContainingIgnoreCase(String name);
}
