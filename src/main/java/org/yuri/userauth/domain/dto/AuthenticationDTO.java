package org.yuri.userauth.domain.dto;

import jakarta.validation.constraints.NotEmpty;

public record AuthenticationDTO(@NotEmpty(message = "{required.login}") String login, @NotEmpty(message = "{required.password}") String password) { }
