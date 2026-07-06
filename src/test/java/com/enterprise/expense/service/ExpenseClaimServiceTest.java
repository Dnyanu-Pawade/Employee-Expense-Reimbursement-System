package com.enterprise.expense.service;

import com.enterprise.expense.entity.ExpenseClaim;
import com.enterprise.expense.entity.User;
import com.enterprise.expense.enums.ClaimStatus;
import com.enterprise.expense.enums.ExpenseCategory;
import com.enterprise.expense.enums.Role;
import com.enterprise.expense.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseClaimServiceTest {

    @Mock ExpenseClaimRepository claimRepo;
    @Mock UserRepository userRepo;
    @Mock ClaimDocumentRepository docRepo;
    @Mock ApprovalHistoryRepository approvalRepo;
    @Mock SlaTrackingRepository slaRepo;
    @Mock NotificationService notificationService;
    @Mock EmailService emailService;
    @Mock AuditLogService auditLogService;
    @InjectMocks ExpenseClaimService expenseClaimService;

    private User employee;
    private ExpenseClaim claim;

    @BeforeEach
    void setUp() {
        employee = User.builder()
                .id(1L).username("employee1").email("emp@test.com")
                .fullName("Test Employee").role(Role.ROLE_EMPLOYEE).active(true).build();

        claim = ExpenseClaim.builder()
                .id(1L).claimNumber("EXP-001").title("Travel Expense")
                .amount(new BigDecimal("1500.00")).category(ExpenseCategory.TRAVEL)
                .status(ClaimStatus.SUBMITTED).employee(employee).build();
    }

    @Test
    void getById_ValidId_ReturnsClaim() {
        when(claimRepo.findById(1L)).thenReturn(Optional.of(claim));

        ExpenseClaim result = expenseClaimService.getById(1L);
        assertEquals("Travel Expense", result.getTitle());
        assertEquals(ClaimStatus.SUBMITTED, result.getStatus());
    }

    @Test
    void getById_InvalidId_ThrowsException() {
        when(claimRepo.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> expenseClaimService.getById(99L));
        assertEquals("Claim not found", ex.getMessage());
    }

    @Test
    void getPendingForTeamLead_ReturnsSubmittedClaims() {
        when(claimRepo.findByStatusOrderByCreatedAtDesc(ClaimStatus.SUBMITTED))
                .thenReturn(java.util.List.of(claim));

        var result = expenseClaimService.getPendingForTeamLead();
        assertEquals(1, result.size());
        assertEquals("EXP-001", result.get(0).getClaimNumber());
    }

    @Test
    void getPendingForManager_ReturnsTeamLeadApprovedClaims() {
        claim.setStatus(ClaimStatus.TEAM_LEAD_APPROVED);
        when(claimRepo.findByStatusOrderByCreatedAtDesc(ClaimStatus.TEAM_LEAD_APPROVED))
                .thenReturn(java.util.List.of(claim));

        var result = expenseClaimService.getPendingForManager();
        assertEquals(1, result.size());
        assertEquals(ClaimStatus.TEAM_LEAD_APPROVED, result.get(0).getStatus());
    }
}
