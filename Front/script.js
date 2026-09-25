window.cancelarReserva = function(codigo) {
    alert("¡Hola! La función funciona perfectamente para la reserva: " + codigo);
};

async function cargarHorarios() {
    const servicioId = document.getElementById('select-servicio').value || 1;
    const fechaInput = document.getElementById('input-fecha');
    const fecha = fechaInput ? fechaInput.value : new Date().toISOString().split('T')[0];
    const duracion = 60;

    try {
        const respuesta = await fetch(`/api/horarios?servicioId=${servicioId}&fecha=${fecha}&duracion=${duracion}`);
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

async function enviarReserva(event, datosReserva) {
    if (event && typeof event.preventDefault === 'function') {
        event.preventDefault();
    }

    try {
        const respuesta = await fetch('/api/reservas', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: new URLSearchParams(datosReserva)
        });

        const resultado = await respuesta.json();
        console.log("Respuesta completa del servidor:", resultado);
        
        if (resultado.exito) {
            const codigo = resultado.codigo;
            localStorage.setItem("reservaExitosa", "true");
            localStorage.setItem("codigoReserva", codigo);
            localStorage.setItem("telefonoReserva", datosReserva.telefono);
            localStorage.setItem("nombreReserva", datosReserva.nombre);
            localStorage.setItem("fechaReserva", datosReserva.fechaHoraInicio);

            location.reload();
        } else {
            alert("No se pudo completar la reserva. El horario ya no está disponible.");
        }

    } catch (error) {
        console.error("¡ERROR CAPTURADO EN EL CATCH!:", error);
        alert("Hubo un error de conexión. Revisá la consola (F12).");
    }
}

async function cancelarReserva(codigo) {
    if (!confirm("¿Estás seguro de que querés cancelar la reserva " + codigo + "?")) {
        return;
    }
    try {
        const respuesta = await fetch(`/api/cancelar?codigo=${codigo}`, {
            method: 'POST'
        }); 
        const resultado = await respuesta.json();
        
        if (resultado.exito || respuesta.ok) {
            alert("¡Reserva cancelada con éxito!");
            cargarReservas();
        } else {
            alert("No se pudo cancelar la reserva.");
        }
    } catch (error) {
        console.error("Error al cancelar:", error);
    }
}

const inputFecha = document.getElementById('input-fecha');
if (inputFecha) {
    inputFecha.value = new Date().toISOString().split('T')[0];
    inputFecha.addEventListener('change', cargarHorarios);
    cargarHorarios(); 
}

const btnEnviar = document.getElementById('btn-enviar-reserva');
if (btnEnviar) {
    btnEnviar.addEventListener('click', function(e) {
        console.log("--> 1. El botón fue presionado, la página NO debería recargarse.");
        const datosReserva = {
            nombre: document.getElementById('input-nombre').value,
            apellido: document.getElementById('input-apellido').value,
            correo: document.getElementById('input-correo').value,
            telefono: document.getElementById('input-telefono').value,
            servicioId: parseInt(document.getElementById('select-servicio').value) || 1,
            fechaHoraInicio: document.getElementById('input-fecha').value + 'T' + document.getElementById('select-horarios').value
        };

        enviarReserva(null, datosReserva);
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
}
async function cargarReservas() {
    try {
        const respuesta = await fetch('/api/listar');
        const reservas = await respuesta.json();

        const tbody = document.getElementById('cuerpo-tabla-reservas');
        
        if (tbody) {
            let html = '';
            
            reservas.forEach(r => {
                console.log("Objeto reserva completo:", r);
                if (r.estado === 'CANCELADO') return;

                html += `
                    <tr>
                        <td>${r.codigo}</td>
                        <td>${r.inicio}</td>
                        <td>${r.nombreServicio}</td>
                        <td>${r.estado}</td>
                        <td>
                            ${r.estado === 'CONFIRMADA' ? `<button class="btn-cancelar" onclick="cancelarReserva('${r.codigo}')">Cancelar</button>` : 'N/A'}
                        </td>
                    </tr>
                `;
            });
            
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
        const respuesta = await fetch(`/api/cancelar?codigo=${codigo}`, {
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
        const respuesta = await fetch('/api/servicios');
        const servicios = await respuesta.json();

        console.log("Servicios recibidos del backend:", servicios);
        
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

function enviarWhatsApp(datos, codigo) {
    const telefono = datos.telefono || "";
    const nombre = datos.nombre || "Cliente";
    const fechaHora = datos.fechaHoraInicio || "Fecha y hora"; 

    if (!telefono) return;

    const mensaje = `¡Hola *${nombre}*! 👋 Te confirmamos tu reserva. 🏟️\n\n` +
                    `📅 *Fecha y Hora:* ${fechaHora}\n` +
                    `🔑 *Código de cancelación:* ${codigo}\n\n` +
                    `¡Te esperamos!`;

    const telefonoLimpiado = telefono.replace(/\D/g, '');
    const urlWhatsApp = `https://wa.me/${telefonoLimpiado}?text=${encodeURIComponent(mensaje)}`;
    
    let bannerWpp = document.getElementById('banner-whatsapp-flotante');
    if (!bannerWpp) {
        bannerWpp = document.createElement('div');
        bannerWpp.id = 'banner-whatsapp-flotante';
        bannerWpp.style.cssText = "position: fixed; bottom: 20px; right: 20px; background: #ffffff; border: 3px solid #25d366; padding: 20px; border-radius: 12px; box-shadow: 0 5px 25px rgba(0,0,0,0.3); z-index: 999999; text-align: max-content; max-width: 320px;";
        document.body.appendChild(bannerWpp);
    }

    bannerWpp.innerHTML = `
        <p style="margin: 0 0 5px 0; font-weight: bold; color: #137333; font-size: 15px;">¡Reserva creada con éxito! 🎉</p>
        <p style="margin: 0 0 12px 0; font-size: 13px; color: #555;">Tu código es: <b>${codigo}</b></p>
        <a href="${urlWhatsApp}" target="_blank" style="background: #25d366; color: white; padding: 10px 15px; text-decoration: none; border-radius: 6px; display: block; text-align: center; font-weight: bold; font-size: 14px;">
            💬 Enviar WhatsApp
        </a>
    `;
    
    bannerWpp.style.display = 'block';
}
function obtenerNombreCancha(idServicio) {
    const id = Number(idServicio);
    if (id === 1) return "Cancha de Sintético";
    if (id === 2) return "Cancha de Futsal";
    if (id === 3) return "Cancha de Cesped Natural";
    return "Cancha Principal";
}

window.cancelarReserva = async function(codigo) {
    console.log("¡Se hizo clic en cancelar para el código:", codigo); 

    if (!confirm("¿Estás seguro de que querés cancelar la reserva " + codigo + "?")) {
        return;
    }

    try {
        const respuesta = await fetch(`/api/cancelar?codigo=${codigo}`, {
            method: 'POST'
        });
        
        const resultado = await respuesta.json();
        
        if (resultado.cancelado) {
            alert("¡Reserva cancelada con éxito!");
            cargarReservas();
        } else {
            alert("No se pudo cancelar la reserva.");
        }
    } catch (error) {
        console.error("Error al cancelar:", error);
    }
};

document.getElementById('select-servicio').addEventListener('change', cargarHorarios);

window.cancelarReservaDesdeTabla = cancelarReservaDesdeTabla;

window.addEventListener('DOMContentLoaded', () => {
    if (typeof cargarServicios === 'function') cargarServicios();
    if (typeof cargarReservas === 'function') cargarReservas();

    try {
        if (localStorage.getItem("reservaExitosa") === "true") {
            const codigo = localStorage.getItem("codigoReserva");
            const telefono = localStorage.getItem("telefonoReserva");
            const nombre = localStorage.getItem("nombreReserva");
            const fechaHora = localStorage.getItem("fechaReserva");

            if (codigo && telefono) {
                enviarWhatsApp({ nombre, telefono, fechaHoraInicio: fechaHora }, codigo);
            }
            localStorage.removeItem("reservaExitosa");
        }
    } catch (e) {
        console.error("Error al restaurar el banner de WhatsApp:", e);
    }
});