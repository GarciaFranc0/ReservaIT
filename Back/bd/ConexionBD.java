package Back.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    private static final String URL = "jdbc:sqlite:db.db";

    private ConexionBD() {}

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontro el driver de SQLite.");
        }
        
        Connection conexion = DriverManager.getConnection(URL);
        
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        
        return conexion;
    }

    public static void cerrarConexion() {
    }
}