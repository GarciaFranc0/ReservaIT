package Back.modelo;

import java.time.LocalDateTime;

public class Reserva {
    private int id;
    private int clienteId;
    private int servicioId;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private EstadoReserva estado;
    private String codigoCancelacion;

    public Reserva() {
        this.estado = EstadoReserva.PENDIENTE;
    }

    // Constructor completo para crear reservas desde la aplicacion
    public Reserva(int id, int clienteId, int servicioId, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, EstadoReserva estado, String codigoCancelacion) {
        this.id = id;
        this.clienteId = clienteId;
        this.servicioId = servicioId;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.estado = (estado != null) ? estado : EstadoReserva.PENDIENTE;
        this.codigoCancelacion = codigoCancelacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public int getServicioId() {
        return servicioId;
    }

    public void setServicioId(int servicioId) {
        this.servicioId = servicioId;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    public String getCodigoCancelacion() {
        return codigoCancelacion;
    }

    public void setCodigoCancelacion(String codigoCancelacion) {
        this.codigoCancelacion = codigoCancelacion;
    }
}