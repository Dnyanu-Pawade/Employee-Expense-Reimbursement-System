package com.enterprise.expense.controller;
import com.enterprise.expense.dto.*;
import com.enterprise.expense.entity.*;
import com.enterprise.expense.service.ExpenseClaimService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
@RestController @RequestMapping("/api/expenses") @RequiredArgsConstructor
public class ExpenseController {
    private final ExpenseClaimService service;
    @PostMapping public ResponseEntity<ExpenseClaim> submit(@Valid @RequestBody ExpenseClaimRequest req) { return ResponseEntity.ok(service.submit(req)); }
    @GetMapping public ResponseEntity<List<ExpenseClaim>> getMyClaims() { return ResponseEntity.ok(service.getMyClaims()); }
    @GetMapping("/{id}") public ResponseEntity<ExpenseClaim> getById(@PathVariable Long id) { return ResponseEntity.ok(service.getById(id)); }
    @PostMapping("/{id}/upload") public ResponseEntity<ClaimDocument> upload(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException { return ResponseEntity.ok(service.uploadDocument(id, file)); }
    @GetMapping("/search") public ResponseEntity<Page<ExpenseClaim>> search(@RequestParam(required=false) String status, @RequestParam(required=false) String category, @RequestParam(required=false) String keyword, @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) { return ResponseEntity.ok(service.searchMyClaims(status, category, keyword, page, size)); }
}
