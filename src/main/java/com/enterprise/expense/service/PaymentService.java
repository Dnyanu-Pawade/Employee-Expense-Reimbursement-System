package com.enterprise.expense.service;
import com.enterprise.expense.entity.*;
import com.enterprise.expense.enums.ClaimStatus;
import com.enterprise.expense.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
@Service @RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepo;
    private final ExpenseClaimRepository claimRepo;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final AuditLogService auditLogService;
    private final UserService userService;

    public Payment processPayment(Long claimId, String paymentRef, String bankRef, String mode, String notes) {
        User finance = userService.currentUser();
        ExpenseClaim claim = claimRepo.findById(claimId).orElseThrow(() -> new RuntimeException("Claim not found"));
        if (claim.getStatus() != ClaimStatus.FINANCE_VERIFIED) throw new RuntimeException("Claim not verified by finance");
        claim.setStatus(ClaimStatus.PAYMENT_PROCESSED);
        claimRepo.save(claim);
        Payment payment = Payment.builder().claim(claim).processedBy(finance)
                .amount(claim.getAmount()).paymentReference(paymentRef)
                .bankTransferRef(bankRef).paymentMode(mode).notes(notes).build();
        paymentRepo.save(payment);
        notificationService.send(claim.getEmployee(), "Payment Processed",
                "Rs." + claim.getAmount() + " for claim " + claim.getClaimNumber() + " has been processed", "PAYMENT");
        emailService.sendPaymentProcessed(claim.getEmployee().getEmail(), claim.getEmployee().getFullName(),
                claim.getClaimNumber(), claim.getAmount().toString());
        auditLogService.log(finance.getUsername(), "PROCESS_PAYMENT", "Payment", claim.getClaimNumber(), "Rs." + claim.getAmount());
        return payment;
    }

    public List<Payment> getAllPayments() { return paymentRepo.findAll(); }
}
