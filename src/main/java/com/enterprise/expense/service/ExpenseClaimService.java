package com.enterprise.expense.service;
import com.enterprise.expense.dto.*;
import com.enterprise.expense.entity.*;
import com.enterprise.expense.enums.*;
import com.enterprise.expense.repository.*;
import com.enterprise.expense.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
@Service @RequiredArgsConstructor
public class ExpenseClaimService {
    private final ExpenseClaimRepository claimRepo;
    private final UserRepository userRepo;
    private final ClaimDocumentRepository docRepo;
    private final ApprovalHistoryRepository approvalRepo;
    private final SlaTrackingRepository slaRepo;
    private final NotificationService notificationService;
    private final EmailService emailService;
    private final AuditLogService auditLogService;
    private static final String UPLOAD_DIR = "uploads/";

    private User currentUser() {
        UserDetailsImpl d = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepo.findById(d.getId()).orElseThrow();
    }

    public ExpenseClaim submit(ExpenseClaimRequest req) {
        User emp = currentUser();
        ExpenseClaim claim = ExpenseClaim.builder()
                .claimNumber("EXP-" + System.currentTimeMillis())
                .employee(emp).title(req.getTitle()).category(req.getCategory())
                .amount(req.getAmount()).expenseDate(req.getExpenseDate())
                .description(req.getDescription()).vendorName(req.getVendorName())
                .gstNumber(req.getGstNumber()).projectCode(req.getProjectCode())
                .urgent(req.isUrgent()).status(ClaimStatus.SUBMITTED).build();
        claimRepo.save(claim);

        // SLA tracking
        LocalDateTime now = LocalDateTime.now();
        slaRepo.save(SlaTracking.builder().claim(claim).submittedAt(now)
                .teamLeadDeadline(now.plusHours(48)).managerDeadline(now.plusHours(96))
                .financeDeadline(now.plusHours(144)).build());

        // Notify team leads
        userRepo.findByRole(Role.ROLE_TEAM_LEAD).forEach(tl -> {
            notificationService.send(tl, "New Claim Pending", emp.getFullName() + " submitted: " + claim.getTitle(), "CLAIM");
            emailService.sendClaimSubmitted(tl.getEmail(), tl.getFullName(), claim.getClaimNumber(), claim.getTitle());
        });
        emailService.sendClaimSubmitted(emp.getEmail(), emp.getFullName(), claim.getClaimNumber(), claim.getTitle());
        auditLogService.log(emp.getUsername(), "SUBMIT_CLAIM", "ExpenseClaim", claim.getClaimNumber(), "Amount: " + claim.getAmount());
        return claim;
    }

    public ExpenseClaim teamLeadApprove(Long id, ReviewRequest req) {
        User tl = currentUser();
        ExpenseClaim claim = getById(id);
        if (claim.getStatus() != ClaimStatus.SUBMITTED) throw new RuntimeException("Claim not in SUBMITTED status");
        claim.setStatus(ClaimStatus.TEAM_LEAD_APPROVED);
        claimRepo.save(claim);
        saveApprovalHistory(claim, tl, ApprovalAction.APPROVED, req.getComment(), "TEAM_LEAD");
        slaRepo.findByClaimId(id).ifPresent(s -> { s.setTeamLeadActualAt(LocalDateTime.now()); slaRepo.save(s); });

        // Notify managers
        userRepo.findByRole(Role.ROLE_MANAGER).forEach(m -> {
            notificationService.send(m, "Claim Ready for Manager Review", claim.getTitle() + " approved by Team Lead", "CLAIM");
        });
        notificationService.send(claim.getEmployee(), "Claim Approved by Team Lead", "Your claim '" + claim.getTitle() + "' approved by Team Lead", "CLAIM");
        emailService.sendClaimApproved(claim.getEmployee().getEmail(), claim.getEmployee().getFullName(), claim.getClaimNumber(), claim.getTitle(), "Team Lead");
        auditLogService.log(tl.getUsername(), "TEAM_LEAD_APPROVE", "ExpenseClaim", claim.getClaimNumber(), req.getComment());
        return claim;
    }

    public ExpenseClaim teamLeadReject(Long id, ReviewRequest req) {
        User tl = currentUser();
        ExpenseClaim claim = getById(id);
        claim.setStatus(ClaimStatus.TEAM_LEAD_REJECTED);
        claimRepo.save(claim);
        saveApprovalHistory(claim, tl, ApprovalAction.REJECTED, req.getComment(), "TEAM_LEAD");
        notificationService.send(claim.getEmployee(), "Claim Rejected by Team Lead", "Reason: " + req.getComment(), "CLAIM");
        emailService.sendClaimRejected(claim.getEmployee().getEmail(), claim.getEmployee().getFullName(), claim.getClaimNumber(), claim.getTitle(), req.getComment());
        auditLogService.log(tl.getUsername(), "TEAM_LEAD_REJECT", "ExpenseClaim", claim.getClaimNumber(), req.getComment());
        return claim;
    }

    public ExpenseClaim managerApprove(Long id, ReviewRequest req) {
        User manager = currentUser();
        ExpenseClaim claim = getById(id);
        if (claim.getStatus() != ClaimStatus.TEAM_LEAD_APPROVED) throw new RuntimeException("Claim not in TEAM_LEAD_APPROVED status");
        claim.setStatus(ClaimStatus.MANAGER_APPROVED);
        claimRepo.save(claim);
        saveApprovalHistory(claim, manager, ApprovalAction.APPROVED, req.getComment(), "MANAGER");
        slaRepo.findByClaimId(id).ifPresent(s -> { s.setManagerActualAt(LocalDateTime.now()); slaRepo.save(s); });

        userRepo.findByRole(Role.ROLE_FINANCE).forEach(f ->
            notificationService.send(f, "Claim Ready for Finance", claim.getTitle() + " approved by Manager", "CLAIM"));
        notificationService.send(claim.getEmployee(), "Claim Approved by Manager", "Your claim '" + claim.getTitle() + "' approved by Manager", "CLAIM");
        emailService.sendClaimApproved(claim.getEmployee().getEmail(), claim.getEmployee().getFullName(), claim.getClaimNumber(), claim.getTitle(), "Manager");
        auditLogService.log(manager.getUsername(), "MANAGER_APPROVE", "ExpenseClaim", claim.getClaimNumber(), req.getComment());
        return claim;
    }

