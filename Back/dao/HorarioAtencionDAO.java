package Back.dao;

import Back.bd.ConexionBD;
import Back.modelo.HorarioAtencion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public class HorarioAtencionDAO {

    public HorarioAtencion obtenerPorDia(int diaSemana) {
        String sql = "SELECT * FROM horarios_atencion WHERE dia_semana = ?";

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, diaSemana);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean estaAbierto = rs.getInt("abierto") == 1;
                    
                    if (!estaAbierto) {
                        return null;
                    }

                    HorarioAtencion horario = new HorarioAtencion();
                    horario.setId(rs.getInt("id"));
                    horario.setDiaSemana(rs.getInt("dia_semana"));
                    horario.setHoraApertura(LocalTime.parse(rs.getString("hora_apertura")));
                    horario.setHoraCierre(LocalTime.parse(rs.getString("hora_cierre")));
                    horario.setAbierto(true);

                    return horario;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar horario de atencion: " + e.getMessage());
        }

        return null;
    }
}