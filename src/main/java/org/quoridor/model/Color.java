package org.quoridor.model;

/**
 * Colores identificativos asignados a los jugadores en una partida de Quoridor.
 * En modo estándar de 2 jugadores se utilizan ROJO y AZUL;
 * en modo de 4 jugadores se incorporan VERDE y AMARILLO.
 */
public enum Color {
    /** Color asignado al Jugador 1 (parte desde la fila superior). */
    ROJO,
    /** Color asignado al Jugador 2 (parte desde la fila inferior). */
    AZUL,
    /** Color asignado al Jugador 3 en partidas de cuatro jugadores. */
    VERDE,
    /** Color asignado al Jugador 4 en partidas de cuatro jugadores. */
    AMARILLO
}
