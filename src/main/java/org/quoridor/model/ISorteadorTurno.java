package org.quoridor.model;

import java.util.*;

/**
 * Contrato de estrategia para seleccionar aleatoriamente o de forma controlada
 * el jugador que inicia la partida.
 */
public interface ISorteadorTurno {

    /**
     * Selecciona el índice del jugador que comenzará el primer turno.
     *
     * @param jugadores Lista no vacía de jugadores participantes en la partida.
     * @return Índice válido dentro del rango.
     */
    int sortear(List<Jugador> jugadores);
}
