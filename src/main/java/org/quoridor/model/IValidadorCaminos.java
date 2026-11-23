package org.quoridor.model;

import java.util.*;

/**
 * Servicio de búsqueda de caminos utilizado para validar que los muros colocados
 * nunca encierren completamente a ningún jugador sin acceso a su fila objetivo.
 */
public interface IValidadorCaminos {

    /**
     * Comprueba si existe al menos un camino transitable desde una posición inicial
     * hasta cualquier casilla de la fila objetivo dada la configuración actual de muros.
     *
     * @param inicio Posición actual del peón dentro del tablero.
     * @param filaObjetivo Índice de fila que constituye la meta del jugador.
     * @param muros Lista de muros colocados que restringen el paso entre casillas adyacentes.
     * @param tamanoTablero Dimensión lateral del tablero cuadrado (en nuestro caso 9).
     * @return true si existe al menos 1 ruta válida hasta filaObjetivo false si está bloqueado.
     */
    boolean hayCaminoDisponible(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero);

    /**
     * Calcula la longitud mínima desde la posición inicial hasta la fila objetivo evitando
     * los muros colocados.
     *
     * @param inicio Posición actual del peón dentro del tablero.
     * @param filaObjetivo Índice de fila que constituye la meta del jugador.
     * @param muros Lista de muros colocados en el tablero.
     * @param tamanoTablero Dimensión lateral del tablero cuadrado.
     * @return Número mínimo de pasos hasta la meta, o -1 si no existe ningún camino posible.
     */
    int calcularDistanciaMinima(Posicion inicio, int filaObjetivo, List<Muro> muros, int tamanoTablero);
}
