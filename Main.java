import Back.web.ServidorWeb;

public class Main {
    public static void main(String[] args) {
        try {
            ServidorWeb.iniciar();
        } catch (Exception e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}