package com.enterprise.expense.scheduler;
import com.enterprise.expense.entity.*;
import com.enterprise.expense.enums.ClaimStatus;
import com.enterprise.expense.repository.*;
import com.enterprise.expense.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
@Component @RequiredArgsConstructor @Slf4j
public class SlaScheduler {
    private final SlaTrackingRepository slaRepo;
    private final ExpenseClaimRepository claimRepo;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Scheduled(fixedRate = 3600000) // every hour
    public void checkSlaBreaches() {
        LocalDateTime now = LocalDateTime.now();
        List<SlaTracking> breachedTL = slaRepo.findBreachedTeamLead(now);
        breachedTL.forEach(s -> {
            s.setTeamLeadBreached(true); s.setEscalated(true);
            slaRepo.save(s);
            ExpenseClaim claim = s.getClaim();
            claim.setStatus(ClaimStatus.ESCALATED);
            claimRepo.save(claim);
            userRepository.findByRole(com.enterprise.expense.enums.Role.ROLE_MANAGER).forEach(m -> {
                notificationService.send(m, "SLA Breach - Escalated", "Claim " + claim.getClaimNumber() + " escalated due to Team Lead inaction", "ESCALATION");
                emailService.sendEscalationAlert(m.getEmail(), m.getFullName(), claim.getClaimNumber());
            });
            auditLogService.log("SYSTEM", "SLA_BREACH", "ExpenseClaim", claim.getClaimNumber(), "Team Lead SLA breached");
            log.warn("SLA breach - claim {} escalated", claim.getClaimNumber());
        });

        List<SlaTracking> breachedMgr = slaRepo.findBreachedManager(now);
        breachedMgr.forEach(s -> {
            s.setManagerBreached(true); s.setEscalated(true);
            slaRepo.save(s);
            ExpenseClaim claim = s.getClaim();
            claim.setStatus(ClaimStatus.ESCALATED);
            claimRepo.save(claim);
            userRepository.findByRole(com.enterprise.expense.enums.Role.ROLE_ADMIN).forEach(a -> {
                notificationService.send(a, "SLA Breach - Manager", "Claim " + claim.getClaimNumber() + " escalated to Admin", "ESCALATION");
                emailService.sendEscalationAlert(a.getEmail(), a.getFullName(), claim.getClaimNumber());
            });
            auditLogService.log("SYSTEM", "SLA_BREACH", "ExpenseClaim", claim.getClaimNumber(), "Manager SLA breached");
        });
    }

    @Scheduled(cron = "0 0 9 1 * *") // 1st of every month at 9am
    public void sendMonthlyReminders() {
        log.info("Sending monthly expense reminders...");
        userRepository.findByRole(com.enterprise.expense.enums.Role.ROLE_EMPLOYEE).forEach(emp ->
            notificationService.send(emp, "Monthly Expense Reminder",
                    "Please submit your pending expense claims for this month.", "REMINDER"));
    }
}
