package com.enterprise.expense.entity;

import com.enterprise.expense.enums.ApprovalAction;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "approval_history")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ApprovalHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","approvalHistory","documents","comments"})
    private ExpenseClaim claim;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","password","resetToken"})
    private User approver;
    private String approverRole;
    @Enumerated(EnumType.STRING)
    private ApprovalAction action;
    @Column(length = 1000)
    private String comment;
    private String level;
    @Column(updatable = false)
    private LocalDateTime actionAt;
    @PrePersist protected void onCreate() { actionAt = LocalDateTime.now(); }
}
