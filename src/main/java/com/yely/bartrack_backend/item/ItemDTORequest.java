package com.yely.bartrack_backend.item;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ItemDTORequest(@NotBlank(message = "Name is required") String name,

        @NotBlank(message = "Category is required") String category,

        @NotNull(message = "Price is required") @Positive(message = "Price must be greater than 0") Double price,

        @NotNull(message = "Quantity is required") @Min(value = 0, message = "Quantity cannot be negative") Integer quantity,

        Boolean active) {

}
