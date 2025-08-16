package com.vhskillpro.backend.utils.jwt.claims;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationTokenExtraClaims implements TokenExtraClaims {
  private String email;

  @Override
  public Map<String, Object> toMap() {
    Map<String, Object> claims = new HashMap<>();
    claims.put("email", email);
    return claims;
  }
}
