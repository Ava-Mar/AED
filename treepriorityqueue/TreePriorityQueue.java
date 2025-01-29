package aed.treepriorityqueue;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import es.upm.aedlib.Position;
import es.upm.aedlib.Entry;
import es.upm.aedlib.EntryImpl;
import es.upm.aedlib.tree.*;

public class TreePriorityQueue<K extends Comparable<K>, V> implements PriorityQueue<K, V> {

	// Private fields for the binary tree and last position
	private BinaryTree<Entry<K, V>> t; // Binary tree structure to store the entries
	private Position<Entry<K, V>> lastPos; // Tracks the last inserted position in the tree

	// Constructor: Initializes the binary tree and sets the last position to null
	public TreePriorityQueue() {
		t = new LinkedBinaryTree<>(); // Create an empty binary tree
		lastPos = null; // No nodes initially, so lastPos is set to null
	}

	// Returns the number of elements in the priority queue
	@Override
	public int size() {
		return t.size(); // Use the tree's size method to get the number of elements
	}

	// Checks if the priority queue is empty
	@Override
	public boolean isEmpty() {
		return t.isEmpty(); // The queue is empty if the tree has no nodes
	}

	// Retrieves the element with the smallest key (the root of the heap)
	// Throws an exception if the priority queue is empty
	@Override
	public Entry<K, V> first() throws EmptyPriorityQueueException {
		if (isEmpty()) {
			// If there are no elements, throw an exception to indicate an error
			throw new EmptyPriorityQueueException();
		}
		// Return the root element, which is the minimum in a min-heap
		return t.root().element();
	}

	/**
	 * Inserta un nuevo elemento en la cola de prioridad utilizando el montículo
	 * binario. La operación mantiene la propiedad de orden del montículo
	 * (heap-order property).
	 *
	 * @param k La clave del nuevo elemento.
	 * @param v El valor asociado al nuevo elemento.
	 */
	@Override
	public void enqueue(K k, V v) {
		Entry<K, V> newEntry = new EntryImpl<>(k, v);

		if (isEmpty()) {
			// Initialize root and lastPos for the first entry
			lastPos = t.addRoot(newEntry);
		} else {
			// Get parent for insertion
			Position<Entry<K, V>> parent = findInsertionParent();

			if (parent == null) {
				throw new IllegalStateException("Parent position for insertion is null.");
			}

			// Insert in the correct position (left or right child)
			if (!t.hasLeft(parent)) {
				lastPos = t.insertLeft(parent, newEntry);
			} else if (!t.hasRight(parent)) {
				lastPos = t.insertRight(parent, newEntry);
			}

			// Restore heap-order property
			upHeap(lastPos);
		}
	}

	/**
	 * Encuentra el padre adecuado para insertar un nuevo nodo en el árbol binario
	 * casi completo. Este método garantiza que el árbol mantenga la propiedad de
	 * llenado de izquierda a derecha.
	 *
	 * @return El nodo que será el padre del nuevo nodo a insertar.
	 */
	private Position<Entry<K, V>> findInsertionParent() {
		if (lastPos == null || t.isEmpty()) {
			return null; // If the tree is empty, there is no valid parent
		}

		Position<Entry<K, V>> current = lastPos;

		// Traverse up until we find a node whose parent's right child is not filled
		while (!t.isRoot(current) && current == t.right(t.parent(current))) {
			current = t.parent(current);
		}

		if (!t.isRoot(current)) {
			current = t.parent(current); // Move to the parent node
			if (!t.hasRight(current)) {
				return current; // The parent's right child is available
			}
			current = t.right(current); // Otherwise, move to the parent's right child
		}

		// Traverse down to find the leftmost open position
		while (t.hasLeft(current)) {
			current = t.left(current);
		}

		return current;
	}

	/**
	 * Elimina y devuelve el elemento con la clave mínima (raíz del montículo) de la
	 * cola de prioridad. Este método asegura que la propiedad del montículo se
	 * restaure después de eliminar el elemento raíz.
	 * 
	 * @return El elemento con la clave mínima (raíz del montículo).
	 * @throws EmptyPriorityQueueException Si la cola de prioridad está vacía.
	 */
	@Override
	public Entry<K, V> dequeue() throws EmptyPriorityQueueException {
		// Verificamos si la cola de prioridad está vacía
		if (isEmpty()) {
			// Si está vacía, lanzamos una excepción
			throw new EmptyPriorityQueueException();
		}

		// Recuperamos la raíz del árbol (que contiene el elemento con la clave mínima)
		Position<Entry<K, V>> root = t.root();
		// Almacenamos el elemento mínimo (de la raíz) que vamos a devolver
		Entry<K, V> min = root.element();

		// Caso cuando el árbol tiene solo un elemento
		if (size() == 1) {
			// Si el árbol tiene solo un elemento, eliminamos la raíz y restablecemos
			// lastPos a null
			t.remove(root);
			lastPos = null;
		} else {
			// Caso cuando el árbol tiene más de un elemento

			// Sustituimos la raíz con el último elemento del árbol (el elemento en lastPos)
			((LinkedBinaryTree<Entry<K, V>>) t).set(root, lastPos.element());

			// Almacenamos la posición del último elemento antes de eliminarlo
			Position<Entry<K, V>> oldLastPos = lastPos;
			// Actualizamos lastPos para apuntar al nuevo último nodo en el árbol
			lastPos = findNewLastPosition();

			// Eliminamos el último nodo (anteriormente lastPos)
			t.remove(oldLastPos);

			// Restauramos la propiedad del montículo realizando un "down-heap" desde la
			// raíz
			downHeap(root);
		}

		// Devolvemos el elemento mínimo que estaba en la raíz antes de la eliminación
		return min;
	}

