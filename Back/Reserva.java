package Back;
import java.time.LocalDateTime;

public class Reserva {
    int id;
    Usuario id_cliente;
    Servicio servicio;    
    LocalDateTime fechaHoriaInicio;
    LocalDateTime fechaHoraFin = fechaHoriaInicio.plusMinutes(servicio.getDuracionMinutos());
    EstadoReserva estado = EstadoReserva.PENDIENTE;
    String codigoCancelacion = "Cancelacion" + id;
    
    public Reserva(int id, Usuario id_cliente, Servicio servicio, LocalDateTime fechaHoriaInicio) {
            this.id = id;
            this.id_cliente = id_cliente;
            this.servicio = servicio;
            this.fechaHoriaInicio = fechaHoriaInicio;
    }

    public int getId() {
            return id;
    }

    public Usuario getId_cliente() {
        return id_cliente;
    }

    public Servicio getServicio() {
        return servicio;
    }

    public LocalDateTime getFechaHoriaInicio() {
        return fechaHoriaInicio;
    }
}
