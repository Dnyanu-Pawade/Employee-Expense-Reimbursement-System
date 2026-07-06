package com.enterprise.expense.dto;

import com.enterprise.expense.enums.ClaimStatus;
import com.enterprise.expense.enums.ExpenseCategory;
import com.enterprise.expense.entity.ExpenseClaim;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ClaimSummaryDTO {
    private Long id;
    private String claimNumber;
    private String title;
    private ExpenseCategory category;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private ClaimStatus status;
    private boolean urgent;
    private LocalDateTime createdAt;
    private String employeeName;
    private String employeeId;

    public static ClaimSummaryDTO from(ExpenseClaim c) {
        ClaimSummaryDTO dto = new ClaimSummaryDTO();
        dto.setId(c.getId());
        dto.setClaimNumber(c.getClaimNumber());
        dto.setTitle(c.getTitle());
        dto.setCategory(c.getCategory());
        dto.setAmount(c.getAmount());
        dto.setExpenseDate(c.getExpenseDate());
        dto.setStatus(c.getStatus());
        dto.setUrgent(c.isUrgent());
        dto.setCreatedAt(c.getCreatedAt());
        if (c.getEmployee() != null) {
            dto.setEmployeeName(c.getEmployee().getFullName());
            dto.setEmployeeId(c.getEmployee().getEmployeeId());
        }
        return dto;
    }
}
