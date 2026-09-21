package Back.dao;

import Back.bd.ConexionBD;
import Back.modelo.EstadoReserva;
import Back.modelo.Reserva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public boolean guardar(Reserva reserva) {
        String sql = "INSERT INTO reservas (cliente_id, servicio_id, fecha_hora_inicio, fecha_hora_fin, estado, codigo_cancelacion) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, reserva.getClienteId());
            stmt.setInt(2, reserva.getServicioId());
            stmt.setString(3, reserva.getFechaHoraInicio().toString()); // ISO-8601: YYYY-MM-DDTHH:MM
            stmt.setString(4, reserva.getFechaHoraFin().toString());
            stmt.setString(5, reserva.getEstado().name()); // Mapea el Enum a String
            stmt.setString(6, reserva.getCodigoCancelacion());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reserva.setId(rs.getInt(1));
                    }
                }
                return true;
            }

        } catch (SQLException e) {
            System.err.println("Error al guardar la reserva: " + e.getMessage());
        }

        return false;
    }

    public List<Reserva> obtenerReservasPorCanchaYFecha(int servicioId, String fechaInicioDia, String fechaFinDia) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE servicio_id = ? " +
                     "AND fecha_hora_inicio >= ? AND fecha_hora_inicio <= ? " +
                     "AND estado IN ('PENDIENTE', 'CONFIRMADA')";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, servicioId);
            stmt.setString(2, fechaInicioDia);
            stmt.setString(3, fechaFinDia);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Reserva r = new Reserva();
                    r.setId(rs.getInt("id"));
                    r.setClienteId(rs.getInt("cliente_id"));
                    r.setServicioId(rs.getInt("servicio_id"));
                    // Se parsea desde la cadena ISO almacenada en SQLite
                    r.setFechaHoraInicio(java.time.LocalDateTime.parse(rs.getString("fecha_hora_inicio")));
                    r.setFechaHoraFin(java.time.LocalDateTime.parse(rs.getString("fecha_hora_fin")));
                    r.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
                    r.setCodigoCancelacion(rs.getString("codigo_cancelacion"));

                    reservas.add(r);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar reservas ocupadas: " + e.getMessage());
        }

        return reservas;
    }

    public boolean cancelarPorCodigo(String codigoCancelacion) {
        String sql = "UPDATE reservas SET estado = 'CANCELADA' WHERE codigo_cancelacion = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoCancelacion);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al cancelar la reserva: " + e.getMessage());
            return false;
        }
    }
}