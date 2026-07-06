package com.enterprise.expense.entity;

import com.enterprise.expense.enums.ClaimStatus;
import com.enterprise.expense.enums.ExpenseCategory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity @Table(name = "expense_claims")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpenseClaim {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String claimNumber;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","password","resetToken","manager","resetTokenExpiry"})
    private User employee;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private ExpenseCategory category;
    @Column(nullable = false)
    private String title;
    @Column(length = 2000)
    private String description;
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
    @Column(nullable = false)
    private LocalDate expenseDate;
    private String vendorName;
    private String gstNumber;
    private String projectCode;
    @Enumerated(EnumType.STRING) @Column(nullable = false)
    @Builder.Default
    private ClaimStatus status = ClaimStatus.DRAFT;
    private boolean urgent = false;
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"claim"})
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<ClaimDocument> documents;
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<ApprovalHistory> approvalHistory;
    @OneToMany(mappedBy = "claim", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Comment> comments;
    @Column(updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @PrePersist protected void onCreate() { createdAt = LocalDateTime.now(); updatedAt = LocalDateTime.now(); }
    @PreUpdate protected void onUpdate() { updatedAt = LocalDateTime.now(); }
}
