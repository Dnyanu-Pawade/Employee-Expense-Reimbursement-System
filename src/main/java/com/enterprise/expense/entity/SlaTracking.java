package com.enterprise.expense.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "sla_tracking")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class SlaTracking {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","documents","approvalHistory","comments"})
    private ExpenseClaim claim;
    private LocalDateTime submittedAt;
    private LocalDateTime teamLeadDeadline;
    private LocalDateTime managerDeadline;
    private LocalDateTime financeDeadline;
    private LocalDateTime teamLeadActualAt;
    private LocalDateTime managerActualAt;
    private LocalDateTime financeActualAt;
    private boolean teamLeadBreached = false;
    private boolean managerBreached = false;
    private boolean financeBreached = false;
    private boolean escalated = false;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); }
}
