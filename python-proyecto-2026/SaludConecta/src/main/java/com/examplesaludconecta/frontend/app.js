const loginForm = document.getElementById("loginForm");
const mensaje = document.getElementById("mensaje");

const loginSection = document.getElementById("loginSection");
const dashboardSection = document.getElementById("dashboardSection");
const usuarioActivo = document.getElementById("usuarioActivo");
const logoutBtn = document.getElementById("logoutBtn");

const URL_LOGIN = "http://localhost:8081/api/auth/login";

document.addEventListener("DOMContentLoaded", () => {
  const token = localStorage.getItem("token");
  const email = localStorage.getItem("email");

  if (token) {
    mostrarDashboard(email);
  }
});

loginForm.addEventListener("submit", async function (event) {
  event.preventDefault();

  mensaje.textContent = "Iniciando sesión...";
  mensaje.style.color = "#0b6b8f";

  const email = document.getElementById("email").value;
  const password = document.getElementById("password").value;

  const datosLogin = {
    email: email,
    password: password
  };

  try {
    const respuesta = await fetch(URL_LOGIN, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(datosLogin)
    });

    const data = await respuesta.json();

    if (!respuesta.ok) {
      mensaje.textContent = data.Error || "Credenciales incorrectas";
      mensaje.style.color = "red";
      return;
    }

    localStorage.setItem("token", data.token);
    localStorage.setItem("email", email);

    mostrarDashboard(email);

  } catch (error) {
    console.error("Error al conectar con el backend:", error);
    mensaje.textContent = "No se pudo conectar con el servidor";
    mensaje.style.color = "red";
  }
});

function mostrarDashboard(email) {
  loginSection.style.display = "none";
  dashboardSection.style.display = "block";

  usuarioActivo.textContent = `Sesión iniciada como: ${email}`;
}

logoutBtn.addEventListener("click", () => {
  localStorage.removeItem("token");
  localStorage.removeItem("email");

  dashboardSection.style.display = "none";
  loginSection.style.display = "flex";

  mensaje.textContent = "";
  loginForm.reset();
});

const btnVerPacientes = document.getElementById("btnVerPacientes");
const pacientesSection = document.getElementById("pacientesSection");
const tbodyPacientes = document.getElementById("tbodyPacientes");
const mensajePacientes = document.getElementById("mensajePacientes");

const URL_PACIENTES = "http://localhost:8080/api/pacientes";

btnVerPacientes.addEventListener("click", cargarPacientes);

async function cargarPacientes() {
  pacientesSection.style.display = "block";
  mensajePacientes.textContent = "Cargando pacientes...";
  mensajePacientes.style.color = "#0b6b8f";
  tbodyPacientes.innerHTML = "";

  try {
    const respuesta = await fetch(URL_PACIENTES);

    if (!respuesta.ok) {
      mensajePacientes.textContent = "No se pudieron cargar los pacientes";
      mensajePacientes.style.color = "red";
      return;
    }

    const pacientes = await respuesta.json();

    if (pacientes.length === 0) {
      mensajePacientes.textContent = "No hay pacientes registrados";
      mensajePacientes.style.color = "#5f7f8f";
      return;
    }

    mensajePacientes.textContent = "";

    pacientes.forEach(paciente => {
      const fila = document.createElement("tr");

      fila.innerHTML = `
        <td>${paciente.id}</td>
        <td>${paciente.nombre}</td>
        <td>${paciente.apellido}</td>
        <td>${paciente.rut}</td>
        <td>${paciente.correo || "Sin correo"}</td>
        <td>${paciente.telefono || "Sin teléfono"}</td>
      `;

      tbodyPacientes.appendChild(fila);
    });

  } catch (error) {
    console.error("Error al cargar pacientes:", error);
    mensajePacientes.textContent = "Error al conectar con SaludConecta";
    mensajePacientes.style.color = "red";
  }
  const btnRegistrarPaciente = document.getElementById("btnRegistrarPaciente");
const registroPacienteSection = document.getElementById("registroPacienteSection");
const pacienteForm = document.getElementById("pacienteForm");
const mensajeRegistro = document.getElementById("mensajeRegistro");

btnRegistrarPaciente.addEventListener("click", () => {
  registroPacienteSection.style.display = "block";
});

pacienteForm.addEventListener("submit", async function (event) {
  event.preventDefault();

  mensajeRegistro.textContent = "Guardando paciente...";
  mensajeRegistro.style.color = "#0b6b8f";

  const paciente = {
    nombre: document.getElementById("nombrePaciente").value,
    apellido: document.getElementById("apellidoPaciente").value,
    rut: document.getElementById("rutPaciente").value,
    correo: document.getElementById("correoPaciente").value,
    telefono: document.getElementById("telefonoPaciente").value
  };

  try {
    const respuesta = await fetch("http://localhost:8080/api/pacientes", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(paciente)
    });

    const data = await respuesta.json();

    if (!respuesta.ok) {
      mensajeRegistro.textContent = "Error al registrar paciente";
      mensajeRegistro.style.color = "red";
      console.error(data);
      return;
    }

    mensajeRegistro.textContent = "Paciente registrado correctamente";
    mensajeRegistro.style.color = "green";

    pacienteForm.reset();

    cargarPacientes();

  } catch (error) {
    console.error("Error al registrar paciente:", error);
    mensajeRegistro.textContent = "Error al conectar con SaludConecta";
    mensajeRegistro.style.color = "red";
  }
});
}