package com.enterprise.expense.config;
import com.enterprise.expense.entity.*;
import com.enterprise.expense.enums.Role;
import com.enterprise.expense.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
@Component @RequiredArgsConstructor @Slf4j
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepo;
    private final DepartmentRepository deptRepo;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) {
        Department it = createDept("Information Technology", "IT");
        Department hr = createDept("Human Resources", "HR");
        Department fin = createDept("Finance", "FIN");
        Department ops = createDept("Operations", "OPS");
        createUser("admin", "Admin@123", "admin@enterprise.com", "System Admin", Role.ROLE_ADMIN, it);
        createUser("Dnyaneshwar", "Dnyaneshwar@123", "dnyaneshwar@enterprise.com", "Dnyaneshwar Pawade", Role.ROLE_ADMIN, it);
        createUser("manager1", "Manager@123", "manager@enterprise.com", "Rajesh Manager", Role.ROLE_MANAGER, it);
        createUser("teamlead1", "TeamLead@123", "teamlead@enterprise.com", "Priya Team Lead", Role.ROLE_TEAM_LEAD, it);
        createUser("finance1", "Finance@123", "finance@enterprise.com", "Suresh Finance", Role.ROLE_FINANCE, fin);
        createUser("auditor1", "Auditor@123", "auditor@enterprise.com", "Audit User", Role.ROLE_AUDITOR, fin);
        createUser("employee1", "Employee@123", "employee@enterprise.com", "Rahul Employee", Role.ROLE_EMPLOYEE, it);
        createUser("employee2", "Employee@123", "employee2@enterprise.com", "Sneha Employee", Role.ROLE_EMPLOYEE, hr);
        log.info("✅ Enterprise Expense Platform initialized with default users");
    }
    private Department createDept(String name, String code) {
        return deptRepo.findByName(name).orElseGet(() -> deptRepo.save(Department.builder().name(name).code(code).active(true).build()));
    }
    private void createUser(String username, String password, String email, String fullName, Role role, Department dept) {
        if (!userRepo.existsByUsername(username))
            userRepo.save(User.builder().username(username).password(passwordEncoder.encode(password))
                .email(email).fullName(fullName).role(role).department(dept).active(true).build());
    }
}
