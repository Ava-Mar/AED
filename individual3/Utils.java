package aed.individual3;

import java.util.Iterator;

public class Utils {
	public static boolean isArithmeticSequence(Iterable<Integer> l) {

		// Crea un iterador para recorrer la lista
		Iterator<Integer> iterator = l.iterator();

		// Variable para almacenar el primer número no nulo
		Integer prev = null;
		// Variable para almacenar la diferencia entre números consecutivos
		Integer diff = null;

		// Recorre la lista utilizando el iterador
		while (iterator.hasNext()) {
			Integer current = iterator.next();

			// Ignora los elementos null
			if (current == null) {
				continue;
			}

			// Si es el primer número no nulo, inicializa la variable previous
			if (prev == null) {
				prev = current;
			} else {
				// Calcula la diferencia si no está definida
				if (diff == null) {
					diff = current - prev;
				} else {
					// Verifica si la diferencia actual coincide con la esperada
					if (current - prev != diff) {
						return false;
					}
				}
				// Actualiza el valor de previous
				prev = current;
			}
		}

		// Si llega al final sin romper la secuencia, devuelve true
		return true;
	}
}
