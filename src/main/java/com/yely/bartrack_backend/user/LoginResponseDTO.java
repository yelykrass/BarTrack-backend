package com.yely.bartrack_backend.user;

import java.util.List;

public record LoginResponseDTO(String username, List<String> roles, boolean active) {
}