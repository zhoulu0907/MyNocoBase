package com.nocobase.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Collection Data Transfer Object
 * Used for creating/updating collections
 */
public record CollectionDTO(
        @NotBlank(message = "Collection name is required")
        @Size(min = 1, max = 100, message = "Collection name must be between 1 and 100 characters")
        String name,

        @NotBlank(message = "Collection title is required")
        @Size(min = 1, max = 200, message = "Collection title must be between 1 and 200 characters")
        String title,

        String tableName,

        String description,

        String type,

        Boolean system,

        Boolean hidden,

        Integer sort
) {
}
