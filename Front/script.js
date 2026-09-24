async function cargarHorarios() {
    const servicioId = document.getElementById('select-servicio').value || 1;
    const fechaInput = document.getElementById('input-fecha');
    const fecha = fechaInput ? fechaInput.value : new Date().toISOString().split('T')[0];
    const duracion = 60;

    try {
        const respuesta = await fetch(`http://localhost:8080/api/horarios?servicioId=${servicioId}&fecha=${fecha}&duracion=${duracion}`);
        const horarios = await respuesta.json();
        
        const select = document.getElementById('select-horarios');
        if (select) {
            select.innerHTML = '';
            let i = 0;
            while (i < horarios.length) {
                const hora = horarios[i];
                const option = document.createElement('option');
                option.value = hora;
                option.textContent = hora;
                select.appendChild(option);
                i++;
            }
        }
    } catch (error) {
        console.error("Error al cargar horarios:", error);
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
        if (resultado.exito) {
            alert("¡Reserva creada con éxito! Tu código de cancelación es: " + resultado.codigo);
            cargarReservas();
        } else {
            alert("No se pudo completar la reserva. El horario ya no está disponible.");
        }
    } catch (error) {
        console.error("Error al enviar la reserva:", error);
    }
}

async function cancelarReserva(codigo) {
    if (!confirm("¿Estás seguro de que querés cancelar la reserva con código " + codigo + "?")) {
        return;
    }
    try {
        const respuesta = await fetch(`http://localhost:8080/api/cancelar?codigo=${codigo}`, {
            method: 'POST'
        });
        
        const resultado = await respuesta.json();
        
        if (resultado.exito || respuesta.ok) {
            alert("¡Reserva cancelada con éxito!");
            cargarReservas();
        } else {
            alert("No se pudo procesar la cancelación.");
        }
    } catch (error) {
        console.error("Error en la petición de cancelación:", error);
    }
}

const inputFecha = document.getElementById('input-fecha');
if (inputFecha) {
    inputFecha.value = new Date().toISOString().split('T')[0];
    inputFecha.addEventListener('change', cargarHorarios);
    cargarHorarios(); 
}

const formReserva = document.getElementById('form-reserva');
if (formReserva) {
    formReserva.addEventListener('submit', function(e) {
        e.preventDefault();

        const datosReserva = {
            nombre: document.getElementById('input-nombre').value,
            apellido: document.getElementById('input-apellido').value,
            correo: document.getElementById('input-correo').value,
            telefono: document.getElementById('input-telefono').value,
            servicioId: 1,
            fechaHoraInicio: document.getElementById('input-fecha').value + 'T' + document.getElementById('select-horarios').value
        };

        enviarReserva(datosReserva);
    });
}
const btnCancelar = document.getElementById('btn-cancelar');
if (btnCancelar) {
    btnCancelar.addEventListener('click', function() {
        const codigo = document.getElementById('input-codigo-cancelar').value;
        if (codigo.trim() !== '') {
            cancelarReserva(codigo.trim());
        } else {
            alert("Por favor, ingresá un código de cancelación.");
        }
    });

    async function cargarReservas() {
    try {
        const respuesta = await fetch('http://localhost:8080/api/listar');
        const reservas = await respuesta.json();
        
        const tbody = document.querySelector('table tbody') || document.querySelector('table'); 
        if (tbody) {
            let html = `
                <tr>
                    <th>Código</th>
                    <th>Fecha y Hora</th>
                    <th>Estado</th>
                    <th>Acción</th>
                </tr>
            `;
            
            let i = 0;
            while (i < reservas.length) {
                const r = reservas[i];
                html += `
                    <tr>
                        <td>${r.codigo}</td>
                        <td>${r.inicio}</td>
                        <td>${r.estado}</td>
                        <td>
                            ${r.estado === 'CONFIRMADA' ? `<button onclick="cancelarReserva('${r.codigo}')">Cancelar</button>` : 'N/A'}
                        </td>
                    </tr>
                `;
                i++;
            }
            tbody.innerHTML = html;
        }
    } catch (error) {
        console.error("Error al listar reservas:", error);
    }
}

async function cancelarReservaDesdeTabla(codigo) {
    if (!confirm("¿Estás seguro de que querés cancelar esta reserva?")) {
        return;
    }

    try {
        const respuesta = await fetch(`http://localhost:8080/api/cancelar?codigo=${codigo}`, {
            method: 'POST'
        });
        
        const resultado = await respuesta.json();
        
        if (resultado.exito || respuesta.ok) {
            alert("Reserva cancelada con éxito.");
            cargarReservas();
        } else {
            alert("No se pudo cancelar la reserva.");
        }
    } catch (error) {
        console.error("Error al cancelar la reserva:", error);
    }
}

async function cargarServicios() {
    try {
        const respuesta = await fetch('http://localhost:8080/api/servicios');
        const servicios = await respuesta.json();
        
        const select = document.getElementById('select-servicio');
        if (select) {
            select.innerHTML = '';
            let i = 0;
            while (i < servicios.length) {
                const s = servicios[i];
                const option = document.createElement('option');
                option.value = s.id;
                option.textContent = s.nombre;
                select.appendChild(option);
                i++;
            }
            cargarHorarios();
        }
    } catch (error) {
        console.error("Error al cargar servicios:", error);
    }
}

document.getElementById('select-servicio').addEventListener('change', cargarHorarios);

window.cancelarReservaDesdeTabla = cancelarReservaDesdeTabla;
window.cancelarReserva = cancelarReserva;
window.addEventListener('DOMContentLoaded', () => {
    cargarServicios();
    cargarReservas();
});

}