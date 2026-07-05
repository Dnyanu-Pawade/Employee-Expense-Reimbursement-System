package com.enterprise.expense.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name = "payments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","documents","approvalHistory","comments"})
    private ExpenseClaim claim;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","password","resetToken"})
    private User processedBy;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    private String paymentReference;
    private String bankTransferRef;
    private String paymentMode;
    private String notes;
    @Column(updatable = false)
    private LocalDateTime processedAt;
    @PrePersist protected void onCreate() { processedAt = LocalDateTime.now(); }
}
