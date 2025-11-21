package org.fergoeqs.dto;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

public record AddressRequestDTO(
        @NotNull String street
) implements Serializable {}