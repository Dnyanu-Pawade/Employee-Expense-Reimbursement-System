package com.enterprise.expense.repository;
import com.enterprise.expense.entity.SlaTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
public interface SlaTrackingRepository extends JpaRepository<SlaTracking, Long> {
    Optional<SlaTracking> findByClaimId(Long claimId);
    @Query("SELECT s FROM SlaTracking s WHERE s.teamLeadDeadline < :now AND s.teamLeadActualAt IS NULL AND s.escalated=false")
    List<SlaTracking> findBreachedTeamLead(@org.springframework.data.repository.query.Param("now") LocalDateTime now);
    @Query("SELECT s FROM SlaTracking s WHERE s.managerDeadline < :now AND s.managerActualAt IS NULL AND s.escalated=false")
    List<SlaTracking> findBreachedManager(@org.springframework.data.repository.query.Param("now") LocalDateTime now);
}
