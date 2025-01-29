package aed.individual4;

import java.util.Iterator;
import java.util.NoSuchElementException;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.PositionList;

public class OrderedIterator implements Iterator<Integer> {

	private PositionList<Integer> list; // La lista sobre la que iterar
	private Position<Integer> cursor; // Posición actual en la lista
	private Integer last; // Último valor devuelto por next()

	public OrderedIterator(PositionList<Integer> list) {
		this.list = list;// Se asigna la lista proporcionada
		// Si la lista está vacía, cursor se inicializa en null; si no, en la primera
		// posición de la lista.
		this.cursor = (list.isEmpty()) ? null : list.first();
		this.last = null; // last comienza como null, ya que aún no se ha devuelto ningún valor
	}

	/**
	 * Constructor que inicializa el iterador con una lista de posiciones.
	 * 
	 * @param list La lista sobre la que se iterará
	 */
	@Override
	public boolean hasNext() {
		// Si la lista está vacía o ya llegamos al final, no hay más elementos
		if (cursor == null) {
			return false;
		}

		// Buscar si existe algún siguiente válido en orden creciente
		// Se crea una variable temporal para buscar el siguiente elemento válido sin mover el cursor original.
		Position<Integer> temp = cursor;
        // Recorre la lista hasta encontrar un elemento mayor o igual al último devuelto (last).
		while (temp != null) {
            // Si last es null (aún no se ha devuelto ningún valor) o si encontramos un valor mayor o igual a last
			if (last == null || temp.element() >= last) {
				return true;
			}
			   // Avanza al siguiente elemento en la lista.
			temp = list.next(temp);
		}
		  // Si no se encuentra un siguiente válido, retorna false.
		return false;
	}
	/**
	 * Método que devuelve el siguiente elemento en la lista que es mayor o igual al último devuelto.
	 * @return El siguiente elemento en la lista.
	 * @throws NoSuchElementException si no hay más elementos que devolver.
	 */
	@Override
	public Integer next() {
		// Si no hay más elementos que devolver, lanzar una excepción
		if (!hasNext()) {
			throw new java.util.NoSuchElementException();
		}

		// Encontrar el siguiente elemento mayor o igual al último devuelto
		while (cursor != null) {
            Integer currentElement = cursor.element();  // Obtiene el elemento actual del cursor.
            
            // Si last es null (primera iteración) o el elemento actual es mayor o igual a last
			if (last == null || currentElement >= last) {
				last = currentElement; // Actualizar el último elemento actual
				
                // Mueve el cursor al siguiente elemento, si existe, de lo contrario lo pone en null.
				cursor = (list.next(cursor)) != null ? list.next(cursor) : null;
				return last; // Devuelve el elemento encontrado
			}
			
            // Si el elemento actual no cumple la condición, avanza al siguiente en la lista.
			cursor = list.next(cursor);
		}

        return null;  // Este retorno nunca se alcanzará porque hasNext controla que haya elementos.
	}

}
