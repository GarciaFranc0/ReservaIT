import Back.servicio.ReservaService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ReservaService reservaService = new ReservaService();
        int servicioId = 1;
        LocalDate fechaConsulta = LocalDate.now().plusDays(1);
        int duracionMinutos = 60;
        List<LocalTime> turnosLibres = reservaService.obtenerHorariosDisponibles(servicioId, fechaConsulta, duracionMinutos);

        System.out.println("--- TURNOS DISPONIBLES PARA EL " + fechaConsulta + " ---");
        if (turnosLibres.isEmpty()) {
            System.out.println("No hay turnos disponibles o el complejo esta cerrado.");
        } else {
            for (LocalTime hora : turnosLibres) {
                System.out.println(" Turno libre a las: " + hora + " hs");
            }
        }
    }
}