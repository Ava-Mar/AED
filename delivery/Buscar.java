package aed.delivery;

import es.upm.aedlib.Pair;
import es.upm.aedlib.positionlist.*;

/**
 * Clase Buscar para navegar el laberinto.
 */
public class Buscar {

    /**
     * Encuentra un camino hacia un punto con un regalo en el laberinto.
     *
     * @param laberinto El laberinto donde se buscará el regalo.
     * @return Un par (Pair) que contiene el regalo y el camino hacia él.
     */
    public static Pair<String, PositionList<Direccion>> busca(Laberinto laberinto) {
        // Inicializa la lista para almacenar el camino
        PositionList<Direccion> path = new NodePositionList<>();

        // Inicia la búsqueda recursiva
        if (dfs(laberinto, path)) {
            return new Pair<>(laberinto.getRegalo(), path);
        }

        // Si no se encuentra un camino, retorna null
        return null;
    }

    /**
     * Método auxiliar recursivo para la búsqueda en profundidad (DFS).
     *
     * @param laberinto El laberinto en el que se busca.
     * @param path El camino actual hasta el punto explorado.
     * @return True si se encuentra un regalo; false en caso contrario.
     */
    private static boolean dfs(Laberinto laberinto, PositionList<Direccion> path) {
        // Caso base: Si el punto actual tiene un regalo, retorna true
        if (laberinto.tieneRegalo()) {
            return true;
        }

        // Marca el punto actual para evitar visitarlo nuevamente
        laberinto.marcaSueloConTiza();

        // Explora todas las direcciones posibles desde el punto actual
        for (Direccion dir : laberinto.direccionesPosibles()) {
            // Comprueba si la dirección conduce a un punto no visitado
            if (!laberinto.canGo(dir).sueloMarcadoConTiza) {
                // Mueve hacia la dirección
                laberinto.moverHacia(dir);
                path.addLast(dir);

                // Realiza una llamada recursiva desde la nueva posición
                if (dfs(laberinto, path)) {
                    return true; // Si se encuentra un regalo, finaliza la búsqueda
                }

                // Retrocede si no se encuentra un regalo en esta dirección
                path.remove(path.last()); // Elimina la última dirección del camino
                laberinto.moverHacia(oppositeDirection(dir)); // Vuelve al punto anterior
            }
        }

        return false; // No se encontró un regalo desde este punto
    }

    /**
     * Obtiene la dirección opuesta para retroceder.
     *
     * @param dir La dirección actual.
     * @return La dirección opuesta.
     */
    private static Direccion oppositeDirection(Direccion dir) {
        switch (dir) {
            case NORTE: return Direccion.SUR;  // Norte -> Sur
            case SUR: return Direccion.NORTE;  // Sur -> Norte
            case ESTE: return Direccion.OESTE;  // Este -> Oeste
            case OESTE: return Direccion.ESTE;  // Oeste -> Este
            default: throw new IllegalArgumentException("Dirección inválida: " + dir);
        }
    }
}
