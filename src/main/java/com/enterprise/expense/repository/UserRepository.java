package com.enterprise.expense.repository;
import com.enterprise.expense.entity.User;
import com.enterprise.expense.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByDepartmentId(Long departmentId);
    List<User> findByManagerId(Long managerId);
}
