package Back.dao;

import Back.bd.ConexionBD;
import Back.modelo.Servicio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ServicioDAO {
    public boolean guardar(Servicio servicio) {
        String sql = "INSERT INTO servicios (nombre, descripcion, duracion_minutos, precio, activo) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, servicio.getNombre());
            stmt.setString(2, servicio.getDescripcion());
            stmt.setInt(3, servicio.getDuracionMinutos());
            stmt.setDouble(4, servicio.getPrecio());
            stmt.setInt(5, servicio.isActivo() ? 1 : 0);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar el servicio/cancha: " + e.getMessage());
            return false;
        }
    }
    
    public List<Servicio> obtenerTodosActivos() {
        List<Servicio> servicios = new ArrayList<>();
        String sql = "SELECT * FROM servicios WHERE activo = 1";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Servicio servicio = new Servicio(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getInt("duracion_minutos"),
                    rs.getDouble("precio"),
                    rs.getInt("activo") == 1
                );
                servicios.add(servicio);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener servicios: " + e.getMessage());
        }

        return servicios;
    }

   public List<Servicio> obtenerTodos() {
    List<Servicio> lista = new ArrayList<>();
    String sql = "SELECT id, nombre, duracion_minutos, precio, descripcion FROM servicios";
    
    try (Connection conn = ConexionBD.obtenerConexion();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        boolean hayMas = rs.next();
        while (hayMas) {
            Servicio servicio = new Servicio();
            servicio.setId(rs.getInt("id"));
            servicio.setNombre(rs.getString("nombre"));
            servicio.setDuracionMinutos(rs.getInt("duracion_minutos"));
            servicio.setPrecio(rs.getDouble("precio"));
            
            // Asegurarnos de mapear la descripción si la columna existe
            try {
                servicio.setDescripcion(rs.getString("descripcion"));
            } catch (Exception e) {
                // Por si acaso la columna no viniera en el result set
            }
            
            lista.add(servicio);
            hayMas = rs.next();
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener los servicios: " + e.getMessage());
    }
    
    List<Servicio> resultadoFinal = lista;
    return resultadoFinal;
}
}
