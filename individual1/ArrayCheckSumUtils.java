package aed.individual1;

public class ArrayCheckSumUtils {

	// a no es null, podria tener tamaño 0, n>0
	public static int[] arrayCheckSum(int[] arr, int n) {
		 // Calcular el tamaño del nuevo array
		int newLength = arr.length;
		int add=0;
		if (arr.length%n == 0) {
			add += arr.length / n;
		} else {
			add += arr.length/n + 1;
		}
		newLength+=add;

        // Crear el nuevo array que tiene checksums
        int[] nArr = new int[newLength];
        int sum = 0;
        int count = 0; // Contador de elementos en el bloque actual de n

        for (int i = 0, j = 0; i < arr.length; i++, j++) {
            nArr[j] = arr[i];  // Copiar el elemento actual al nuevo array
            sum += arr[i];       // Sumar el valor actual para el checksum
            count++;

            // Cada vez que se han procesado n elementos, agregar un checksum
            if (count == n) {
                nArr[++j] = sum; // Agregar el checksum al nuevo array
                sum = 0;           // Reiniciar la suma
                count = 0;         // Reiniciar el contador
            }
        }

        // Si hay elementos sobrantes (cuando arr.length no es múltiplo de n), agregar un último checksum
        if (count > 0) {
            nArr[newLength - 1] = sum;
        }
		return nArr;
	}
}
