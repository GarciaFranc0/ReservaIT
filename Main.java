import Back.dao.ReservaDAO;
import Back.dao.UsuarioDAO;
import Back.modelo.EstadoReserva;
import Back.modelo.Reserva;
import Back.modelo.Usuario;
import Back.servicio.ReservaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ReservaDAO reservaDAO = new ReservaDAO();
        ReservaService reservaService = new ReservaService();

        int servicioId = 1;
        LocalDate fechaMañana = LocalDate.now().plusDays(1);
        int duracion = 60;

        Usuario cliente = usuarioDAO.obtenerOCrear(new Usuario(0, "Lionel", "Messi", "lionel@email.com", "1133445566"));

        LocalDateTime inicioReserva = LocalDateTime.parse(fechaMañana.toString() + "T15:00:00");
        LocalDateTime finReserva = inicioReserva.plusMinutes(duracion);
        String codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Reserva nuevaReserva = new Reserva();
        nuevaReserva.setClienteId(cliente.getId());
        nuevaReserva.setServicioId(servicioId);
        nuevaReserva.setFechaHoraInicio(inicioReserva);
        nuevaReserva.setFechaHoraFin(finReserva);
        nuevaReserva.setEstado(EstadoReserva.CONFIRMADA);
        nuevaReserva.setCodigoCancelacion(codigo);

        boolean guardado = reservaDAO.guardar(nuevaReserva);
        System.out.println("Guardado exitoso: " + guardado);

        List<LocalTime> turnos = reservaService.obtenerHorariosDisponibles(servicioId, fechaMañana, duracion);
        System.out.println("¿Está libre las 15:00?: " + turnos.contains(LocalTime.of(15, 0)));

        boolean cancelado = reservaService.cancelarReserva(codigo);
        System.out.println("Cancelación exitosa: " + cancelado);

        List<LocalTime> turnosTrasCancelar = reservaService.obtenerHorariosDisponibles(servicioId, fechaMañana, duracion);
        System.out.println("¿Está libre las 15:00 después de cancelar?: " + turnosTrasCancelar.contains(LocalTime.of(15, 0)));
    }
}