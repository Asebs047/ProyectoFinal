package org.Algorix.TallerKinal.dominio.repository;



import org.Algorix.TallerKinal.dominio.dto.ModReporteDto;
import org.Algorix.TallerKinal.dominio.dto.ReporteDto;

import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

public interface ResporteRepository {
    List<ReporteDto> obtenerTodos();
    ReporteDto buscarPorId(Long id);
    ReporteDto guardarReporte(ReporteDto reporteDto);
    ReporteDto modificarReporte(Long id, ModReporteDto modReporteDto);
    void eliminarReporte(Long id);

    // Suma los totales de reportes cuyas citas estén entre start y end (inclusive)
    BigDecimal sumarTotalEntreFechas(LocalDate start, LocalDate end);
}
