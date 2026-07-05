package com.enterprise.expense.controller;
import com.enterprise.expense.entity.*;
import com.enterprise.expense.repository.CommentRepository;
import com.enterprise.expense.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/comments") @RequiredArgsConstructor
public class CommentController {
    private final CommentRepository commentRepo;
    private final ExpenseClaimService expenseService;
    private final UserService userService;
    @GetMapping("/{claimId}") public ResponseEntity<List<Comment>> get(@PathVariable Long claimId) {
        return ResponseEntity.ok(commentRepo.findByClaimOrderByCreatedAtAsc(expenseService.getById(claimId)));
    }
    @PostMapping("/{claimId}") public ResponseEntity<Comment> add(@PathVariable Long claimId, @RequestBody Map<String,String> body) {
        return ResponseEntity.ok(commentRepo.save(Comment.builder().claim(expenseService.getById(claimId)).user(userService.currentUser()).content(body.get("content")).build()));
    }
}
