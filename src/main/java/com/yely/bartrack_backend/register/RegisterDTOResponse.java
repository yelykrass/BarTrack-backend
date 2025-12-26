package com.yely.bartrack_backend.register;

import lombok.Builder;

@Builder
public record RegisterDTOResponse(String message, String username) {

}
