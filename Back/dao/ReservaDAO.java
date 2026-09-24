package Back.dao;

import Back.bd.ConexionBD;
import Back.modelo.EstadoReserva;
import Back.modelo.Reserva;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public boolean guardar(Reserva reserva) {
        boolean guardado = false;
        String sql = "INSERT INTO reservas (cliente_id, servicio_id, fecha_hora_inicio, fecha_hora_fin, estado, codigo_cancelacion) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, reserva.getClienteId());
            stmt.setInt(2, reserva.getServicioId());
            stmt.setString(3, reserva.getFechaHoraInicio().toString());
            stmt.setString(4, reserva.getFechaHoraFin().toString());
            stmt.setString(5, reserva.getEstado().name());
            stmt.setString(6, reserva.getCodigoCancelacion());

            int filasAfectadas = stmt.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        reserva.setId(rs.getInt(1));
                    }
                }
                guardado = true;
            }

        } catch (SQLException e) {
            System.err.println("ERROR REAL AL GUARDAR: " + e.getMessage());
        }

        return guardado;
    }

    public List<Reserva> obtenerReservasPorCanchaYFecha(int servicioId, String fechaInicioDia, String fechaFinDia) {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reservas WHERE servicio_id = ? AND estado LIKE '%CONFIRM%'";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, servicioId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String inicioDb = rs.getString("fecha_hora_inicio");
                    String fechaBase = fechaInicioDia.substring(0, 10);
                    
                    if (inicioDb != null && inicioDb.startsWith(fechaBase)) {
                        Reserva r = new Reserva();
                        r.setId(rs.getInt("id"));
                        r.setClienteId(rs.getInt("cliente_id"));
                        r.setServicioId(rs.getInt("servicio_id"));
                        
                        LocalDateTime inicioParsed = LocalDateTime.parse(inicioDb.replace(" ", "T"));
                        LocalDateTime finParsed = LocalDateTime.parse(rs.getString("fecha_hora_fin").replace(" ", "T"));
                        
                        r.setFechaHoraInicio(inicioParsed);
                        r.setFechaHoraFin(finParsed);
                        r.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
                        r.setCodigoCancelacion(rs.getString("codigo_cancelacion"));
                        
                        reservas.add(r);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Error al consultar reservas: " + e.getMessage());
        }

        return reservas;
    }

    public Reserva obtenerPorCodigo(String codigo) {
        Reserva reserva = null;
        String sql = "SELECT * FROM reservas WHERE codigo_cancelacion = ? AND estado != 'CANCELADO'";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    reserva = new Reserva();
                    reserva.setId(rs.getInt("id"));
                    reserva.setClienteId(rs.getInt("cliente_id"));
                    reserva.setServicioId(rs.getInt("servicio_id"));
                    
                    LocalDateTime inicioParsed = LocalDateTime.parse(rs.getString("fecha_hora_inicio").replace(" ", "T"));
                    LocalDateTime finParsed = LocalDateTime.parse(rs.getString("fecha_hora_fin").replace(" ", "T"));

                    reserva.setFechaHoraInicio(inicioParsed);
                    reserva.setFechaHoraFin(finParsed);
                    reserva.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
                    reserva.setCodigoCancelacion(rs.getString("codigo_cancelacion"));
                }
            }

        } catch (Exception e) {
            System.err.println("Error al buscar reserva por codigo: " + e.getMessage());
        }

        return reserva;
    }

    public boolean actualizarEstado(int reservaId, EstadoReserva nuevoEstado) {
        boolean actualizado = false;
        String sql = "UPDATE reservas SET estado = ? WHERE id = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado.name());
            stmt.setInt(2, reservaId);

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                actualizado = true;
            }

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de reserva: " + e.getMessage());
        }

        return actualizado;
    }

    public boolean cancelarPorCodigo(String codigoCancelacion) {
        boolean cancelado = false;
        String sql = "UPDATE reservas SET estado = 'CANCELADO' WHERE codigo_cancelacion = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigoCancelacion);
            
            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                cancelado = true;
            }

        } catch (SQLException e) {
            System.err.println("Error al cancelar la reserva: " + e.getMessage());
        }

        return cancelado;
    }

    public List<Reserva> obtenerTodas() {
    List<Reserva> lista = new ArrayList<>();
    String sql = "SELECT id, cliente_id, servicio_id, fecha_hora_inicio, fecha_hora_fin, estado, codigo_cancelacion FROM reservas";
    
    try (Connection conn = ConexionBD.obtenerConexion();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        
        boolean hayMas = rs.next();
        while (hayMas) {
            Reserva reserva = new Reserva();
            reserva.setId(rs.getInt("id"));
            reserva.setClienteId(rs.getInt("cliente_id"));
            reserva.setServicioId(rs.getInt("servicio_id"));
            
            String inicioStr = rs.getString("fecha_hora_inicio");
            if (inicioStr != null) {
                reserva.setFechaHoraInicio(LocalDateTime.parse(inicioStr.replace(" ", "T")));
            }
            
            String finStr = rs.getString("fecha_hora_fin");
            if (finStr != null) {
                reserva.setFechaHoraFin(LocalDateTime.parse(finStr.replace(" ", "T")));
            }
            
            reserva.setEstado(EstadoReserva.valueOf(rs.getString("estado")));
            reserva.setCodigoCancelacion(rs.getString("codigo_cancelacion"));
            
            lista.add(reserva);
            hayMas = rs.next();
        }
    } catch (SQLException e) {
        System.err.println("Error al obtener todas las reservas: " + e.getMessage());
    }
    
    List<Reserva> resultadoFinal = lista;
    return resultadoFinal;
}
}