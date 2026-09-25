🏟️ ReservaIT - Sistema Web de Gestión de Reservas

Proyecto web full-stack desarrollado desde cero para la gestión integral de reservas de canchas, combinando un backend robusto en Java nativo con una interfaz moderna y atractiva.

🚀 Tecnologías Utilizadas

* **Backend:** Java (Uso de `HttpServer` y `HttpHandler` nativos, sin frameworks pesados).
* **Base de Datos:** SQLite.
* **Frontend:** HTML5, CSS3 y JavaScript (Vanilla JS con peticiones `fetch`).
* **Despliegue y Pruebas:** Ngrok.

📂 Estructura del Proyecto
```text
ReservaIT/
│
├── Back/           # Lógica de conexión a base de datos y controladores
├── Front/          # Archivos estáticos (index.html, styles.css, script.js)
├── img/            # Recursos visuales e imágenes de la aplicación
├── lib/            # Librerías externas (JDBC SQLite, etc.)
├── db.db           # Archivo de la base de datos SQLite
└── Main.java       # Punto de entrada y configuración del servidor HTTP nativo
```
✨ Características Principales
* Servidor HTTP nativo en Java que maneja tanto las rutas de la API REST como la entrega de archivos estáticos.

* Conexión a base de datos relacional SQLite para el almacenamiento persistente de canchas, turnos y reservas.

* Interfaz de usuario responsiva e interactiva con actualización dinámica de datos mediante JavaScript.

* Exposición del entorno local mediante Ngrok para pruebas públicas y demostraciones en tiempo real.

⚙️ Cómo ejecutar el proyecto localmente
* Clona este repositorio en tu computadora.

* Asegúrate de tener instalado el JDK de Java y las librerías necesarias en la carpeta lib.

* Ejecuta el archivo principal Main.java para levantar el servidor en el puerto 8080.

* Abre tu navegador e ingresa a http://localhost:8080.
