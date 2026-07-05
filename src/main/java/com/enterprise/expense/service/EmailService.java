package com.enterprise.expense.service;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service @RequiredArgsConstructor @Slf4j
public class EmailService {
    private final JavaMailSender mailSender;
    public void send(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to); msg.setSubject(subject); msg.setText(body); msg.setFrom("noreply@enterprise-expense.com");
            mailSender.send(msg); log.info("Email sent to {}", to);
        } catch (Exception e) { log.error("Email failed to {}: {}", to, e.getMessage()); }
    }
    public void sendClaimSubmitted(String to, String name, String claimNo, String title) {
        send(to, "Expense Claim Submitted - " + claimNo,
            "Dear " + name + ",\n\nYour expense claim '" + title + "' ("+claimNo+") has been submitted successfully.\n\nIt is now pending approval from your Team Lead.\n\nEnterprise Expense Platform");
    }
    public void sendClaimApproved(String to, String name, String claimNo, String title, String approverRole) {
        send(to, "Claim Approved by " + approverRole + " - " + claimNo,
            "Dear " + name + ",\n\nYour claim '" + title + "' has been approved by " + approverRole + ".\n\nEnterprise Expense Platform");
    }
    public void sendClaimRejected(String to, String name, String claimNo, String title, String reason) {
        send(to, "Claim Rejected - " + claimNo,
            "Dear " + name + ",\n\nYour claim '" + title + "' has been rejected.\nReason: " + reason + "\n\nEnterprise Expense Platform");
    }
    public void sendPaymentProcessed(String to, String name, String claimNo, String amount) {
        send(to, "Payment Processed - " + claimNo,
            "Dear " + name + ",\n\nPayment of Rs." + amount + " for claim " + claimNo + " has been processed.\n\nEnterprise Expense Platform");
    }
    public void sendEscalationAlert(String to, String managerName, String claimNo) {
        send(to, "SLA Breach - Claim Escalated - " + claimNo,
            "Dear " + managerName + ",\n\nClaim " + claimNo + " has been escalated due to SLA breach.\nPlease review immediately.\n\nEnterprise Expense Platform");
    }
    public void sendPasswordReset(String to, String token) {
        send(to, "Password Reset - Enterprise Expense Platform",
            "Your password reset token: " + token + "\n\nExpires in 1 hour.\n\nEnterprise Expense Platform");
    }
}
