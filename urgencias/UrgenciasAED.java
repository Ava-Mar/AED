package aed.urgencias;

import es.upm.aedlib.Pair;
import java.util.*;

/**
 * Implementación de la interfaz Urgencias con manejo de excepciones.
 */
public class UrgenciasAED implements Urgencias {

    // Mapa para gestionar pacientes por su DNI
    private Map<String, Paciente> pacientes;
    // Cola de prioridad para gestionar pacientes según urgencia
    private PriorityQueue<Paciente> colaPrioridad;
    // Variables para el seguimiento de tiempos de espera y número de pacientes atendidos
    private int sumaTiemposEspera;
    private int numeroPacientesAtendidos;

    // Constructor: inicializa las estructuras y variables internas
    public UrgenciasAED() {
        this.pacientes = new HashMap<>();
        this.colaPrioridad = new PriorityQueue<>();
        this.sumaTiemposEspera = 0;
        this.numeroPacientesAtendidos = 0;
    }

    @Override
    public Paciente admitirPaciente(String DNI, int prioridad, int hora) throws PacienteExisteException {
        // Verifica si ya existe un paciente con el DNI proporcionado
        if (pacientes.containsKey(DNI)) {
            throw new PacienteExisteException();
        }

        // Crea un nuevo paciente y lo agrega al mapa y a la cola de prioridad
        Paciente nuevoPaciente = new Paciente(DNI, prioridad, hora, hora);
        pacientes.put(DNI, nuevoPaciente);
        colaPrioridad.add(nuevoPaciente);
        return nuevoPaciente;
    }

    @Override
    public Paciente salirPaciente(String DNI, int hora) throws PacienteNoExisteException {
        // Intenta eliminar al paciente del mapa
        Paciente paciente = pacientes.remove(DNI);
        if (paciente == null) {
            throw new PacienteNoExisteException(); // Si no existe, lanza una excepción
        }

        // También se elimina de la cola de prioridad
        colaPrioridad.remove(paciente);
        return paciente;
    }

    @Override
    public Paciente cambiarPrioridad(String DNI, int nuevaPrioridad, int hora) throws PacienteNoExisteException {
        // Recupera al paciente del mapa
        Paciente paciente = pacientes.get(DNI);
        if (paciente == null) {
            throw new PacienteNoExisteException(); // Si no existe, lanza una excepción
        }

        // Elimina al paciente de la cola de prioridad para actualizar su información
        colaPrioridad.remove(paciente);

        // Actualiza la prioridad solo si ha cambiado
        if (paciente.getPrioridad() != nuevaPrioridad) {
            paciente.setPrioridad(nuevaPrioridad);
            paciente.setTiempoAdmisionEnPrioridad(hora);
        }

        // Reintroduce al paciente en la cola con la nueva prioridad
        colaPrioridad.add(paciente);
        return paciente;
    }

    @Override
    public Paciente atenderPaciente(int hora) {
        // Si la cola está vacía, no hay pacientes para atender
        if (colaPrioridad.isEmpty()) {
            return null;
        }

        // Se extrae al paciente con mayor prioridad (head de la cola)
        Paciente paciente = colaPrioridad.poll();
        pacientes.remove(paciente.getDNI()); // También se elimina del mapa

        // Calcula el tiempo de espera y lo acumula en la suma total
        int tiempoEspera = hora - paciente.getTiempoAdmision();
        sumaTiemposEspera += tiempoEspera;
        numeroPacientesAtendidos++; // Incrementa el contador de pacientes atendidos

        return paciente;
    }

    @Override
    public void aumentaPrioridad(int maxTiempoEspera, int hora) {
        // Lista temporal para almacenar pacientes que necesitan actualizar su prioridad
        List<Paciente> actualizar = new ArrayList<>();

        // Identifica pacientes cuya prioridad debe incrementarse
        for (Paciente paciente : colaPrioridad) {
            int tiempoEnPrioridad = hora - paciente.getTiempoAdmisionEnPrioridad();
            if (tiempoEnPrioridad > maxTiempoEspera) {
                actualizar.add(paciente);
            }
        }

        // Actualiza la prioridad de los pacientes seleccionados
        for (Paciente paciente : actualizar) {
            colaPrioridad.remove(paciente); // Elimina el paciente de la cola
            int nuevaPrioridad = Math.max(0, paciente.getPrioridad() - 1); // Incrementa la prioridad
            if (paciente.getPrioridad() != nuevaPrioridad) {
                paciente.setPrioridad(nuevaPrioridad);
                paciente.setTiempoAdmisionEnPrioridad(hora);
            }
            colaPrioridad.add(paciente); // Reintroduce al paciente con la nueva prioridad
        }
    }

    @Override
    public Iterable<Paciente> pacientesEsperando() {
        // Crea una lista con los pacientes de la cola de prioridad
        List<Paciente> pacientesOrdenados = new ArrayList<>(colaPrioridad);
        pacientesOrdenados.sort(null); // Ordena usando el método compareTo() de Paciente
        return pacientesOrdenados;
    }

    @Override
    public Paciente getPaciente(String DNI) {
        // Devuelve el paciente asociado al DNI, o null si no existe
        return pacientes.get(DNI);
    }

    @Override
    public Pair<Integer, Integer> informacionEspera() {
        // Devuelve un par con la suma total de tiempos de espera y el número de pacientes atendidos
        return new Pair<>(sumaTiemposEspera, numeroPacientesAtendidos);
    }

}
