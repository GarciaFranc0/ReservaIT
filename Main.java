import Back.dao.UsuarioDAO;
import Back.modelo.Usuario;

public class Main {
    public static void main(String[] args) {
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        Usuario cliente = new Usuario(0, "Juan", "Pérez", "juan.perez@email.com", "1122334455");
        Usuario clienteGuardado = usuarioDAO.obtenerOCrear(cliente);

        if (clienteGuardado != null) {
            System.out.println("Cliente registrado/encontrado con ID: " + clienteGuardado.getId());
            System.out.println("Nombre: " + clienteGuardado.getNombre() + " " + clienteGuardado.getApellido());
            System.out.println("Correo: " + clienteGuardado.getCorreo());
        } else {
            System.out.println("No se pudo procesar el registro del cliente.");
        }
    }
}