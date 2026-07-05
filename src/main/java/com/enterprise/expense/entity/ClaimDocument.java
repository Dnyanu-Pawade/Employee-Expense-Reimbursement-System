package com.enterprise.expense.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity @Table(name = "claim_documents")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ClaimDocument {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claim_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","documents","approvalHistory","comments"})
    private ExpenseClaim claim;
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String filePath;
    private String fileType;
    private Long fileSize;
    private String extractedAmount;
    private String extractedDate;
    private String extractedVendor;
    @Column(updatable = false)
    private LocalDateTime uploadedAt;
    @PrePersist protected void onCreate() { uploadedAt = LocalDateTime.now(); }
}
