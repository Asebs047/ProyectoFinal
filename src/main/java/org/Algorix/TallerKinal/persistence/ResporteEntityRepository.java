package org.Algorix.TallerKinal.persistence;

import org.Algorix.TallerKinal.dominio.dto.ModReporteDto;
import org.Algorix.TallerKinal.dominio.dto.ReporteDto;
import org.Algorix.TallerKinal.dominio.exception.ReporteNoExiste;
import org.Algorix.TallerKinal.dominio.exception.CitaNoExiste;
import org.Algorix.TallerKinal.dominio.repository.ResporteRepository;
import org.Algorix.TallerKinal.persistence.crud.CrudReporte;
import org.Algorix.TallerKinal.persistence.crud.CrudCita;
import org.Algorix.TallerKinal.persistence.entity.ReporteEntity;
import org.Algorix.TallerKinal.web.mapper.ReporteMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDate;
import java.math.BigDecimal;

@Repository
public class ResporteEntityRepository implements ResporteRepository {
    private final CrudReporte crudReporte;
    private final CrudCita crudCita;
    private final ReporteMapper reporteMapper;

    public ResporteEntityRepository(CrudReporte crudReporte, CrudCita crudCita, ReporteMapper reporteMapper) {
        this.crudReporte = crudReporte;
        this.crudCita = crudCita;
        this.reporteMapper = reporteMapper;
    }

    @Override
    public List<ReporteDto> obtenerTodos() {
        return this.reporteMapper.toDto(this.crudReporte.findAll());
    }

    @Override
    public ReporteDto buscarPorId(Long id) {
        ReporteEntity reporteEntity = this.crudReporte.findById(id).orElse(null);
        if (reporteEntity == null) {
            throw new ReporteNoExiste(id);
        }
        return this.reporteMapper.toDto(reporteEntity);
    }

    @Override
    public ReporteDto guardarReporte(ReporteDto reporteDto) {
        ReporteEntity reporteEntity = this.reporteMapper.toEntity(reporteDto);
        // validar existencia de la cita referenciada
        if (reporteEntity == null || reporteEntity.getCita() == null || reporteEntity.getCita().getId_cita() == null) {
            throw new CitaNoExiste(null);
        }
        Long idCita = reporteEntity.getCita().getId_cita();
        if (!this.crudCita.existsById(idCita)) {
            throw new CitaNoExiste(idCita);
        }
        ReporteEntity saved = this.crudReporte.save(reporteEntity);
        return this.reporteMapper.toDto(saved);
    }

    @Override
    public ReporteDto modificarReporte(Long id, ModReporteDto modReporteDto) {
        ReporteEntity reporteEntity = this.crudReporte.findById(id).orElse(null);
        if (reporteEntity == null) {
            throw new ReporteNoExiste(id);
        }
        // validar que la cita nueva exista antes de aplicar cambios
        if (modReporteDto != null && modReporteDto.idCita() != null && !this.crudCita.existsById(modReporteDto.idCita())) {
            throw new CitaNoExiste(modReporteDto.idCita());
        }
        this.reporteMapper.modificarEntityFromDto(modReporteDto, reporteEntity);
        return this.reporteMapper.toDto(this.crudReporte.save(reporteEntity));
    }

    @Override
    public void eliminarReporte(Long id) {
    ReporteEntity reporteEntity = this.crudReporte.findById(id).orElse(null);
    if (reporteEntity == null) {
        throw new ReporteNoExiste(id);
    } else {
        this.crudReporte.deleteById(id);
        }
    }

    @Override
    public BigDecimal sumarTotalEntreFechas(LocalDate start, LocalDate end) {
        return this.crudReporte.sumTotalByFechaCitaBetween(start, end);
    }
}
