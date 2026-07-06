package com.enterprise.expense.service;

import com.enterprise.expense.dto.LoginRequest;
import com.enterprise.expense.dto.RegisterRequest;
import com.enterprise.expense.entity.User;
import com.enterprise.expense.enums.Role;
import com.enterprise.expense.repository.DepartmentRepository;
import com.enterprise.expense.repository.RefreshTokenRepository;
import com.enterprise.expense.repository.UserRepository;
import com.enterprise.expense.security.JwtTokenProvider;
import com.enterprise.expense.security.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock DepartmentRepository departmentRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AuthenticationManager authenticationManager;
    @Mock JwtTokenProvider jwtTokenProvider;
    @Mock EmailService emailService;
    @Mock AuditLogService auditLogService;
    @InjectMocks AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L).username("employee1").email("employee1@test.com")
                .fullName("Test Employee").role(Role.ROLE_EMPLOYEE).active(true).build();
    }

    @Test
    void register_Success() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser"); req.setEmail("new@test.com");
        req.setFullName("New User"); req.setPassword("Password@123");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(testUser);

        String result = authService.register(req);
        assertEquals("Employee registered successfully", result);
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("employee1"); req.setEmail("new@test.com");

        when(userRepository.existsByUsername("employee1")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(req));
        assertEquals("Username already taken", ex.getMessage());
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser"); req.setEmail("employee1@test.com");

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("employee1@test.com")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.register(req));
        assertEquals("Email already registered", ex.getMessage());
    }

    @Test
    void forgotPassword_InvalidEmail_ThrowsException() {
        when(userRepository.findByEmail("wrong@test.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> authService.forgotPassword("wrong@test.com"));
        assertEquals("No account found with this email", ex.getMessage());
    }

    @Test
    void forgotPassword_ValidEmail_ReturnsToken() {
        when(userRepository.findByEmail("employee1@test.com")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any())).thenReturn(testUser);
        doNothing().when(emailService).sendPasswordReset(any(), any());

        String result = authService.forgotPassword("employee1@test.com");
        assertTrue(result.startsWith("RESET_TOKEN:"));
    }
}
