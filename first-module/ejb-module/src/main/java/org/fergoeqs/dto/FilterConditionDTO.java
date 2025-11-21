package org.fergoeqs.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public record FilterConditionDTO(
        @NotNull String field,
        @NotNull String operator,
        Object value
) implements Serializable {}