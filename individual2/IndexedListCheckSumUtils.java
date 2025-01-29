package aed.individual2;

import es.upm.aedlib.indexedlist.*;

public class IndexedListCheckSumUtils {

	// a no es null, podria tener tamaño 0, n>0
	public static IndexedList<Integer> indexedListCheckSum(IndexedList<Integer> list, int n) {
		// Crear una nueva lista para almacenar el resultado
		IndexedList<Integer> res = new ArrayIndexedList<>();

		int sum = 0; // suma de los elementos
		int count = 0; // Contador de elementos

		// Recorrer la lista original
		for (int i = 0; i < list.size(); i++) {
			res.add(res.size(), list.get(i)); // Añadir el elemento a la nueva lista
			sum += list.get(i); // Sumar el valor al acumulador
			count++;

			// Si hemos procesado n elementos, añadir el checksum
			if (count == n) {
				res.add(res.size(), sum); // Añadir el checksum
				sum = 0; // Reiniciar la suma
				count = 0; // Reiniciar el contador
			}
		}

		// Si quedan elementos no procesados (si list.size() no es múltiplo de n)
		if (count > 0) {
			res.add(res.size(), sum); // Añadir el checksum final
		}

		return res; // Devolver la nueva lista con checksums
	}

	// list no es null, podria tener tamaño 0, n>0
	public static boolean checkIndexedListCheckSum(IndexedList<Integer> list, int n) {
		boolean result = true;
		int i = 0; // Index para atravesar la lista
		int sum = 0; // Sumar elementos para añadir despues de n elementos
		int count = 0; // Countar elementos atravesido hasta n elementos
		if (n == list.size()) {  //si n es igual que n significa que el sum no esta en la lista y devuelva false
			result = false;
		}
		// atravesar los elementos para n bloques
		while (i < list.size()) {
			// Reiniciar sum y count
			sum = 0;
			count = 0;

			// Sumar n elementos hasta n bloques
			while (count < n && i < list.size()) {
				sum += list.get(i);
				i++;
				count++;
			}

			// despues n elementos checkear sum esta en la lista
			if (i < list.size()) {
				int checksum = list.get(i); // obtener el valor de la checksum
				if (checksum != sum) {
					result = false; // si el valor the chechsum no es equal de sum, result es false
				}
				i++; // saltar the checksum element
			}
			// si el checksum no esta en la lista y count es una y estamos en elultimo elementos significa que el utimo 
			//no tiene checksum y result es false
			else if (count == 1 && i == list.size()) {
				result = false; 
			}

		}

		return result;
	}
}