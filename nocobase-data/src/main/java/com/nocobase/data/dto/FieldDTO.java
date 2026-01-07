package com.nocobase.data.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Field Data Transfer Object
 * Used for creating/updating fields
 */
public record FieldDTO(
        @NotNull(message = "Collection ID is required")
        Long collectionId,

        @NotBlank(message = "Field name is required")
        @Size(min = 1, max = 100, message = "Field name must be between 1 and 100 characters")
        String name,

        @NotBlank(message = "Field title is required")
        @Size(min = 1, max = 200, message = "Field title must be between 1 and 200 characters")
        String title,

        @NotBlank(message = "Field type is required")
        String type,

        String dbType,

        String description,

        Boolean required,

        Boolean unique,

        String defaultValue,

        String uiSchema,

        Integer sort,

        Boolean system,

        Boolean hidden
) {
}
