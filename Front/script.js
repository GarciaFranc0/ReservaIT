async function cargarHorarios() {
    const servicioId = 1;
    const fecha = '2026-09-24'; 
    const duracion = 60;

    try {
        const respuesta = await fetch(`http://localhost:8080/api/horarios?servicioId=${servicioId}&fecha=${fecha}&duracion=${duracion}`);
        const horarios = await respuesta.json();
        
        console.log("Horarios recibidos del backend:", horarios);
        
        const contenedor = document.getElementById('lista-horarios');
        if (contenedor) {
            contenedor.innerHTML = '';
            let i = 0;
            while (i < horarios.length) {
                const hora = horarios[i];
                const option = document.createElement('option');
                option.value = hora;
                option.textContent = hora;
                contenedor.appendChild(option);
                i++;
            }
        }
    } catch (error) {
        console.error("Error al conectar con el backend:", error);
    }
}

async function enviarReserva(datosReserva) {
    try {
        const respuesta = await fetch('http://localhost:8080/api/reservas', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams(datosReserva)
        });
        
        const resultado = await respuesta.json();
        console.log("Respuesta de creación:", resultado);
    } catch (error) {
        console.error("Error al registrar la reserva:", error);
    }
}
cargarHorarios();