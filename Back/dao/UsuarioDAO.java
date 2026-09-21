package Back.dao;

import Back.bd.ConexionBD;
import Back.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UsuarioDAO {

    public Usuario buscarPorCorreo(String correo) {
        String sql = "SELECT * FROM usuarios WHERE correo = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, correo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("correo"),
                        rs.getString("telefono")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por correo: " + e.getMessage());
        }

        return null; // Retorna null si no se encontro el usuario
    }


    public boolean guardar(Usuario usuario) {
        String sql = "INSERT INTO usuarios (nombre, apellido, correo, telefono) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNombre());
            stmt.setString(2, usuario.getApellido());
            stmt.setString(3, usuario.getCorreo());
            stmt.setString(4, usuario.getTelefono());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                // Recuperar el ID autoincremental generado por SQLite
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        usuario.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar el usuario: " + e.getMessage());
        }

        return false;
    }

    public Usuario obtenerOCrear(Usuario nuevoUsuario) {
        Usuario existente = buscarPorCorreo(nuevoUsuario.getCorreo());
        
        if (existente != null) {
            return existente;
        }

        if (guardar(nuevoUsuario)) {
            return nuevoUsuario;
        }

        return null;
    }
}