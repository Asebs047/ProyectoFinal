package org.Algorix.TallerKinal.dominio.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record ModReporteDto(
        @Min(value = 1, message = "El id de la cita debe ser un valor válido")
        Long idCita,
        @NotBlank(message = "La descripcion del reporte no puede estar vacia")
        String description,
        @DecimalMin(value = "0.0", inclusive = true, message = "El total del reporte no puede ser negativo")
        BigDecimal total
) {
}
