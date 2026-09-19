package Back;

public class Servicio {
    int id;
    String nombre;
    String descripcion;
    int duracionMinutos;
    int precio;
    boolean activo;

public Servicio(int id, String nombre, String descripcion, int duracionMinutos, int precio, boolean activo) {
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

    public int getPrecio() {
        return precio;
    }

    public boolean isActivo() {
        return activo;
    }
}
