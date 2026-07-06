package com.enterprise.expense.controller;
import com.enterprise.expense.dto.ClaimSummaryDTO;
import com.enterprise.expense.dto.ReviewRequest;
import com.enterprise.expense.service.ExpenseClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
@RestController @RequestMapping("/api/team-lead") @RequiredArgsConstructor
public class TeamLeadController {
    private final ExpenseClaimService service;
    @GetMapping("/pending") public ResponseEntity<List<ClaimSummaryDTO>> getPending() { return ResponseEntity.ok(service.getPendingForTeamLead().stream().map(ClaimSummaryDTO::from).collect(Collectors.toList())); }
    @PutMapping("/approve/{id}") public ResponseEntity<ClaimSummaryDTO> approve(@PathVariable Long id, @RequestBody ReviewRequest req) { return ResponseEntity.ok(ClaimSummaryDTO.from(service.teamLeadApprove(id, req))); }
    @PutMapping("/reject/{id}") public ResponseEntity<ClaimSummaryDTO> reject(@PathVariable Long id, @RequestBody ReviewRequest req) { return ResponseEntity.ok(ClaimSummaryDTO.from(service.teamLeadReject(id, req))); }
}