    public ExpenseClaim managerReject(Long id, ReviewRequest req) {
        User manager = currentUser();
        ExpenseClaim claim = getById(id);
        claim.setStatus(ClaimStatus.MANAGER_REJECTED);
        claimRepo.save(claim);
        saveApprovalHistory(claim, manager, ApprovalAction.REJECTED, req.getComment(), "MANAGER");
        notificationService.send(claim.getEmployee(), "Claim Rejected by Manager", "Reason: " + req.getComment(), "CLAIM");
        emailService.sendClaimRejected(claim.getEmployee().getEmail(), claim.getEmployee().getFullName(), claim.getClaimNumber(), claim.getTitle(), req.getComment());
        auditLogService.log(manager.getUsername(), "MANAGER_REJECT", "ExpenseClaim", claim.getClaimNumber(), req.getComment());
        return claim;
    }

    public ExpenseClaim financeVerify(Long id, ReviewRequest req) {
        User finance = currentUser();
        ExpenseClaim claim = getById(id);
        if (claim.getStatus() != ClaimStatus.MANAGER_APPROVED) throw new RuntimeException("Claim not in MANAGER_APPROVED status");
        claim.setStatus(ClaimStatus.FINANCE_VERIFIED);
        claimRepo.save(claim);
        saveApprovalHistory(claim, finance, ApprovalAction.APPROVED, req.getComment(), "FINANCE");
        slaRepo.findByClaimId(id).ifPresent(s -> { s.setFinanceActualAt(LocalDateTime.now()); slaRepo.save(s); });
        notificationService.send(claim.getEmployee(), "Claim Verified by Finance", "Payment will be processed soon", "PAYMENT");
        auditLogService.log(finance.getUsername(), "FINANCE_VERIFY", "ExpenseClaim", claim.getClaimNumber(), req.getComment());
        return claim;
    }

    public ExpenseClaim financeReject(Long id, ReviewRequest req) {
        User finance = currentUser();
        ExpenseClaim claim = getById(id);
        claim.setStatus(ClaimStatus.FINANCE_REJECTED);
        claimRepo.save(claim);
        saveApprovalHistory(claim, finance, ApprovalAction.REJECTED, req.getComment(), "FINANCE");
        notificationService.send(claim.getEmployee(), "Claim Rejected by Finance", "Reason: " + req.getComment(), "CLAIM");
        emailService.sendClaimRejected(claim.getEmployee().getEmail(), claim.getEmployee().getFullName(), claim.getClaimNumber(), claim.getTitle(), req.getComment());
        auditLogService.log(finance.getUsername(), "FINANCE_REJECT", "ExpenseClaim", claim.getClaimNumber(), req.getComment());
        return claim;
    }

    public ClaimDocument uploadDocument(Long claimId, MultipartFile file) throws IOException {
        ExpenseClaim claim = getById(claimId);
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return docRepo.save(ClaimDocument.builder().claim(claim).fileName(file.getOriginalFilename())
                .filePath(filePath.toString()).fileType(file.getContentType()).fileSize(file.getSize()).build());
    }

    private void saveApprovalHistory(ExpenseClaim claim, User approver, ApprovalAction action, String comment, String level) {
        approvalRepo.save(ApprovalHistory.builder().claim(claim).approver(approver)
                .approverRole(approver.getRole().name()).action(action).comment(comment).level(level).build());
    }

    public List<ExpenseClaim> getMyClaims() { return claimRepo.findByEmployeeOrderByCreatedAtDesc(currentUser()); }
    public ExpenseClaim getById(Long id) { return claimRepo.findById(id).orElseThrow(() -> new RuntimeException("Claim not found")); }
    public List<ExpenseClaim> getPendingForTeamLead() { return claimRepo.findByStatusOrderByCreatedAtDesc(ClaimStatus.SUBMITTED); }
    public List<ExpenseClaim> getPendingForManager() { return claimRepo.findByStatusOrderByCreatedAtDesc(ClaimStatus.TEAM_LEAD_APPROVED); }
    public List<ExpenseClaim> getPendingForFinance() { return claimRepo.findByStatusOrderByCreatedAtDesc(ClaimStatus.MANAGER_APPROVED); }
    public List<ExpenseClaim> getVerifiedForPayment() { return claimRepo.findByStatusOrderByCreatedAtDesc(ClaimStatus.FINANCE_VERIFIED); }
    public List<ExpenseClaim> getAll() { return claimRepo.findAll(); }
    public Page<ExpenseClaim> searchMyClaims(String status, String category, String keyword, int page, int size) {
        ClaimStatus cs = (status != null && !status.isEmpty()) ? ClaimStatus.valueOf(status) : null;
        return claimRepo.searchByEmployee(currentUser(), cs, category, keyword, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }
    public Page<ExpenseClaim> searchAll(String status, String category, String keyword, Long deptId, int page, int size) {
        ClaimStatus cs = (status != null && !status.isEmpty()) ? ClaimStatus.valueOf(status) : null;
        return claimRepo.searchAll(cs, category, keyword, deptId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt")));
    }
}
