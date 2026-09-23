import Back.dao.UsuarioDAO;
import Back.modelo.EstadoReserva;
import Back.modelo.Reserva;
import Back.modelo.Usuario;
import Back.servicio.ReservaService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ReservaService reservaService = new ReservaService();

        int servicioId = 1;
        LocalDate fechaMañana = LocalDate.now().plusDays(1);

        Usuario cliente = usuarioDAO.obtenerOCrear(new Usuario(0, "Lionel", "Messi", "lionel@email.com", "1133445566"));

        LocalDateTime inicioValido = LocalDateTime.parse(fechaMañana.toString() + "T16:00:00");
        Reserva reservaValida = new Reserva();
        reservaValida.setClienteId(cliente.getId());
        reservaValida.setServicioId(servicioId);
        reservaValida.setFechaHoraInicio(inicioValido);
        reservaValida.setFechaHoraFin(inicioValido.plusMinutes(60));
        reservaValida.setEstado(EstadoReserva.CONFIRMADA);
        reservaValida.setCodigoCancelacion(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        boolean resultadoValido = reservaService.crearReserva(reservaValida);
        System.out.println("1. ¿Se creó la reserva válida?: " + resultadoValido);

        LocalDate fechaAyer = LocalDate.now().minusDays(1);
        LocalDateTime inicioPasado = LocalDateTime.parse(fechaAyer.toString() + "T15:00:00");
        Reserva reservaPasado = new Reserva();
        reservaPasado.setClienteId(cliente.getId());
        reservaPasado.setServicioId(servicioId);
        reservaPasado.setFechaHoraInicio(inicioPasado);
        reservaPasado.setFechaHoraFin(inicioPasado.plusMinutes(60));
        reservaPasado.setEstado(EstadoReserva.CONFIRMADA);
        reservaPasado.setCodigoCancelacion(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        boolean resultadoPasado = reservaService.crearReserva(reservaPasado);
        System.out.println("2. ¿Bloqueó la reserva en el pasado?: " + !resultadoPasado + " (Debe ser true, es decir, rechazado)");

        LocalDateTime inicioMadrugada = LocalDateTime.parse(fechaMañana.toString() + "T02:00:00");
        Reserva reservaMadrugada = new Reserva();
        reservaMadrugada.setClienteId(cliente.getId());
        reservaMadrugada.setServicioId(servicioId);
        reservaMadrugada.setFechaHoraInicio(inicioMadrugada);
        reservaMadrugada.setFechaHoraFin(inicioMadrugada.plusMinutes(60));
        reservaMadrugada.setEstado(EstadoReserva.CONFIRMADA);
        reservaMadrugada.setCodigoCancelacion(UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        boolean resultadoMadrugada = reservaService.crearReserva(reservaMadrugada);
        System.out.println("3. ¿Bloqueó la reserva fuera de hora?: " + !resultadoMadrugada + " (Debe ser true, es decir, rechazado)");
    }
}