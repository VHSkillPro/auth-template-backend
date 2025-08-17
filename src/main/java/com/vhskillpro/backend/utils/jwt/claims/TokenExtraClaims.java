package com.vhskillpro.backend.utils.jwt.claims;

import java.util.Map;

public interface TokenExtraClaims {
  /**
   * Converts the extra claims to a map representation.
   *
   * @return a map containing the extra claims as key-value pairs
   */
  public Map<String, Object> toMap();
}
