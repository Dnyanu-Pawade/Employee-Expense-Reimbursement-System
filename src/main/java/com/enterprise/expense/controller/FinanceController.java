package com.enterprise.expense.controller;
import com.enterprise.expense.dto.ClaimSummaryDTO;
import com.enterprise.expense.dto.ReviewRequest;
import com.enterprise.expense.entity.*;
import com.enterprise.expense.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;
@RestController @RequestMapping("/api/finance") @RequiredArgsConstructor
public class FinanceController {
    private final ExpenseClaimService expenseService;
    private final PaymentService paymentService;
    @GetMapping("/pending") public ResponseEntity<List<ClaimSummaryDTO>> getPending() { return ResponseEntity.ok(expenseService.getPendingForFinance().stream().map(ClaimSummaryDTO::from).collect(Collectors.toList())); }
    @GetMapping("/verified") public ResponseEntity<List<ClaimSummaryDTO>> getVerified() { return ResponseEntity.ok(expenseService.getVerifiedForPayment().stream().map(ClaimSummaryDTO::from).collect(Collectors.toList())); }
    @PutMapping("/verify/{id}") public ResponseEntity<ClaimSummaryDTO> verify(@PathVariable Long id, @RequestBody ReviewRequest req) { return ResponseEntity.ok(ClaimSummaryDTO.from(expenseService.financeVerify(id, req))); }
    @PutMapping("/reject/{id}") public ResponseEntity<ClaimSummaryDTO> reject(@PathVariable Long id, @RequestBody ReviewRequest req) { return ResponseEntity.ok(ClaimSummaryDTO.from(expenseService.financeReject(id, req))); }
    @PostMapping("/payment/{id}") public ResponseEntity<Payment> processPayment(@PathVariable Long id, @RequestBody Map<String,String> body) { return ResponseEntity.ok(paymentService.processPayment(id, body.get("paymentReference"), body.get("bankTransferRef"), body.get("paymentMode"), body.get("notes"))); }
    @GetMapping("/payments") public ResponseEntity<List<Payment>> getAllPayments() { return ResponseEntity.ok(paymentService.getAllPayments()); }
}
