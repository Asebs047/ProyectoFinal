package org.Algorix.TallerKinal.web.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import org.Algorix.TallerKinal.dominio.dto.AdministradorDto;
import org.Algorix.TallerKinal.dominio.dto.CitaDto;
import org.Algorix.TallerKinal.dominio.service.CitaService;
import org.Algorix.TallerKinal.dominio.service.ReporteService;
import org.Algorix.TallerKinal.dominio.service.AdministradorService;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

@Component("administradorView")
@SessionScope
@Data
public class AdministradorView {
    private final AdministradorService administradorService;
    private final CitaService citaService;
    private final ReporteService reporteService;

    private List<AdministradorDto> administradores;
    private AdministradorDto selected;

    // Dashboard
    private List<CitaDto> pendientesHoy;
    private BigDecimal totalSemana;
    private BigDecimal totalMes;

    // Campos unificados para crear/editar
    private String editName;
    private String editLastName;
    private String editEmail;
    private String editPassword;
    private String editPhone;

    public AdministradorView(AdministradorService administradorService, CitaService citaService, ReporteService reporteService) {
        this.administradorService = administradorService;
        this.citaService = citaService;
        this.reporteService = reporteService;
    }

    @PostConstruct
    public void init() {
        refresh();
        clearEdits();
    }

    public void refresh() {
        try {
            List<AdministradorDto> list = administradorService.obtenerAdministradores();
            this.administradores = list == null ? new ArrayList<>() : new ArrayList<>(list);
            // actualizar datos del dashboard (no bloquear si falla)
            try {
                refreshDashboard();
            } catch (Exception ignored) {
            }
        } catch (Exception e) {
            this.administradores = new ArrayList<>();
        }
    }

    private void refreshDashboard() {
        LocalDate today = LocalDate.now();
        try {
            // obtener pendientes hoy (estado PENDIENTE)
            List<CitaDto> pendientes = citaService.obtenerPorFechaYEstado(today, "PENDIENTE");
            this.pendientesHoy = pendientes == null ? new ArrayList<>() : pendientes;
        } catch (Exception e) {
            this.pendientesHoy = new ArrayList<>();
        }

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

    private void clearEdits() {
        this.editName = "";
        this.editLastName = "";
        this.editEmail = "";
        this.editPassword = "";
        this.editPhone = "";
    }

    public void agregarAdministrador() {
        this.selected = null;
        clearEdits();
        PrimeFaces.current().executeScript("PF('ventanaModalAdministrador').show()");
    }

    public void prepararEdicionAdministrador(AdministradorDto a) {
        this.selected = a;
        clearEdits();
        if (a != null) {
            this.editName = a.name();
            this.editLastName = a.lastname();
            this.editEmail = a.email();
            this.editPassword = a.password();
            this.editPhone = a.phone();
        }
        PrimeFaces.current().executeScript("PF('ventanaModalAdministrador').show()");
    }

    public void guardarAdministrador() {
        try {
            if (this.selected == null) {
                AdministradorDto dto = new AdministradorDto(null, editName, editLastName, editEmail, editPassword, editPhone);
                administradorService.guardarAdministrador(dto);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Administrador Agregado"));
            } else {
                AdministradorDto mod = new AdministradorDto(selected.id_admin(), editName, editLastName, editEmail, editPassword, editPhone);
                administradorService.modificarAdministrador(selected.id_admin(), mod);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Administrador Modificado"));
            }
            refresh();
            PrimeFaces.current().ajax().update("administradoresForm:tabla", "growlForm:growlMensajes");
            PrimeFaces.current().executeScript("PF('ventanaModalAdministrador').hide()");
            this.selected = null;
            clearEdits();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar"));
        }
    }

    public void eliminarAdministrador() {
        if (this.selected == null || this.selected.id_admin() == null) return;
        try {
            administradorService.eliminarAdministrador(this.selected.id_admin());
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Administrador Eliminado"));
            refresh();
            PrimeFaces.current().ajax().update("administradoresForm:tabla", "growlForm:growlMensajes");
            this.selected = null;
            clearEdits();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo eliminar"));
        }
    }

    public void cancelarAdministrador() {
        this.selected = null;
        clearEdits();
        PrimeFaces.current().executeScript("PF('ventanaModalAdministrador').hide()");
    }
}
