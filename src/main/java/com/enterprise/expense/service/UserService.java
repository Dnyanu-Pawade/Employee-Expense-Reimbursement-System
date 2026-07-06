package com.enterprise.expense.service;
import com.enterprise.expense.entity.*;
import com.enterprise.expense.enums.Role;
import com.enterprise.expense.repository.*;
import com.enterprise.expense.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${file.upload-dir:uploads}") private String uploadDir;
    public User currentUser() {
        UserDetailsImpl d = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userRepository.findById(d.getId()).orElseThrow(() -> new RuntimeException("User not found"));
    }
    public List<User> getAll() { return userRepository.findAll(); }
    public User getById(Long id) { return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")); }
    public User updateRole(Long id, Role role) { User u = getById(id); u.setRole(role); return userRepository.save(u); }
    public User toggleStatus(Long id) { User u = getById(id); u.setActive(!u.isActive()); return userRepository.save(u); }
    public User updateProfile(User data) {
        User u = currentUser();
        u.setFullName(data.getFullName()); u.setPhone(data.getPhone());
        u.setDesignation(data.getDesignation());
        return userRepository.save(u);
    }
    public String uploadProfilePhoto(MultipartFile file) {
        try {
            String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".") ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")) : ".jpg";
            String filename = "profile_" + UUID.randomUUID() + ext;
            Path dir = Paths.get(uploadDir, "profiles");
            Files.createDirectories(dir);
            Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            User u = currentUser();
            u.setProfilePhoto("/uploads/profiles/" + filename);
            userRepository.save(u);
            return "/uploads/profiles/" + filename;
        } catch (IOException e) { throw new RuntimeException("Failed to upload photo"); }
    }
    public User assignManager(Long userId, Long managerId) {
        User u = getById(userId); User m = getById(managerId);
        u.setManager(m); return userRepository.save(u);
    }
    public List<User> getByRole(Role role) { return userRepository.findByRole(role); }
    public List<User> getByDepartment(Long deptId) { return userRepository.findByDepartmentId(deptId); }
}
