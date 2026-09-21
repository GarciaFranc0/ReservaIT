package Back.servicio;

import Back.dao.HorarioAtencionDAO;
import Back.dao.ReservaDAO;
import Back.modelo.HorarioAtencion;
import Back.modelo.Reserva;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaService {

    private final ReservaDAO reservaDAO;
    private final HorarioAtencionDAO horarioDAO;

    public ReservaService() {
        this.reservaDAO = new ReservaDAO();
        this.horarioDAO = new HorarioAtencionDAO();
    }

    public List<LocalTime> obtenerHorariosDisponibles(int servicioId, LocalDate fecha, int duracionMinutos) {
        List<LocalTime> horariosDisponibles = new ArrayList<>();
        int diaSemana = fecha.getDayOfWeek().getValue();
        HorarioAtencion horarioAtencion = horarioDAO.obtenerPorDia(diaSemana);

        if (horarioAtencion != null && horarioAtencion.isAbierto()) {
            String inicioDia = fecha.atStartOfDay().toString();
            String finDia = fecha.atTime(LocalTime.MAX).toString();
            List<Reserva> reservasOcupadas = reservaDAO.obtenerReservasPorCanchaYFecha(servicioId, inicioDia, finDia);

            LocalTime horaActual = horarioAtencion.getHoraApertura();
            LocalTime horaCierre = horarioAtencion.getHoraCierre();

            while (horaActual.plusMinutes(duracionMinutos).isBefore(horaCierre) || 
                   horaActual.plusMinutes(duracionMinutos).equals(horaCierre)) {

                LocalDateTime inicioSlot = LocalDateTime.of(fecha, horaActual);
                LocalDateTime finSlot = inicioSlot.plusMinutes(duracionMinutos);
                boolean estaOcupado = false;
                int i = 0;
                while (i < reservasOcupadas.size() && !estaOcupado) {
                    Reserva reserva = reservasOcupadas.get(i);
                    boolean solapa = inicioSlot.isBefore(reserva.getFechaHoraFin()) && 
                                     finSlot.isAfter(reserva.getFechaHoraInicio());
                    if (solapa) {
                        estaOcupado = true;
                    }
                    i++;
                }

                if (!estaOcupado) {
                    horariosDisponibles.add(horaActual);
                }
                horaActual = horaActual.plusMinutes(duracionMinutos);
            }
        }
        return horariosDisponibles;
    }
}