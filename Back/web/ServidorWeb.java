package Back.web;

import Back.modelo.EstadoReserva;
import Back.modelo.Reserva;
import Back.modelo.Servicio;
import Back.modelo.Usuario;
import Back.dao.ReservaDAO;
import Back.dao.ServicioDAO;
import Back.dao.UsuarioDAO;
import Back.servicio.ReservaService;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class ServidorWeb {

    public static void iniciar() throws IOException {
        HttpServer servidor = HttpServer.create(new InetSocketAddress(8080), 0);
        
        servidor.createContext("/api/horarios", new HorariosHandler());
        servidor.createContext("/api/reservas", new ReservasHandler());
        servidor.createContext("/api/cancelar", new CancelarHandler());
        servidor.createContext("/api/listar", new ListarHandler());
        servidor.createContext("/api/servicios", new ServiciosHandler());
        
        servidor.setExecutor(null);
        servidor.start();
        System.out.println("Servidor HTTP nativo iniciado en el puerto 8080...");
    }

    private static void agregarCorsHeaders(HttpExchange exchange) {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
    }

    private static byte[] leerBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        boolean leyendo = true;
        while (leyendo) {
            nRead = is.read(data, 0, data.length);
            if (nRead != -1) {
                buffer.write(data, 0, nRead);
            } else {
                leyendo = false;
            }
        }
        byte[] resultadoFinal = buffer.toByteArray();
        return resultadoFinal;
    }

    private static String decodificar(String texto) {
        String decodificado = "";
        try {
            if (texto != null) {
                decodificado = URLDecoder.decode(texto, "UTF-8");
            }
        } catch (Exception e) {
            decodificado = texto;
        }
        String resultadoFinal = decodificado;
        return resultadoFinal;
    }

    private static Map<String, String> parsearQuery(String query) {
        Map<String, String> resultado = new HashMap<>();
        if (query != null && !query.isEmpty()) {
            String[] pares = query.split("&");
            int i = 0;
            while (i < pares.length) {
                String par = pares[i];
                String[] keyValue = par.split("=");
                if (keyValue.length > 1) {
                    resultado.put(decodificar(keyValue[0]), decodificar(keyValue[1]));
                } else if (keyValue.length == 1) {
                    resultado.put(decodificar(keyValue[0]), "");
                }
                i++;
            }
        }
        Map<String, String> resultadoFinal = resultado;
        return resultadoFinal;
    }

    static class HorariosHandler implements HttpHandler {
        private final ReservaService reservaService = new ReservaService();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            agregarCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                String query = exchange.getRequestURI().getQuery();
                Map<String, String> params = parsearQuery(query);
                
                int servicioId = Integer.parseInt(params.getOrDefault("servicioId", "1"));
                LocalDate fecha = LocalDate.parse(params.getOrDefault("fecha", LocalDate.now().toString()));
                int duracion = Integer.parseInt(params.getOrDefault("duracion", "60"));

                List<LocalTime> horarios = reservaService.obtenerHorariosDisponibles(servicioId, fecha, duracion);
                
                StringBuilder json = new StringBuilder("[");
                int i = 0;
                while (i < horarios.size()) {
                    json.append("\"").append(horarios.get(i).toString()).append("\"");
                    if (i < horarios.size() - 1) {
                        json.append(",");
                    }
                    i++;
                }
                json.append("]");

                byte[] respuesta = json.toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, respuesta.length);
                OutputStream os = exchange.getResponseBody();
                os.write(respuesta);
                os.close();
            }
            return;
        }
    }

    static class ReservasHandler implements HttpHandler {
        private final ReservaService reservaService = new ReservaService();
        private final UsuarioDAO usuarioDAO = new UsuarioDAO();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            agregarCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    byte[] bytesBody = leerBytes(exchange.getRequestBody());
                    String body = new String(bytesBody, StandardCharsets.UTF_8);
                    Map<String, String> datos = parsearQuery(body);

                    String nombre = datos.getOrDefault("nombre", "Cliente");
                    String apellido = datos.getOrDefault("apellido", "Web");
                    String correo = datos.getOrDefault("correo", "cliente@email.com");
                    String telefono = datos.getOrDefault("telefono", "00000000");
                    int servicioId = Integer.parseInt(datos.getOrDefault("servicioId", "1"));
                    String fechaHoraInicioStr = datos.getOrDefault("fechaHoraInicio", LocalDateTime.now().toString());

                    Usuario usuario = usuarioDAO.obtenerOCrear(new Usuario(0, nombre, apellido, correo, telefono));
                    LocalDateTime inicio = LocalDateTime.parse(fechaHoraInicioStr.replace(" ", "T"));
                    LocalDateTime fin = inicio.plusMinutes(60);
                    String codigo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                    Reserva reserva = new Reserva();
                    reserva.setClienteId(usuario.getId());
                    reserva.setServicioId(servicioId);
                    reserva.setFechaHoraInicio(inicio);
                    reserva.setFechaHoraFin(fin);
                    reserva.setEstado(EstadoReserva.CONFIRMADA);
                    reserva.setCodigoCancelacion(codigo);

                    boolean creada = reservaService.crearReserva(reserva);

                    String jsonRespuesta = "{\"exito\": " + creada + ", \"codigo\": \"" + codigo + "\"}";
                    byte[] respuesta = jsonRespuesta.getBytes(StandardCharsets.UTF_8);
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(creada ? 201 : 400, respuesta.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(respuesta);
                    os.close();
                }
            }
            return;
        }
    }

    static class CancelarHandler implements HttpHandler {
        private final ReservaService reservaService = new ReservaService();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            agregarCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                    byte[] bytesBody = leerBytes(exchange.getRequestBody());
                    String body = new String(bytesBody, StandardCharsets.UTF_8);
                    Map<String, String> datos = parsearQuery(body);
                    String codigo = datos.getOrDefault("codigo", "");

                    boolean cancelado = reservaService.cancelarReserva(codigo);

                    String jsonRespuesta = "{\"cancelado\": " + cancelado + "}";
                    byte[] respuesta = jsonRespuesta.getBytes(StandardCharsets.UTF_8);
                    
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, respuesta.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(respuesta);
                    os.close();
                }
            }
            return;
        }
    }

    static class ListarHandler implements HttpHandler {
        private final ReservaDAO reservaDAO = new ReservaDAO();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            agregarCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                List<Reserva> lista = reservaDAO.obtenerTodas();
                
                StringBuilder json = new StringBuilder("[");
                int i = 0;
                while (i < lista.size()) {
                    Reserva r = lista.get(i);
                    json.append("{");
                    json.append("\"id\":").append(r.getId()).append(",");
                    json.append("\"inicio\":\"").append(r.getFechaHoraInicio()).append("\",");
                    json.append("\"estado\":\"").append(r.getEstado()).append("\",");
                    json.append("\"codigo\":\"").append(r.getCodigoCancelacion()).append("\"");
                    json.append("}");
                    if (i < lista.size() - 1) {
                        json.append(",");
                    }
                    i++;
                }
                json.append("]");

                byte[] respuesta = json.toString().getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                exchange.sendResponseHeaders(200, respuesta.length);
                OutputStream os = exchange.getResponseBody();
                os.write(respuesta);
                os.close();
            }
            return;
        }
    }

    static class ServiciosHandler implements HttpHandler {
        private final ServicioDAO servicioDAO = new ServicioDAO();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            agregarCorsHeaders(exchange);
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                try {
                    List<Servicio> lista = servicioDAO.obtenerTodos();
                    
                    StringBuilder json = new StringBuilder("[");
                    int i = 0;
                    while (i < lista.size()) {
                        Servicio s = lista.get(i);
                        json.append("{");
                        json.append("\"id\":").append(s.getId()).append(",");
                        json.append("\"nombre\":\"").append(s.getNombre()).append("\"");
                        json.append("}");
                        if (i < lista.size() - 1) {
                            json.append(",");
                        }
                        i++;
                    }
                    json.append("]");

                    byte[] respuesta = json.toString().getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
                    exchange.sendResponseHeaders(200, respuesta.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(respuesta);
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace(); 
                    String errorMsg = "{\"error\": \"" + e.getMessage() + "\"}";
                    byte[] respuesta = errorMsg.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(500, respuesta.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(respuesta);
                    os.close();
                }
            }
            return;
        }
    }
}