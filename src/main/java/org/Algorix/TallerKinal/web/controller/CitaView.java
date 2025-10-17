package org.Algorix.TallerKinal.web.controller;

import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import lombok.Data;
import org.Algorix.TallerKinal.dominio.dto.CitaDto;
import org.Algorix.TallerKinal.dominio.dto.ModCitaDto;
import org.Algorix.TallerKinal.dominio.dto.MecanicoDto;
import org.Algorix.TallerKinal.dominio.dto.ClienteDto;
import org.Algorix.TallerKinal.dominio.dto.VehiculoDto;
import org.Algorix.TallerKinal.dominio.service.CitaService;
import org.Algorix.TallerKinal.dominio.service.MecanicoService;
import org.Algorix.TallerKinal.dominio.service.ClienteService;
import org.Algorix.TallerKinal.dominio.service.VehiculoService;
import org.primefaces.PrimeFaces;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component("citaView")
@SessionScope
@Data
public class CitaView implements Serializable {
    private final CitaService citaService;
    private final ClienteView clienteView;
    private final MecanicoService mecanicoService;
    private final ClienteService clienteService;
    private final VehiculoService vehiculoService;
    private List<CitaDto> citas;
    private CitaDto selected;
    // Citas pendientes del día (para vista cliente/administrador)
    private List<CitaDto> pendientesHoy;

    // listas para selects en UI (admin)
    private List<MecanicoDto> mecanicos;
    private List<ClienteDto> clientes;
    private List<VehiculoDto> vehiculos;

    // Campos unificados (crear / editar)
    private LocalDate editAppointmentDate;
    private Long editIdEmpleado;
    private Long editIdCliente;
    private String editAppointmentType;
    private Long editIdVehiculo;
    private String editStatus;
    private String editComments;

    // campos para recibir parámetros de la vista (view params)
    private Long viewParamIdVehiculo;
    private Long viewParamIdCliente;

    public CitaView(CitaService citaService, ClienteView clienteView, MecanicoService mecanicoService, ClienteService clienteService, VehiculoService vehiculoService) {
        this.citaService = citaService;
        this.clienteView = clienteView;
        this.mecanicoService = mecanicoService;
        this.clienteService = clienteService;
        this.vehiculoService = vehiculoService;
    }

    @PostConstruct
    public void init() {
        refresh();
        refreshHoy();
        clearEdits();
    }

    public void refresh() {
        try {
            this.citas = new ArrayList<>(citaService.obtenerTodo());
            // cargar listas para selects (para admin)
            try { this.mecanicos = mecanicoService.obtenerTodo(); } catch (Exception e) { this.mecanicos = new ArrayList<>(); }
            try { this.clientes = clienteService.obtenerClientes(); } catch (Exception e) { this.clientes = new ArrayList<>(); }
            try { this.vehiculos = vehiculoService.obtenerTodo(); } catch (Exception e) { this.vehiculos = new ArrayList<>(); }
        } catch (Exception e) {
            this.citas = new ArrayList<>();
        }
    }

    /** Refresca la lista de citas pendientes para hoy (estado PENDIENTE). */
    public void refreshHoy() {
        try {
            var today = java.time.LocalDate.now();
            this.pendientesHoy = new ArrayList<>(citaService.obtenerPorFechaYEstado(today, "PENDIENTE"));
        } catch (Exception e) {
            this.pendientesHoy = new ArrayList<>();
        }
    }

    /**
     * Iniciar agendado desde la vista de cliente: abre el diálogo con campos prellenados.
     */
    public void iniciarAgendarCita(Long idVehiculo) {
        try {
            if (clienteView == null || !clienteView.isLoggedIn() || clienteView.getCurrentCliente() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Acceso denegado", "Debe iniciar sesión como cliente para agendar una cita."));
                return;
            }
            // preparar nueva cita usando sesión
            clearEdits();
            this.editIdVehiculo = idVehiculo;
            this.editIdCliente = clienteView.getCurrentCliente().getId_cliente();
            this.editAppointmentDate = LocalDate.now();
            this.editStatus = "PENDIENTE"; // por defecto
            // mostrar diálogo desde la vista (clienteWeb.xhtml hará oncomplete)
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo iniciar agendado."));
        }
    }

