package Back.dao;

import Back.bd.ConexionBD;
import Back.modelo.HorarioAtencion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class HorarioAtencionDAO {
    private final DateTimeFormatter FORMATO_HORA = new DateTimeFormatterBuilder()
            .appendPattern("[HH:mm:ss][H:mm:ss][HH:mm][H:mm]")
            .toFormatter();

    public HorarioAtencion obtenerPorDia(int diaSemana) {
        String sql = "SELECT * FROM horarios_atencion WHERE dia_semana = ?";
        HorarioAtencion horario = null;

        try (Connection conn = ConexionBD.obtenerConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, diaSemana);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    boolean estaAbierto = rs.getInt("abierto") == 1;
                    
                    if (estaAbierto) {
                        horario = new HorarioAtencion();
                        horario.setId(rs.getInt("id"));
                        horario.setDiaSemana(rs.getInt("dia_semana"));
                        String strApertura = rs.getString("hora_apertura").trim();
                        String strCierre = rs.getString("hora_cierre").trim();
                        horario.setHoraApertura(LocalTime.parse(strApertura, FORMATO_HORA));
                        horario.setHoraCierre(LocalTime.parse(strCierre, FORMATO_HORA));
                        horario.setAbierto(true);
                    }
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar horario de atencion: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error al parsear hora de atencion: " + e.getMessage());
        }

        return horario;
    }
}