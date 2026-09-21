package Back.modelo;

public class Servicio {
    int id;
    String nombre;
    String descripcion;
    int duracionMinutos;
    double precio;
    boolean activo;

    public Servicio(int id, String nombre, String descripcion, int duracionMinutos, double precio, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.duracionMinutos = duracionMinutos;
        this.precio = precio;
        this.activo = activo;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public double getPrecio() {
        return precio;
    }

    public boolean isActivo() {
        return activo;
    }
}
