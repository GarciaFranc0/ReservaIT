import Back.dao.ServicioDAO;
import Back.modelo.Servicio;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        ServicioDAO servicioDAO = new ServicioDAO();

        Servicio cancha1 = new Servicio(0, "Cancha 1 - Fútbol 5", "Sintético techado con iluminación LED", 60, 15000.0, true);
        Servicio cancha2 = new Servicio(0, "Cancha 2 - Fútbol 7", "Césped natural al aire libre", 60, 22000.0, true);

        servicioDAO.guardar(cancha1);
        servicioDAO.guardar(cancha2);

        List<Servicio> lista = servicioDAO.obtenerTodosActivos();
        System.out.println("--- CANCHAS DISPONIBLES EN LA BASE DE DATOS ---");
        for (Servicio s : lista) {
            System.out.println("ID: " + s.getId() + " | " + s.getNombre() + " | Precio: $" + s.getPrecio());
        }
    }
}