package org.quoridor.model;

import java.util.*;

/**
 * Implementación estándar de ISorteadorTurno que escoge de forma uniforme
 * y pseudoaleatoria el índice del jugador que comienza la partida.
 */
public class SorteadorTurno implements ISorteadorTurno {

    /**
     * Construye un sorteador de turnos por defecto.
     */
    public SorteadorTurno() {
    }

    /**
     * Sortea de manera aleatoria el índice del jugador inicial entre los participantes.
     *
     * @param jugadores Lista de jugadores inscritos en la partida.
     * @return Índice entero del jugador seleccionado en el intervalo [0, jugadores.size() - 1].
     */
    @Override
    public int sortear(List<Jugador> jugadores) {
        // Precondición
        assert jugadores != null && !jugadores.isEmpty() : "La lista de jugadores no puede estar vacía o null";

        Random random = new Random();
        int indice = random.nextInt(jugadores.size());

        // Postcondición
        assert indice >= 0 && indice < jugadores.size() : "Indice fuera de rango de jugadores";
        return indice;
    }
}
