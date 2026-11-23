package org.quoridor.model;

import java.util.*;

/**
 * Implementación de IValidadorCaminos basada en búsqueda en anchura (BFS).
 * Recorre las casillas transitables del tablero evitando los muros colocados para garantizar
 * que ningún jugador quede encerrado sin camino hacia su meta y calcular rutas mínimas.
 */
public class ValidadorCaminos implements IValidadorCaminos {

    /**
     * Construye una instancia del validador de caminos BFS.
     */
    public ValidadorCaminos() {
    }

    /**
     * Determina mediante búsqueda en anchura (BFS) si existe al menos un camino desde inicio
     * hasta cualquier casilla de filaObjetivo sin atravesar muros.
     *
     * @param inicio Posición inicial del jugador dentro del tablero.
     * @param filaObjetivo Índice de fila objetivo en [0, tamanoTablero - 1].
     * @param muros Lista de muros colocados actualmente.
     * @param tamanoTablero Dimensión lateral del tablero.
     * @return true si la fila objetivo es alcanzable; false si todos los accesos están bloqueados.
     */
    @Override
    public boolean hayCaminoDisponible(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero) {
        // Precondiciones
        assert inicio != null : "La posicion inicial es null";
        assert filaObjetivo >= 0 && filaObjetivo < tamanoTablero : "filaObjetivo fuera del rango del tablero";
        assert muros != null : "La lista de muros es null";
        assert tamanoTablero > 0 : "El tamaño del tablero es 0 o negativo";
        assert inicio.esValida(tamanoTablero) : "La posicion inicial no es valida";

        // Caso base: si ya estamos en la filaObjetivo
        if (inicio.getY() == filaObjetivo) {
            return true;
        }

        // Estructuras del recorrido BFS
        Set<Posicion> visitados = new HashSet<>();
        Queue<Posicion> cola = new LinkedList<>();

        cola.add(inicio);
        visitados.add(inicio);

        while (!cola.isEmpty()) {
            Posicion actual = cola.poll();

            // ¿Llegamos a la fila objetivo?
            if (actual.getY() == filaObjetivo) {
                return true;
            }

            // Explorar los 4 vecinos posibles (Arriba, Abajo, Izquierda, Derecha)
            for (Posicion vecino : obtenerVecinosValidos(actual, muros, tamanoTablero)) {
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);
                    cola.add(vecino);
                }
            }
        }

        // Si no existe camino tras agotar todas las casillas alcanzables
        return false;
    }

    /**
     * Calcula mediante búsqueda en anchura (BFS) la distancia mínima en pasos ortogonales
     * desde inicio hasta alcanzar filaObjetivo.
     *
     * @param inicio Posición de origen del recorrido dentro del tablero.
     * @param filaObjetivo Índice de fila meta en [0, tamanoTablero - 1].
     * @param muros Lista de muros colocados en el tablero.
     * @param tamanoTablero Dimensión lateral del tablero.
     * @return Número mínimo de pasos necesarios para alcanzar la fila objetivo, o -1 si no hay camino.
     */
    @Override
    public int calcularDistanciaMinima(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero) {
        // Precondiciones
        assert inicio != null : "La posicion inicial es null";
        assert filaObjetivo >= 0 && filaObjetivo < tamanoTablero : "filaObjetivo fuera del rango del tablero";
        assert muros != null : "La lista de muros es null";
        assert tamanoTablero > 0 : "El tamaño del tablero es 0 o negativo";
        assert inicio.esValida(tamanoTablero) : "La posicion inicial no es valida";

        // Caso base: si ya estamos en la filaObjetivo, la distancia es 0
        if (inicio.getY() == filaObjetivo) {
            return 0;
        }

        // Mapa de distancias mínimas acumuladas desde inicio y cola FIFO de exploración BFS
        Map<Posicion, Integer> distancias = new HashMap<>();
        Queue<Posicion> cola = new LinkedList<>();

        cola.add(inicio);
        distancias.put(inicio, 0);

        while (!cola.isEmpty()) {
            Posicion actual = cola.poll();
            int distanciaActual = distancias.get(actual);

            // Al ser BFS sobre grafo no ponderado, el primer alcance a filaObjetivo garantiza distancia mínima
            if (actual.getY() == filaObjetivo) {
                // Postcondición
                assert distanciaActual >= 0 : "La distancia no puede ser menor que 0";
                return distanciaActual;
            }

            // Explorar los 4 vecinos posibles (Arriba, Abajo, Izquierda, Derecha)
            for (Posicion vecino : obtenerVecinosValidos(actual, muros, tamanoTablero)) {
                if (!distancias.containsKey(vecino)) {
                    distancias.put(vecino, distanciaActual + 1);
                    cola.add(vecino);
                }
            }
        }

        // Si no existe camino
        return -1;
    }

    /**
     * Obtiene la lista de casillas ortogonalmente adyacentes a actual que están dentro
     * del tablero y cuyo paso no está bloqueado por ningún muro.
     *
     * @param actual Casilla desde la que se evalúan los desplazamientos.
     * @param muros Lista de muros activos en el tablero.
     * @param tamanoTablero Dimensión lateral del tablero.
     * @return Lista de posiciones vecinas directamente alcanzables desde actual.
     */
    List<Posicion> obtenerVecinosValidos(Posicion actual, List<Muro> muros, int tamanoTablero) {
        // Precondiciones
        assert actual != null : "La posicion actual es null";
        assert muros != null : "La lista de muros es null";
        assert tamanoTablero > 0 : "El tamaño del tablero es 0 o negativo";
        assert actual.esValida(tamanoTablero) : "La posicion actual no es valida";

        List<Posicion> vecinos = new ArrayList<>();

        // Movimientos posibles: Arriba, Abajo, Izquierda, Derecha
        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        for (int i = 0; i < 4; i++) {
            Posicion destino = new Posicion(actual.getX() + dx[i], actual.getY() + dy[i]);
            if (destino.esValida(tamanoTablero)) {
                if (!estaBloqueadoPorMuro(actual, destino, muros)) {
                    vecinos.add(destino);
                }
            }
        }

        return vecinos;
    }

    /**
     * Comprueba si algún muro de la lista impide el paso directo entre origen y destino.
     *
     * @param origen Casilla de salida.
     * @param destino Casilla de destino adyacente.
     * @param muros Lista de muros colocados en el tablero.
     * @return true si al menos un muro bloquea la arista entre ambas casillas.
     */
    boolean estaBloqueadoPorMuro(Posicion origen, Posicion destino, List<Muro> muros) {
        // Precondiciones
        assert origen != null : "La posicion origen es null";
        assert destino != null : "La posicion destino es null";
        assert muros != null : "La lista de muros es null";

        for (Muro muro : muros) {
            if (muro.bloqueaPaso(origen, destino)) {
                return true;
            }
        }
        return false;
    }
}
