import Back.dao.ReservaDAO;
import Back.dao.ServicioDAO;
import Back.dao.UsuarioDAO;
import Back.modelo.EstadoReserva;
import Back.modelo.Reserva;
import Back.modelo.Servicio;
import Back.modelo.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Main {
    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        ServicioDAO servicioDAO = new ServicioDAO();
        ReservaDAO reservaDAO = new ReservaDAO();

        Usuario cliente = usuarioDAO.obtenerOCrear(
            new Usuario(0, "Carlos", "Tevez", "carlos@email.com", "1199887766")
        );
        List<Servicio> canchas = servicioDAO.obtenerTodosActivos();
        if (cliente != null && !canchas.isEmpty()) {
            Servicio canchaElegida = canchas.get(0);          
            LocalDateTime inicio = LocalDateTime.now().plusDays(1).withHour(19).withMinute(0).withSecond(0).withNano(0);
            LocalDateTime fin = inicio.plusMinutes(canchaElegida.getDuracionMinutos());
            String codigoCancelacion = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            Reserva nuevaReserva = new Reserva(
                0,
                cliente.getId(),
                canchaElegida.getId(),
                inicio,
                fin,
                EstadoReserva.PENDIENTE,
                codigoCancelacion
            );

            if (reservaDAO.guardar(nuevaReserva)) {
                System.out.println("¡Reserva guardada con exito!");
                System.out.println("Cancha: " + canchaElegida.getNombre());
                System.out.println("Cliente: " + cliente.getNombre() + " " + cliente.getApellido());
                System.out.println("Horario: " + inicio + " a " + fin);
                System.out.println("Codigo de cancelacion: " + codigoCancelacion);
            }
        }
    }
}