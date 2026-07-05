package com.enterprise.expense.controller;
import com.enterprise.expense.dto.ReviewRequest;
import com.enterprise.expense.entity.ExpenseClaim;
import com.enterprise.expense.service.ExpenseClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/manager") @RequiredArgsConstructor
public class ManagerController {
    private final ExpenseClaimService service;
    @GetMapping("/pending") public ResponseEntity<List<ExpenseClaim>> getPending() { return ResponseEntity.ok(service.getPendingForManager()); }
    @PutMapping("/approve/{id}") public ResponseEntity<ExpenseClaim> approve(@PathVariable Long id, @RequestBody ReviewRequest req) { return ResponseEntity.ok(service.managerApprove(id, req)); }
    @PutMapping("/reject/{id}") public ResponseEntity<ExpenseClaim> reject(@PathVariable Long id, @RequestBody ReviewRequest req) { return ResponseEntity.ok(service.managerReject(id, req)); }
}
