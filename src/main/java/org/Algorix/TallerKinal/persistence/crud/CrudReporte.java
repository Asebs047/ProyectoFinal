package org.Algorix.TallerKinal.persistence.crud;

import org.Algorix.TallerKinal.persistence.entity.ReporteEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CrudReporte extends CrudRepository<ReporteEntity,Long> {
    @Query("SELECT COALESCE(SUM(r.total), 0) FROM ReporteEntity r WHERE r.cita.fechaCita BETWEEN :start AND :end")
    BigDecimal sumTotalByFechaCitaBetween(@Param("start") LocalDate start, @Param("end") LocalDate end);
}
