package org.Algorix.TallerKinal.web.controller;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.Algorix.TallerKinal.dominio.service.ReporteService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Component("ingresosView")
@SessionScope
@Data
public class IngresosView {
    private final ReporteService reporteService;

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalSemana;
    private BigDecimal totalMes;
    private BigDecimal totalRange;

    public IngresosView(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @PostConstruct
    public void init() {
        refreshTotals();
    }

    public void refreshTotals() {
        LocalDate today = LocalDate.now();
        try {
            LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            BigDecimal semana = reporteService.obtenerTotalEntre(startOfWeek, endOfWeek);
            this.totalSemana = semana == null ? BigDecimal.ZERO : semana;
        } catch (Exception e) {
            this.totalSemana = BigDecimal.ZERO;
        }

        try {
            LocalDate startOfMonth = today.withDayOfMonth(1);
            LocalDate endOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
            BigDecimal mes = reporteService.obtenerTotalEntre(startOfMonth, endOfMonth);
            this.totalMes = mes == null ? BigDecimal.ZERO : mes;
        } catch (Exception e) {
            this.totalMes = BigDecimal.ZERO;
        }
    }

    public void computeRange() {
        try {
            if (this.startDate == null || this.endDate == null) {
                this.totalRange = BigDecimal.ZERO;
                return;
            }
            BigDecimal rango = reporteService.obtenerTotalEntre(this.startDate, this.endDate);
            this.totalRange = rango == null ? BigDecimal.ZERO : rango;
        } catch (Exception e) {
            this.totalRange = BigDecimal.ZERO;
        }
    }
}

