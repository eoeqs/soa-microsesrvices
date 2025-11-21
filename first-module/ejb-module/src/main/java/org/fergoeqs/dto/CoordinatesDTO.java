package org.fergoeqs.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public record CoordinatesDTO(
        @NotNull Double x,
        @NotNull Float y
) implements Serializable {}