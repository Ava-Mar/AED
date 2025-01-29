package aed.urgencias;

/**
 * Un paciente.
 */
public class Paciente implements Comparable<Paciente> {

	// DNI
	private String DNI;
	// Prioridad
	private int prioridad;
	// Tiempo de admision en las urgencias
	private int tiempoAdmision;
	// Tiempo cuando entro en la prioridad
	private int tiempoAdmisionEnPrioridad;

	/**
	 * Constructor.
	 */
	public Paciente(String DNI, int prioridad, int tiempoAdmision, int tiempoAdmisionEnPrioridad) {
		this.DNI = DNI;
		this.prioridad = prioridad;
		this.tiempoAdmision = tiempoAdmision;
		this.tiempoAdmisionEnPrioridad = tiempoAdmisionEnPrioridad;
	}

	/**
	 * Devuelve el dni.
	 * 
	 * @return el dni.
	 */
	public String getDNI() {
		return DNI;
	}

	/**
	 * Devuelve la prioridad.
	 * 
	 * @return la prioridad.
	 */
	public int getPrioridad() {
		return prioridad;
	}

	/**
	 * Devuelve el tiempo de admision.
	 * 
	 * @return el tiempo de admision.
	 */
	public int getTiempoAdmision() {
		return tiempoAdmision;
	}

	/**
	 * Devuelve el tiempo de admision en la prioridad actual.
	 * 
	 * @return el tiempo de admision en la prioridad actual.
	 */
	public int getTiempoAdmisionEnPrioridad() {
		return tiempoAdmisionEnPrioridad;
	}

	/**
	 * Asigna una prioridad nueva.
	 * 
	 * @return la prioridad antigua.
	 */
	public int setPrioridad(int prioridadNuevo) {
		int oldPrioridad = prioridad;
		prioridad = prioridadNuevo;
		return oldPrioridad;
	}

	/**
	 * Asigna un nuevo tiempo de admision en prioridad.
	 * 
	 * @return el tiempo de admision en prioridad antigua.
	 */
	public int setTiempoAdmisionEnPrioridad(int tiempoNuevo) {
		int oldTiempo = tiempoAdmisionEnPrioridad;
		tiempoAdmisionEnPrioridad = tiempoNuevo;
		return oldTiempo;
	}

	@Override
	public String toString() {
		return "<\"" + DNI.toString() + "\"," + prioridad + "," + tiempoAdmision + "," + tiempoAdmisionEnPrioridad
				+ ">";
	}

	@Override
	public int compareTo(Paciente paciente) {
	    if (this.prioridad != paciente.prioridad) {
	        return Integer.compare(this.prioridad, paciente.prioridad);
	    }
	    if (this.tiempoAdmisionEnPrioridad != paciente.tiempoAdmisionEnPrioridad) {
	        return Integer.compare(this.tiempoAdmisionEnPrioridad, paciente.tiempoAdmisionEnPrioridad);
	    }
	    return Integer.compare(this.tiempoAdmision, paciente.tiempoAdmision);
	}


	@Override
	public boolean equals(Object obj) {
	    // Paso 1: Comprobar si el objeto actual (this) es exactamente el mismo que el objeto pasado (obj).
	    // Si ambos apuntan al mismo lugar en memoria, son iguales.
	    if (this == obj)
	        return true;

	    // Paso 2: Comprobar si el objeto pasado es nulo o si no pertenece a la misma clase que el objeto actual.
	    // Si es nulo o las clases son diferentes, los objetos no son iguales.
	    if (obj == null || getClass() != obj.getClass())
	        return false;

	    // Paso 3: Convertir el objeto genérico (obj) al tipo Paciente, ya que sabemos que pertenece a esta clase.
	    Paciente paciente = (Paciente) obj;

	    // Paso 4: Comparar los valores de DNI de ambos objetos (this y paciente).
	    // Si los DNIs son iguales, los objetos se consideran iguales.
	    return DNI.equals(paciente.DNI);
	}


	// Sobrescribimos el método hashCode para que sea coherente con el método equals.
	@Override
	public int hashCode() {
	    // Utilizamos únicamente el atributo DNI para calcular el código hash.
	    // Esto asegura que dos objetos Paciente con el mismo DNI tengan el mismo hashCode.
	    return DNI.hashCode();
	}


}
