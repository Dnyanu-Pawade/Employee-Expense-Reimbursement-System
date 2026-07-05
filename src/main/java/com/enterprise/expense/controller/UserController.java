package com.enterprise.expense.controller;
import com.enterprise.expense.entity.User;
import com.enterprise.expense.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/users") @RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/me") public ResponseEntity<User> getProfile() { return ResponseEntity.ok(userService.currentUser()); }
    @PutMapping("/me") public ResponseEntity<User> updateProfile(@RequestBody User user) { return ResponseEntity.ok(userService.updateProfile(user)); }
}
