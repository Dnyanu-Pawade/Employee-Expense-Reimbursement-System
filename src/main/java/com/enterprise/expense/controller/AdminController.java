package com.enterprise.expense.controller;
import com.enterprise.expense.entity.*;
import com.enterprise.expense.enums.*;
import com.enterprise.expense.repository.*;
import com.enterprise.expense.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;
@RestController @RequestMapping("/api/admin") @RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final ExpenseClaimService expenseService;
    private final AuditLogService auditLogService;
    private final DepartmentRepository deptRepo;
    private final ExpenseClaimRepository claimRepo;
    private final PaymentRepository paymentRepo;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String,Object>> getDashboard() {
        Map<String,Object> stats = new HashMap<>();
        stats.put("totalUsers", userService.getAll().size());
        stats.put("totalClaims", claimRepo.count());
        stats.put("pendingClaims", claimRepo.countByStatus(ClaimStatus.SUBMITTED));
        stats.put("managerPending", claimRepo.countByStatus(ClaimStatus.TEAM_LEAD_APPROVED));
        stats.put("financePending", claimRepo.countByStatus(ClaimStatus.MANAGER_APPROVED));
        stats.put("processedClaims", claimRepo.countByStatus(ClaimStatus.PAYMENT_PROCESSED));
        stats.put("totalPaid", claimRepo.sumAmountByStatus(ClaimStatus.PAYMENT_PROCESSED));
        stats.put("escalated", claimRepo.countByStatus(ClaimStatus.ESCALATED));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users") public ResponseEntity<List<User>> getUsers() { return ResponseEntity.ok(userService.getAll()); }
    @PatchMapping("/users/{id}/role") public ResponseEntity<User> updateRole(@PathVariable Long id, @RequestParam Role role) { return ResponseEntity.ok(userService.updateRole(id, role)); }
    @PatchMapping("/users/{id}/toggle") public ResponseEntity<User> toggleStatus(@PathVariable Long id) { return ResponseEntity.ok(userService.toggleStatus(id)); }
    @PatchMapping("/users/{id}/manager") public ResponseEntity<User> assignManager(@PathVariable Long id, @RequestParam Long managerId) { return ResponseEntity.ok(userService.assignManager(id, managerId)); }

    @GetMapping("/claims") public ResponseEntity<List<ExpenseClaim>> getAllClaims() { return ResponseEntity.ok(expenseService.getAll()); }
    @GetMapping("/claims/search") public ResponseEntity<Page<ExpenseClaim>> searchClaims(@RequestParam(required=false) String status, @RequestParam(required=false) String category, @RequestParam(required=false) String keyword, @RequestParam(required=false) Long deptId, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) { return ResponseEntity.ok(expenseService.searchAll(status, category, keyword, deptId, page, size)); }

    @GetMapping("/departments") public ResponseEntity<List<Department>> getDepts() { return ResponseEntity.ok(deptRepo.findAll()); }
    @PostMapping("/departments") public ResponseEntity<Department> createDept(@RequestBody Department dept) { return ResponseEntity.ok(deptRepo.save(dept)); }
    @PutMapping("/departments/{id}") public ResponseEntity<Department> updateDept(@PathVariable Long id, @RequestBody Department dept) { dept.setId(id); return ResponseEntity.ok(deptRepo.save(dept)); }

    @GetMapping("/audit-logs") public ResponseEntity<List<AuditLog>> getAuditLogs(@RequestParam(defaultValue="100") int limit) { return ResponseEntity.ok(auditLogService.getRecent(limit)); }

    @GetMapping("/charts/category")
    public ResponseEntity<Map<String,Object>> getCategoryChart() {
        List<Object[]> raw = claimRepo.getCategoryStats();
        List<String> labels = new ArrayList<>(); List<Double> values = new ArrayList<>();
        raw.forEach(r -> { labels.add(r[0].toString()); values.add(((Number)r[2]).doubleValue()); });
        return ResponseEntity.ok(Map.of("labels", labels, "values", values));
    }

    @GetMapping("/charts/status")
    public ResponseEntity<Map<String,Object>> getStatusChart() {
        List<String> labels = Arrays.stream(ClaimStatus.values()).map(Enum::name).toList();
        List<Long> values = Arrays.stream(ClaimStatus.values()).map(claimRepo::countByStatus).toList();
        return ResponseEntity.ok(Map.of("labels", labels, "values", values));
    }

    @GetMapping("/charts/monthly")
    public ResponseEntity<Map<String,Object>> getMonthlyChart() {
        int year = LocalDateTime.now().getYear();
        List<Object[]> raw = claimRepo.getMonthlyStats(year);
        Map<Integer,Double> monthMap = new LinkedHashMap<>();
        for (int i=1;i<=12;i++) monthMap.put(i, 0.0);
        raw.forEach(r -> monthMap.put(((Number)r[0]).intValue(), ((Number)r[1]).doubleValue()));
        String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
        return ResponseEntity.ok(Map.of("labels", Arrays.asList(months), "values", new ArrayList<>(monthMap.values())));
    }
}
