package com.enterprise.expense.security;
import com.enterprise.expense.entity.User;
import lombok.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
@AllArgsConstructor @Getter
public class UserDetailsImpl implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String role;
    private Collection<? extends GrantedAuthority> authorities;
    public static UserDetailsImpl build(User user) {
        return new UserDetailsImpl(user.getId(), user.getUsername(), user.getPassword(),
                user.getEmail(), user.getFullName(), user.getRole().name(),
                List.of(new SimpleGrantedAuthority(user.getRole().name())));
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
