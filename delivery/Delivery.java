package aed.delivery;

import es.upm.aedlib.positionlist.PositionList;
import es.upm.aedlib.positionlist.NodePositionList;
import es.upm.aedlib.graph.DirectedGraph;
import es.upm.aedlib.graph.DirectedAdjacencyListGraph;
import es.upm.aedlib.graph.Vertex;
import es.upm.aedlib.graph.Edge;
import java.util.HashSet;
import java.util.Iterator;

public class Delivery<V> {

	private DirectedGraph<V, Integer> graph;

	// Constructor: Construye un grafo dirigido a partir de lugares y una matriz de
	// adyacencia
	@SuppressWarnings("unchecked")
	public Delivery(V[] places, Integer[][] gmat) {
		graph = new DirectedAdjacencyListGraph<>();
		Vertex<V>[] vertices = new Vertex[places.length];

		// Agregar los vértices al grafo
		for (int i = 0; i < places.length; i++) {
			vertices[i] = graph.insertVertex(places[i]);
		}

		// Agregar las aristas basadas en la matriz de adyacencia
		for (int i = 0; i < gmat.length; i++) {
			for (int j = 0; j < gmat[i].length; j++) {
				if (gmat[i][j] != null) { // Si la celda de la matriz no es nula, se crea una arista
					graph.insertDirectedEdge(vertices[i], vertices[j], gmat[i][j]);
				}
			}
		}
	}

	// Retorna el grafo construido
	public DirectedGraph<V, Integer> getGraph() {
		return graph;
	}

	// Retorna un camino hamiltoniano si existe
	public PositionList<Vertex<V>> tour() {
		PositionList<Vertex<V>> path = new NodePositionList<>();
		HashSet<Vertex<V>> visited = new HashSet<>();

		// Intenta encontrar un camino hamiltoniano comenzando desde cada vértice
		for (Vertex<V> start : graph.vertices()) {
			if (findHamiltonianPath(start, visited, path)) {
				return path; // Si encuentra un camino hamiltoniano, lo retorna
			}
			path = new NodePositionList<>(); // Limpia el camino para el siguiente intento
			visited.clear(); // Limpia el conjunto de visitados para el siguiente intento
		}

		// Si no encuentra un camino hamiltoniano, retorna null
		return null;
	}

	// Método auxiliar recursivo para encontrar un camino hamiltoniano
	private boolean findHamiltonianPath(Vertex<V> current, HashSet<Vertex<V>> visited, PositionList<Vertex<V>> path) {
		path.addLast(current); // Agrega el vértice actual al camino
		visited.add(current); // Marca el vértice como visitado

		// Si todos los vértices han sido visitados, retorna true
		if (visited.size() == graph.numVertices()) {
			return true;
		}

		// Explora todas las aristas salientes del vértice actual
		for (Edge<Integer> edge : graph.outgoingEdges(current)) {
			Vertex<V> next = graph.endVertex(edge); // Obtiene el vértice destino de la arista
			if (!visited.contains(next)) { // Si el vértice no ha sido visitado
				if (findHamiltonianPath(next, visited, path)) { // Realiza una llamada recursiva
					return true; // Si encuentra un camino hamiltoniano, retorna true
				}
			}
		}

		// Retrocede si no encuentra un camino desde este vértice
		visited.remove(current); // Desmarca el vértice como visitado
		path.remove(path.last()); // Elimina el vértice del camino
		return false;
	}

	// Calcula la longitud total de un camino dado
	public int length(PositionList<Vertex<V>> path) {
		if (path == null || path.size() < 2) { // Si el camino es inválido o tiene menos de 2 vértices
			return 0;
		}

		int totalLength = 0;
		Iterator<Vertex<V>> it = path.iterator();
		Vertex<V> prev = it.next(); // Obtiene el primer vértice del camino

		while (it.hasNext()) {
			Vertex<V> current = it.next(); // Obtiene el siguiente vértice
			boolean edgeFound = false;

			// Busca una arista entre el vértice previo y el actual
			for (Edge<Integer> edge : graph.outgoingEdges(prev)) {
				if (graph.endVertex(edge).equals(current)) { // Si encuentra una arista válida
					totalLength += edge.element(); // Suma el peso de la arista al total
					edgeFound = true;
					break;
				}
			}

			// Lanza una excepción si no encuentra una arista entre dos vértices
			// consecutivos
			if (!edgeFound) {
				throw new IllegalArgumentException("No se encontró una arista entre " + prev + " y " + current);
			}

			prev = current; // Avanza al siguiente vértice
		}

		return totalLength; // Retorna la longitud total del camino
	}

	@Override
	public String toString() {
		// Devuelve una representación del grafo indicando el número de vértices y
		// aristas
		return "Grafo de entrega con " + graph.numVertices() + " vértices y " + graph.numEdges() + " aristas.";
	}
}
