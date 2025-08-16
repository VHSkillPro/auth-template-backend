package com.vhskillpro.backend.utils.jwt.claims;

import java.util.HashMap;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshTokenExtraClaims implements TokenExtraClaims {
  @Override
  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>();
    return map;
  }
}
