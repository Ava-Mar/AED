package aed.multisets;

import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.positionlist.NodePositionList;

/**
 * Una implementacion de un multiset (multiconjunto) a traves de una lista de
 * posiciones.
 */
public class MultiSetList<E> implements MultiSet<E> {

	/**
	 * La estructura de datos que guarda los elementos del multiset.
	 */
	private PositionList<Pair<E, Integer>> elements;

	/**
	 * El tamaño del multiset.
	 */
	private int size;

	/**
	 * Construye un multiset vacio.
	 */
	public MultiSetList() {
		this.elements = new NodePositionList<Pair<E, Integer>>();
		this.size = 0;
	}

	/**
	 * Añade `n` copias de `elem` al multiset.
	 * 
	 * @param elem El elemento a añadir.
	 * @param n    El número de copias a añadir.
	 * @throws IllegalArgumentException si `n` es menor que 0.
	 */
	public void add(E elem, int n) {
		if (n < 0) // Lanza una excepción si `n` es negativo
			throw new IllegalArgumentException("n debe ser mayor o igual a 0.");

		if (n == 0)
			return; // No hace nada si `n` es 0

		// Encuentra la posición del elemento en la lista
		Position<Pair<E, Integer>> pos = findPosition(elem);

		if (pos != null) { // Si el elemento existe en el multiset, actualiza su cantidad
			Pair<E, Integer> pair = pos.element();
			elements.set(pos, new Pair<>(elem, pair.getRight() + n));
		} else { // Si el elemento no existe, añade un nuevo par
			elements.addLast(new Pair<>(elem, n));
		}
		size += n; // Actualiza el tamaño total del multiset

	}

	/**
	 * Elimina `n` copias de `elem` del multiset.
	 * 
	 * @param elem El elemento a eliminar.
	 * @param n    El número de copias a eliminar.
	 * @return El número de copias eliminadas.
	 * @throws IllegalArgumentException si `n` es menor que 0.
	 */
	@Override
	public int remove(E elem, int n) {
		if (n < 0) {// Lanza una excepción si `n` es negativo
			throw new IllegalArgumentException("n debe ser mayor o igual a 0.");
		}

		// Encuentra la posición del elemento en la lista
		Position<Pair<E, Integer>> pos = findPosition(elem);

		// Si el elemento no se encuentra, devuelve 0 (no se elimina nada)
		if (pos == null) {
			return 0;
		}

		// Obtener la multiplicidad actual del elemento
		Pair<E, Integer> pair = pos.element();
		int multiplicidadActual = pair.getRight();

		// si n == 0, no se elimina nada
		if (n == 0) {
			return 0;
		}

		// Determinar cuántas copias eliminar
		if (n > multiplicidadActual) {
			return 0; // si n es mayor de multiplicidad
		}

		// Si se eliminan todas las copias, elimine el par de la lista
		if (n == multiplicidadActual) {
			elements.remove(pos);
		} else {
			// De lo contrario, disminuya la multiplicidad.
			elements.set(pos, new Pair<>(elem, multiplicidadActual - n));
		}

		// Actualizar el tamaño total del multiconjunto
		size -= n;

		// Devuelve el número de copias eliminadas
		return n;
	}

	/**
	 * Devuelve la multiplicidad (número de copias) de `elem` en el multiset.
	 * 
	 * @param elem El elemento a consultar.
	 * @return La multiplicidad del elemento, o 0 si no existe.
	 */
	@Override
	public int multiplicity(E elem) {
		// Encuentra la posición del elemento en la lista

		Position<Pair<E, Integer>> pos = findPosition(elem);
		if (pos != null) {
			return pos.element().getRight();// Devuelve la multiplicidad si se encuentra
		}
		return 0; // Devuelve 0 si el elemento no está en el multiset
	}

	/**
	 * Devuelve el número total de elementos en el multiset, considerando sus
	 * multiplicidades.
	 * 
	 * @return El tamaño del multiset.
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Verifica si el multiset está vacío.
	 * 
	 * @return True si el multiset está vacío, false de lo contrario.
	 */
	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Devuelve una lista de todos los elementos únicos en el multiset.
	 * 
	 * @return Una PositionList de todos los elementos únicos.
	 */
	@Override
	public PositionList<E> elements() {
		PositionList<E> elementosUnicos = new NodePositionList<>();
		// Itera sobre todos los pares y recolecta los elementos únicos
		for (Pair<E, Integer> pair : elements) {
			elementosUnicos.addLast(pair.getLeft());
		}
		return elementosUnicos;
	}

