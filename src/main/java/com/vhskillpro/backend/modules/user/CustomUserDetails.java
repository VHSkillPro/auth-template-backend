package com.vhskillpro.backend.modules.user;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Data
@Builder
public class CustomUserDetails implements UserDetails {
  private Long id;
  private String email;
  private String password;
  private boolean enabled;
  private boolean locked;
  private boolean superuser;
  private List<GrantedAuthority> authorities;

  public String getUsername() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public List<GrantedAuthority> getAuthorities() {
    if (superuser) {
      return List.of(new SimpleGrantedAuthority("all:all"));
    }
    return authorities;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public boolean isAccountNonLocked() {
    return !locked;
  }
}
