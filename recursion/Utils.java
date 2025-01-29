package aed.recursion;

import es.upm.aedlib.Pair;
import es.upm.aedlib.Position;
import es.upm.aedlib.positionlist.*;

public class Utils {

	// Método para calcular la raíz cuadrada entera de un número 'n'
	public static int sqrt(int n) {
		return sqrtRec(n, 1, n); // Llama a la función recursiva
	}

	// Método recursivo para calcular la raíz cuadrada utilizando búsqueda binaria
	private static int sqrtRec(int n, int low, int high) {
		if (low > high) { // Caso base: si el límite inferior supera al superior
			return high; // Retorna el valor más cercano a la raíz cuadrada
		}
		int mid = (low + high) / 2; // Calcula el punto medio
		if (mid * mid == n) { // Caso en el que 'mid' es exactamente la raíz cuadrada
			return mid;
		} else if (mid * mid < n) { // Si el cuadrado es menor que 'n', busca en la mitad superior
			return sqrtRec(n, mid + 1, high);
		} else { // Si el cuadrado es mayor, busca en la mitad inferior
			return sqrtRec(n, low, mid - 1);
		}
	}

	// Método para generar todos los números primos hasta un valor dado 'n'
	public static Iterable<Integer> primes(int n) {
		boolean[] isPrime = new boolean[n + 1]; // Array para marcar si un número es primo o no
		for (int i = 2; i <= n; i++) {
			isPrime[i] = true; // Initially, assume all numbers >= 2 are prime
		}
		sieveRec(isPrime, 2, n); // Aplica el algoritmo de la criba de Eratóstenes recursivamente
		PositionList<Integer> primes = new NodePositionList<>(); // Lista para almacenar los números primos
		collectPrimes(isPrime, 2, primes); // Colecta los primos en la lista
		return primes;
	}

	// Método recursivo para marcar los múltiplos de los números no primos
	private static void sieveRec(boolean[] isPrime, int current, int n) {
		if (current * current > n) // Caso base: si el cuadrado del número supera 'n', termina
			return;
		if (isPrime[current]) {// Si el número es primo
			markMultiples(isPrime, current, current * 2, n); // Marca sus múltiplos como no primos
		}
		sieveRec(isPrime, current + 1, n); // Llama recursivamente con el siguiente número
	}

// Marca recursivamente los múltiplos de un número base como no primos
	private static void markMultiples(boolean[] isPrime, int base, int multiple, int n) {
		if (multiple > n) // Caso base: si el múltiplo supera 'n', termina
			return;
		isPrime[multiple] = false; // Marca el múltiplo como no primo
		markMultiples(isPrime, base, multiple + base, n); // Llama recursivamente con el siguiente múltiplo
	}

	// Método recursivo para recolectar los números primos en una lista
	private static void collectPrimes(boolean[] isPrime, int current, PositionList<Integer> primes) {
		if (current >= isPrime.length) // Caso base: si se sale del rango del array, termina
			return;
		if (isPrime[current]) // Si el número es primo
			primes.addLast(current); // Lo añade al final de la lista de primos
		collectPrimes(isPrime, current + 1, primes); // Llama recursivamente con el siguiente número
	}
	
    // Método para comparar dos listas de posiciones de pares (Pair) y verificar si son iguales
	public static <E> boolean equals(PositionList<Pair<E, Integer>> p1, PositionList<Pair<E, Integer>> p2) {
		if (p1 == null && p2 == null) // Caso especial: ambas listas nulas
			return true;
		if (p1 == null || p2 == null) // Caso especial: una lista nula y la otra no
			return false;
		if (p1.size() != p2.size())
			return false;  // Si tienen tamaños diferentes, no son iguales

		PositionList<Pair<E, Integer>> copyOfP2 = copyList(p2); // Copia de la lista p2 para comparar sin modificarla
		return equalsRec(p1, p1.first(), copyOfP2); // Compara recursivamente ambas listas
	}

    // Método recursivo para comparar elementos de dos listas
	private static <E> boolean equalsRec(PositionList<Pair<E, Integer>> list1, Position<Pair<E, Integer>> cursor,
			PositionList<Pair<E, Integer>> list2) { // Caso base: si se terminó list1, comprueba si list2 está vacía
		if (cursor == null)
			return list2.isEmpty();

		Pair<E, Integer> element = cursor.element(); // Elemento actual en list1
		Position<Pair<E, Integer>> posInList2 = findAndRemove(list2, list2.first(), element); // Busca y elimina el elemento en list2


		if (posInList2 == null) // Si no encuentra el elemento en list2, las listas no son iguales
			return false;

		Position<Pair<E, Integer>> nextCursor = list1.next(cursor); // Pasa al siguiente elemento en list1
		return equalsRec(list1, nextCursor, list2); // Llama recursivamente para seguir comparando
	}

	   // Busca un elemento en la lista y lo elimina si lo encuentra
	private static <E> Position<Pair<E, Integer>> findAndRemove(PositionList<Pair<E, Integer>> list,
			Position<Pair<E, Integer>> cursor, Pair<E, Integer> element) {
		if (cursor == null)
			return null; // Caso base: fin de la lista sin encontrar el elemento

		Pair<E, Integer> current = cursor.element(); // Elemento actual en la lista
		if (current.getLeft().equals(element.getLeft()) &&
				current.getRight().equals(element.getRight())) {  // Si coincide el par, elimina el elemento
			list.remove(cursor);  // Elimina el elemento encontrado
			return cursor; // Retorna la posición del elemento encontrado
		}

		Position<Pair<E, Integer>> nextCursor = list.next(cursor); // Pasa al siguiente elemento
		return findAndRemove(list, nextCursor, element);  // Llama recursivamente para buscar el siguiente
	}

		 // Crea una copia de la lista dada
	private static <E> PositionList<Pair<E, Integer>> copyList(PositionList<Pair<E, Integer>> list) {
		PositionList<Pair<E, Integer>> copy = new NodePositionList<>(); // Nueva lista para la copia
		copyRecursively(list, list.first(), copy);  // Copia recursivamente cada elemento
		return copy; // Retorna la lista copiada
	}

	 // Método recursivo para copiar elementos de una lista a otra
	private static <E> void copyRecursively(PositionList<Pair<E, Integer>> list, Position<Pair<E, Integer>> cursor,
			PositionList<Pair<E, Integer>> copy) {
		if (cursor == null)
			return; // Caso base: si cursor es nulo, ha terminado la copia
		copy.addLast(cursor.element());  // Añade el elemento actual a la lista copia
		copyRecursively(list, list.next(cursor), copy); // Llama recursivamente con el siguiente elemento
	}
}