	/**
	 * Devuelve un nuevo multiset que es la suma de este multiset y otro.
	 * 
	 * @param s El otro multiset a sumar.
	 * @return Un nuevo multiset que contiene la suma de ambos multisets.
	 */
	@Override
	public MultiSet<E> sum(MultiSet<E> s) {
		MultiSet<E> result = new MultiSetList<>();
		// Añade todos los elementos del multiset actual
		for (Pair<E, Integer> pair : elements) {
			result.add(pair.getLeft(), pair.getRight());
		}
		// Añade todos los elementos del otro multiset
		PositionList<E> otherElements = s.elements();
		for (E elem : otherElements) {
			result.add(elem, s.multiplicity(elem));
		}
		return result; // Devuelve el multiset resultante
	}

	/**
	 * Devuelve un nuevo multiset que es la diferencia entre este multiset y otro.
	 * 
	 * @param s El otro multiset a restar.
	 * @return Un nuevo multiset que contiene la diferencia.
	 */
	@Override
	public MultiSet<E> minus(MultiSet<E> s) {
		MultiSet<E> result = new MultiSetList<>();
		// Itera sobre todos los elementos en el multiset actual
		for (Pair<E, Integer> pair : elements) {
			
			// Calcula la diferencia en multiplicidades
			int otherMultiplicity = s.multiplicity(pair.getLeft());
			int diffMultiplicity = Math.max(pair.getRight() - otherMultiplicity, 0);
			
			// Si la diferencia es mayor que 0, añade el resultado
			if (diffMultiplicity > 0) {
				result.add(pair.getLeft(), diffMultiplicity);
			}
		}
		return result; // Devuelve el multiset resultante
	}

	/**
	 * Devuelve un nuevo multiset que es la intersección de este multiset y otro.
	 * 
	 * @param s El otro multiset a intersectar.
	 * @return Un nuevo multiset que contiene la intersección.
	 */
	@Override
	public MultiSet<E> intersection(MultiSet<E> s) {
		MultiSet<E> result = new MultiSetList<>();
		// Itera sobre todos los elementos en el multiset actual
		for (Pair<E, Integer> pair : elements) {
			// Encuentra la multiplicidad mínima entre ambos multisets
			int otherMultiplicity = s.multiplicity(pair.getLeft());
			int minMultiplicity = Math.min(pair.getRight(), otherMultiplicity);
			// Si la multiplicidad mínima es mayor que 0, añádela al resultado
			if (minMultiplicity > 0) {
				result.add(pair.getLeft(), minMultiplicity);
			}
		}
		return result; // Devuelve el multiset resultante
	}

	/**
	 * Verifica si el multiset actual es un subconjunto (o igual) de otro multiset.
	 * 
	 * @param s El otro multiset a comparar.
	 * @return True si el multiset actual es un subconjunto o igual, false de lo
	 *         contrario.
	 */
	@Override
	public boolean subsetEqual(MultiSet<E> s) {
		// Itera sobre todos los elementos en el multiset actual
		for (Pair<E, Integer> pair : elements) {
			// Verifica si la multiplicidad de cada elemento es menor o igual en `s`
			if (s.multiplicity(pair.getLeft()) < pair.getRight()) {
				return false; // Si algún elemento tiene una mayor multiplicidad, devuelve false
			}
		}
		return true; // Devuelve true si todas las multiplicidades son satisfechas
	}

	/**
	 * Encuentra la posición de `elem` en la lista.
	 * 
	 * @param elem El elemento a encontrar.
	 * @return La posición del elemento, o null si no se encuentra.
	 */
	private Position<Pair<E, Integer>> findPosition(E elem) {
		Position<Pair<E, Integer>> cursor = elements.first(); // Empieza en la primera posición
		// Recorre la lista para encontrar el elemento
		while (cursor != null) {
			if (cursor.element().getLeft().equals(elem)) {
				return cursor; // Elemento encontrado
			}
			cursor = elements.next(cursor); // Avanza a la siguiente posición
		}
		return null; // Devuelve null si el elemento no se encuentra<
	}

}
