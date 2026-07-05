package com.enterprise.expense.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Table(name = "expense_categories")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ExpenseCategoryConfig {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String name;
    private String description;
    private BigDecimal maxLimit;
    private boolean requiresReceipt = true;
    private boolean active = true;
}
