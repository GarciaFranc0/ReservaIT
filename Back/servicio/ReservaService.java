package Back.servicio;

import Back.dao.HorarioAtencionDAO;
import Back.dao.ReservaDAO;
import Back.modelo.EstadoReserva;
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
            String inicioDiaStr = fecha.toString() + "T00:00";
            String finDiaStr = fecha.toString() + "T23:59:59.999"; 
            List<Reserva> reservasOcupadas = reservaDAO.obtenerReservasPorCanchaYFecha(servicioId, inicioDiaStr, finDiaStr);
            reservasOcupadas.removeIf(r -> r.getEstado() != null && r.getEstado().name().equals("CANCELADO"));
            LocalDateTime slotActual = LocalDateTime.of(fecha, horarioAtencion.getHoraApertura());
            LocalDateTime limiteCierre = LocalDateTime.of(fecha, horarioAtencion.getHoraCierre());
            LocalDateTime ahora = LocalDateTime.now();

            while (slotActual.plusMinutes(duracionMinutos).isBefore(limiteCierre) || slotActual.plusMinutes(duracionMinutos).isEqual(limiteCierre)) {
                LocalDateTime finSlot = slotActual.plusMinutes(duracionMinutos);
                boolean estaOcupado = false;
                
                if (slotActual.isAfter(ahora)) {
                    int i = 0;
                    while (i < reservasOcupadas.size() && !estaOcupado) {
                        Reserva reserva = reservasOcupadas.get(i);
                        boolean solapa = slotActual.isBefore(reserva.getFechaHoraFin()) && finSlot.isAfter(reserva.getFechaHoraInicio());
                        if (solapa) {
                            estaOcupado = true;
                        }
                        i++;
                    }
                    if (!estaOcupado) {
                        horariosDisponibles.add(slotActual.toLocalTime());
                    }
                }
                slotActual = slotActual.plusMinutes(duracionMinutos);
            }
        }
        return horariosDisponibles;
    }

    public boolean crearReserva(Reserva reserva) {
        boolean creada = false;
        
        if (reserva != null && reserva.getFechaHoraInicio() != null && reserva.getFechaHoraFin() != null) {
            LocalDate fecha = reserva.getFechaHoraInicio().toLocalDate();
            int diaSemana = fecha.getDayOfWeek().getValue();
            HorarioAtencion horarioAtencion = horarioDAO.obtenerPorDia(diaSemana);

            if (horarioAtencion != null && horarioAtencion.isAbierto()) {
                LocalDateTime aperturaDia = LocalDateTime.of(fecha, horarioAtencion.getHoraApertura());
                LocalDateTime cierreDia = LocalDateTime.of(fecha, horarioAtencion.getHoraCierre());
                LocalDateTime ahora = LocalDateTime.now();

                boolean esValidoEnHorario = (reserva.getFechaHoraInicio().isEqual(aperturaDia) || reserva.getFechaHoraInicio().isAfter(aperturaDia)) &&
                                            (reserva.getFechaHoraFin().isEqual(cierreDia) || reserva.getFechaHoraFin().isBefore(cierreDia) || reserva.getFechaHoraFin().isEqual(cierreDia));
                
                boolean esFuturo = reserva.getFechaHoraInicio().isAfter(ahora);

                if (esValidoEnHorario && esFuturo) {
                    List<LocalTime> disponibles = obtenerHorariosDisponibles(reserva.getServicioId(), fecha, (int) java.time.Duration.between(reserva.getFechaHoraInicio(), reserva.getFechaHoraFin()).toMinutes());
                    LocalTime horaInicioSolicitada = reserva.getFechaHoraInicio().toLocalTime();

                    if (disponibles.contains(horaInicioSolicitada)) {
                        creada = reservaDAO.guardar(reserva);
                    }
                }
            }
        }
        return creada;
    }

    public Reserva buscarReservaPorCodigo(String codigo) {
        Reserva reserva = null;
        if (codigo != null && !codigo.trim().isEmpty()) {
            reserva = reservaDAO.obtenerPorCodigo(codigo.trim().toUpperCase());
        }
        return reserva;
    }

    public boolean cancelarReserva(String codigo) {
        boolean exito = false;
        Reserva reserva = buscarReservaPorCodigo(codigo);

        if (reserva != null && reserva.getEstado() != EstadoReserva.CANCELADO) {
            exito = reservaDAO.actualizarEstado(reserva.getId(), EstadoReserva.CANCELADO);
        }

        return exito;
    } 
}