import java.sql.Connection;
import Back.bd.ConexionBD;

public class Main {
    public static void main(String[] args) {
        Connection conn = ConexionBD.obtenerConexion();

        if (conn != null) {
            System.out.println("¡Conexión exitosa a la base de datos SQLite!");
        } else {
            System.out.println("Fallo en la conexión.");
        }

        // Cierre al finalizar
        ConexionBD.cerrarConexion();
    }
}