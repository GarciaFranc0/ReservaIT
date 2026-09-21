import Back.servicio.ReservaService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ReservaService reservaService = new ReservaService();

        int servicioId = 1;
        LocalDate fechaMañana = LocalDate.now().plusDays(1);
        int duracion = 60;

        System.out.println("=== BUSCANDO TURNOS LIBRES ===");
        List<LocalTime> turnos = reservaService.obtenerHorariosDisponibles(servicioId, fechaMañana, duracion);
        
        System.out.println("Cantidad de turnos encontrados: " + turnos.size());
        for (LocalTime hora : turnos) {
            System.out.println("Disponible: " + hora + " hs");
        }
    }
}