package Back.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    private static final String URL = "jdbc:sqlite:db.db";
    private static Connection conexion = null;

    private ConexionBD() {}

    public static Connection obtenerConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                // Registra el driver de SQLite en tiempo de ejecucion
                Class.forName("org.sqlite.JDBC");

                conexion = DriverManager.getConnection(URL);
                
                try (Statement stmt = conexion.createStatement()) {
                    stmt.execute("PRAGMA foreign_keys = ON;");
                }
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontro el driver de SQLite en Referenced Libraries.");
        } catch (SQLException e) {
            System.err.println("Error al conectar con la base de datos: " + e.getMessage());
        }
        return conexion;
    }

    public static void cerrarConexion() {
        if (conexion != null) {
            try {
                if (!conexion.isClosed()) {
                    conexion.close();
                    System.out.println("Conexion a SQLite cerrada.");
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexion: " + e.getMessage());
            }
        }
    }
}