    /**
     * Guardar cita creada por cliente. Valida campos mínimos y fuerza idCliente desde sesión.
     */
    public void guardarCitaDesdeCliente() {
        try {
            // validar sesión
            if (clienteView == null || !clienteView.isLoggedIn() || clienteView.getCurrentCliente() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Acceso denegado", "Debe iniciar sesión como cliente para agendar una cita."));
                return;
            }
            // validaciones básicas
            if (this.editAppointmentDate == null) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Fecha requerida."));
                return;
            }
            if (this.editAppointmentType == null || this.editAppointmentType.isBlank()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Tipo de cita requerido."));
                return;
            }
            if (this.editComments == null || this.editComments.isBlank()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "Comentario requerido."));
                return;
            }

            // forzar cliente desde sesión
            this.editIdCliente = clienteView.getCurrentCliente().getId_cliente();
            if (this.editStatus == null || this.editStatus.isBlank()) this.editStatus = "PENDIENTE";

            CitaDto nuevo = new CitaDto(null, this.editAppointmentDate, this.editIdEmpleado, this.editIdCliente,
                    this.editAppointmentType, this.editIdVehiculo, this.editStatus, this.editComments);
            citaService.guardarCita(nuevo);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Éxito", "Cita agendada."));
            refresh();
            PrimeFaces.current().ajax().update("growlForm:growlMensajes", ":citasForm:tabla");
            PrimeFaces.current().executeScript("PF('ventanaAgendarCita').hide()");
            clearEdits();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar la cita."));
        }
    }

    /**
     * Si la página recibe parámetros idVehiculo / idCliente, preparar una nueva cita
     * y abrir el diálogo. Sólo permite crear si hay sesión de cliente y el id coincide.
     */
    public void prepararNuevaCitaFromParams() {
        try {
            if (this.viewParamIdVehiculo == null) return; // no hay petición para agendar

            // validar sesión de cliente
            if (clienteView == null || !clienteView.isLoggedIn() || clienteView.getCurrentCliente() == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Acceso denegado", "Debe iniciar sesión como cliente para agendar una cita."));
                return;
            }

            Long idClienteSesion = clienteView.getCurrentCliente().getId_cliente();

            // si viene idCliente en parámetros, verificar coherencia
            if (this.viewParamIdCliente != null && !this.viewParamIdCliente.equals(idClienteSesion)) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN, "Datos incorrectos", "No coincide el cliente autenticado con el pedido de agendar."));
                return;
            }

            // preparar nueva cita
            this.selected = null;
            clearEdits();
            this.editIdVehiculo = this.viewParamIdVehiculo;
            this.editIdCliente = idClienteSesion; // siempre usar sesión
            this.editAppointmentDate = LocalDate.now();
            this.editStatus = "PENDIENTE";

            PrimeFaces.current().executeScript("PF('ventanaModalCita').show()");
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo preparar la cita."));
        }
    }

    private void clearEdits() {
        this.editAppointmentDate = null;
        this.editIdEmpleado = null;
        this.editIdCliente = null;
        this.editAppointmentType = null;
        this.editIdVehiculo = null;
        this.editStatus = null;
        this.editComments = null;
    }

    public void agregarCita() {
        this.selected = null;
        clearEdits();
        PrimeFaces.current().executeScript("PF('ventanaModalCita').show()");
    }

    public void prepararEdicionCita(CitaDto c) {
        this.selected = c;
        clearEdits();
        if (c != null) {
            this.editAppointmentDate = c.appointmentDate();
            this.editIdEmpleado = c.idEmpleado();
            this.editIdCliente = c.idCliente();
            this.editAppointmentType = c.appointmentType();
            this.editIdVehiculo = c.idVehiculo();
            this.editStatus = c.status();
            this.editComments = c.comments();
        }
        PrimeFaces.current().executeScript("PF('ventanaModalCita').show()");
    }

    public void guardarCita() {
        try {
            if (this.selected == null) {
                // Crear
                CitaDto nuevo = new CitaDto(null, editAppointmentDate, editIdEmpleado, editIdCliente, editAppointmentType, editIdVehiculo, editStatus, editComments);
                citaService.guardarCita(nuevo);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cita Agregada"));
            } else {
                // Editar (si un campo viene null, usamos el valor previo)
                ModCitaDto mod = new ModCitaDto(
                        editIdEmpleado != null ? editIdEmpleado : selected.idEmpleado(),
                        editAppointmentDate != null ? editAppointmentDate : selected.appointmentDate(),
                        editAppointmentType != null && !editAppointmentType.isBlank() ? editAppointmentType : selected.appointmentType(),
                        editIdCliente != null ? editIdCliente : selected.idCliente(),
                        editIdVehiculo != null ? editIdVehiculo : selected.idVehiculo(),
                        editStatus != null && !editStatus.isBlank() ? editStatus : selected.status(),
                        editComments != null && !editComments.isBlank() ? editComments : selected.comments()
                );
                citaService.modificarCita(selected.id_cita(), mod);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Cita Modificada"));
            }
            refresh();
            refreshHoy();
            PrimeFaces.current().ajax().update("citasForm:tabla", "growlForm:growlMensajes");
            PrimeFaces.current().executeScript("PF('ventanaModalCita').hide()");
            this.selected = null;
            clearEdits();
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error", "No se pudo guardar"));
        }
    }

    public void eliminarCita(){ if(this.selected==null || this.selected.id_cita()==null) return; try { citaService.eliminarCita(this.selected.id_cita()); FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Cita Eliminada")); refresh(); PrimeFaces.current().ajax().update("citasForm:tabla", "growlForm:growlMensajes"); this.selected=null; clearEdits(); } catch(Exception e){ FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error","No se pudo eliminar")); } }

    // Al eliminar también actualizar pendientes de hoy
    public void eliminarCitaYRefrescar(){
        if(this.selected==null || this.selected.id_cita()==null) return;
        try {
            citaService.eliminarCita(this.selected.id_cita());
            FacesContext.getCurrentInstance().addMessage(null,new FacesMessage("Cita Eliminada"));
            refresh();
            refreshHoy();
            PrimeFaces.current().ajax().update("citasForm:tabla", "growlForm:growlMensajes");
            this.selected=null; clearEdits();
        } catch(Exception e){ FacesContext.getCurrentInstance().addMessage(null,new FacesMessage(FacesMessage.SEVERITY_ERROR,"Error","No se pudo eliminar")); }
    }

    public void cancelarCita(){ this.selected=null; clearEdits(); PrimeFaces.current().executeScript("PF('ventanaModalCita').hide()"); }

    // Evita que clientes accedan a la página de gestión de citas (solo admin)
    public void asegurarAccesoAdministrador() {
        try {
            if (clienteView != null && clienteView.isLoggedIn()) {
                // Cliente intentando acceder a la página de administración: redirigir a su vista
                redirectLocal("clienteWeb.xhtml");
            }
        } catch (Exception e) {
            // no hacemos nada especial; si la redirección falla, el page render continuará
        }
    }

    private void redirectLocal(String page) {
        try {
            var fc = FacesContext.getCurrentInstance();
            var ec = fc.getExternalContext();
            String target;
            if (page == null) {
                target = ec.getRequestContextPath() + "/";
            } else if (page.startsWith("http://") || page.startsWith("https://")) {
                target = page;
            } else {
                target = ec.getRequestContextPath() + (page.startsWith("/") ? "" : "/") + page;
            }
            ec.redirect(target);
        } catch (IOException e) {
            // ignorar
        }
    }
}
