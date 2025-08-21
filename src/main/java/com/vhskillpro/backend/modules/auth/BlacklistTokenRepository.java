package com.vhskillpro.backend.modules.auth;

import org.springframework.data.repository.CrudRepository;

public interface BlacklistTokenRepository extends CrudRepository<BlacklistToken, String> {}
