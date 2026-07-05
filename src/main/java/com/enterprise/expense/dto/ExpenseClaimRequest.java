package com.enterprise.expense.dto;
import com.enterprise.expense.enums.ExpenseCategory;
import jakarta.validation.constraints.*; import lombok.Data;
import java.math.BigDecimal; import java.time.LocalDate;
@Data public class ExpenseClaimRequest {
    @NotBlank private String title;
    @NotNull private ExpenseCategory category;
    @NotNull @DecimalMin("0.01") private BigDecimal amount;
    @NotNull private LocalDate expenseDate;
    private String description; private String vendorName;
    private String gstNumber; private String projectCode;
    private boolean urgent = false;
}
