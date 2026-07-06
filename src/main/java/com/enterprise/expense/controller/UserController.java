package com.enterprise.expense.controller;
import com.enterprise.expense.dto.ApiResponse;
import com.enterprise.expense.entity.User;
import com.enterprise.expense.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController @RequestMapping("/api/users") @RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/me") public ResponseEntity<User> getProfile() { return ResponseEntity.ok(userService.currentUser()); }
    @PutMapping("/me") public ResponseEntity<User> updateProfile(@RequestBody User user) { return ResponseEntity.ok(userService.updateProfile(user)); }
    @PostMapping("/me/photo") public ResponseEntity<ApiResponse> uploadPhoto(@RequestParam("file") MultipartFile file) { return ResponseEntity.ok(new ApiResponse(true, userService.uploadProfilePhoto(file))); }
}
