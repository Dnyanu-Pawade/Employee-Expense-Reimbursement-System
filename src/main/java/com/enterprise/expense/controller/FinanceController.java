package com.enterprise.expense.controller;
import com.enterprise.expense.dto.ReviewRequest;
import com.enterprise.expense.entity.*;
import com.enterprise.expense.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/finance") @RequiredArgsConstructor
public class FinanceController {
    private final ExpenseClaimService expenseService;
    private final PaymentService paymentService;
    @GetMapping("/pending") public ResponseEntity<List<ExpenseClaim>> getPending() { return ResponseEntity.ok(expenseService.getPendingForFinance()); }
    @GetMapping("/verified") public ResponseEntity<List<ExpenseClaim>> getVerified() { return ResponseEntity.ok(expenseService.getVerifiedForPayment()); }
    @PutMapping("/verify/{id}") public ResponseEntity<ExpenseClaim> verify(@PathVariable Long id, @RequestBody ReviewRequest req) { return ResponseEntity.ok(expenseService.financeVerify(id, req)); }
    @PutMapping("/reject/{id}") public ResponseEntity<ExpenseClaim> reject(@PathVariable Long id, @RequestBody ReviewRequest req) { return ResponseEntity.ok(expenseService.financeReject(id, req)); }
    @PostMapping("/payment/{id}") public ResponseEntity<Payment> processPayment(@PathVariable Long id, @RequestBody Map<String,String> body) {
        return ResponseEntity.ok(paymentService.processPayment(id, body.get("paymentReference"), body.get("bankTransferRef"), body.get("paymentMode"), body.get("notes")));
    }
    @GetMapping("/payments") public ResponseEntity<List<Payment>> getAllPayments() { return ResponseEntity.ok(paymentService.getAllPayments()); }
}