	@Override
	public String toString() {
		return t.toString();
	}

	@Override
	public Iterator<Entry<K, V>> iterator() {
		LinkedList<Entry<K, V>> entries = new LinkedList<>();
		if (!t.isEmpty()) {
			populateEntries(t.root(), entries);
		}
		return entries.iterator();
	}

	// Recursive helper method for traversal
	private void populateEntries(Position<Entry<K, V>> pos, LinkedList<Entry<K, V>> entries) {
		if (pos == null)
			return;

		// Add the current position's element to the list
		entries.add(pos.element());

		// Recursively traverse the left and right subtrees
		if (t.hasLeft(pos)) {
			populateEntries(t.left(pos), entries);
		}
		if (t.hasRight(pos)) {
			populateEntries(t.right(pos), entries);
		}
	}

	/**
	 * Restaura la propiedad del montículo subiendo el nodo hasta que se cumpla la
	 * heap-order property. Este método se utiliza después de insertar un nuevo nodo
	 * en el montículo para asegurar que los valores de las claves en los nodos
	 * padres sean menores o iguales que los de sus hijos.
	 *
	 * @param node El nodo que debe ser desplazado hacia arriba (si es necesario).
	 */
	private void upHeap(Position<Entry<K, V>> node) {
		// Mientras el nodo no sea la raíz, continuamos comprobando si es necesario
		// moverlo hacia arriba
		while (!t.isRoot(node)) {
			// Obtenemos el nodo padre del nodo actual
			Position<Entry<K, V>> parent = t.parent(node);

			// Comparamos las claves del nodo actual y su padre
			// Si la clave del nodo es menor que la clave del padre, se realiza un
			// intercambio
			if (node.element().getKey().compareTo(parent.element().getKey()) < 0) {
				swap(node, parent); // Intercambiamos el nodo con su padre
				node = parent; // Ahora el nodo se mueve a la posición del padre para seguir comprobando
			} else {
				// Si no es necesario mover el nodo (la propiedad del montículo se cumple),
				// terminamos
				break;
			}
		}
	}

	private void downHeap(Position<Entry<K, V>> node) {
		// Mientras el nodo actual tenga un hijo izquierdo, seguimos en el bucle.
		// (En un montículo completo, si un nodo tiene hijos, siempre tendrá al menos un
		// hijo izquierdo).
		while (t.hasLeft(node)) {
			// Inicialmente, asumimos que el hijo izquierdo es el hijo más pequeño.
			Position<Entry<K, V>> smallerChild = t.left(node);

			// Si el nodo también tiene un hijo derecho, comparamos las claves de ambos
			// hijos
			// y actualizamos smallerChild para que apunte al hijo con la clave más pequeña.
			if (t.hasRight(node) && t.right(node).element().getKey().compareTo(smallerChild.element().getKey()) < 0) {
				smallerChild = t.right(node); // Ahora smallerChild apunta al hijo derecho.
			}

			// Comparamos la clave del hijo más pequeño (smallerChild) con la clave del nodo
			// actual.
			// Si la clave del hijo más pequeño es menor que la del nodo actual,
			// intercambiamos sus valores para mantener la propiedad del montículo.
			if (smallerChild.element().getKey().compareTo(node.element().getKey()) < 0) {
				swap(node, smallerChild); // Intercambiamos el nodo actual con el hijo más pequeño.
				node = smallerChild; // Actualizamos node para continuar bajando en el árbol.
			} else {
				// Si la clave del hijo más pequeño no es menor que la clave del nodo actual,
				// significa que la propiedad del montículo ya está satisfecha, así que
				// terminamos el bucle.
				break;
			}
		}
	}

	private Position<Entry<K, V>> findNewLastPosition() {
		// Start with the current last position in the tree
		Position<Entry<K, V>> current = lastPos;

		// Step 1: Traverse up the tree until we find a node that is a right child
		// OR reach the root. This helps us locate the correct subtree for the new last
		// position.
		while (!t.isRoot(current) && current == t.left(t.parent(current))) {
			current = t.parent(current);
		}

		// Step 2: If we didn't reach the root, move to the left sibling of the parent.
		// This sets us up to traverse down and find the new last position.
		if (!t.isRoot(current)) {
			current = t.left(t.parent(current));
		}

		// Step 3: Traverse down to find the rightmost leaf node, ensuring completeness.
		// This traversal guarantees we move to the deepest and rightmost node.
		while (t.hasRight(current)) {
			current = t.right(current);
		}

		// Return the new last position, which is now the deepest and rightmost leaf.
		return current;
	}

	private void swap(Position<Entry<K, V>> pos1, Position<Entry<K, V>> pos2) {
		// Temporarily store the element at pos1
		Entry<K, V> temp = pos1.element();

		// Set the element of pos2 to pos1
		((LinkedBinaryTree<Entry<K, V>>) t).set(pos1, pos2.element());

		// Set the temporarily stored element of pos1 to pos2
		((LinkedBinaryTree<Entry<K, V>>) t).set(pos2, temp);
	}

}
