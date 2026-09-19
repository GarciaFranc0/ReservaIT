package Back.modelo;

import java.time.*;;

public class HorarioAtencion {
    int id;
    DayOfWeek dia;
    LocalTime horaInicio;
    LocalTime horaFin;
    boolean activo;

public HorarioAtencion(int id, DayOfWeek dia, LocalTime horaInicio, LocalTime horaFin, boolean activo) {
        this.id = id;
        this.dia = dia;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.activo = activo;
    }

public int getId() {
        return id;
    }

    public DayOfWeek getDia() {
        return dia;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public boolean isActivo() {
        return activo;
    }

}
