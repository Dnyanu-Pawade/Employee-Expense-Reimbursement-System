package com.enterprise.expense.dto;
import lombok.*; @Data @AllArgsConstructor
public class JwtResponse {
    private String token; private String refreshToken;
    private Long id; private String username; private String email;
    private String fullName; private String role; private String department;
}
