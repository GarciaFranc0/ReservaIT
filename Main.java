import Back.dao.HorarioAtencionDAO;
import Back.modelo.HorarioAtencion;
import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        HorarioAtencionDAO horarioDAO = new HorarioAtencionDAO();
        int diaHoy = LocalDate.now().getDayOfWeek().getValue(); 
        HorarioAtencion horarioHoy = horarioDAO.obtenerPorDia(diaHoy);

        if (horarioHoy != null) {
            System.out.println("El complejo atiende hoy de " + horarioHoy.getHoraApertura() + " a " + horarioHoy.getHoraCierre() + " hs.");
        } else {
            System.out.println("El complejo se encuentra cerrado el dia de hoy.");
        }
    }
}