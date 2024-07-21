package org.yuri.userauth.domain.dto;

import jakarta.validation.constraints.NotEmpty;
import org.yuri.userauth.domain.user.UserRole;

public record RegisterDTO(@NotEmpty(message = "{required.login}") String login,
                          @NotEmpty(message = "{required.password}") String password, UserRole role) { }
