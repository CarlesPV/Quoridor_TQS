package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase SorteadorTurno.
 * Técnicas aplicadas: Caja Negra (Particiones Equivalentes y Valores Límite/Frontera)
 * y Programación por Contrato (DbC — Precondiciones y Postcondición de rango).
 */
class SorteadorTurnoTest {

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Límite) y Contrato (DbC):
     * Evalúa sortear con 2 jugadores (partición estándar), 1 jugador (límite inferior válido),
     * 4 jugadores (partición máxima estándar y bucle de estabilidad), así como las precondiciones
     * inválidas con lista vacía (0 jugadores) y lista nula.
     */
    @Test
    @DisplayName("sortear: particiones de 1, 2 y 4 jugadores, estabilidad en bucle y precondiciones DbC")
    void testSortear() {
        ISorteadorTurno sorteador = new SorteadorTurno();

        // Partición estándar válida: 2 jugadores (índice esperado en [0, 1])
        List<Jugador> jugadores = Arrays.asList(
                new Jugador("J1", Color.ROJO, new Posicion(4, 8), 0, 5),
                new Jugador("J2", Color.AZUL, new Posicion(4, 0), 8, 5)
        );
        int indiceSeleccionado = sorteador.sortear(jugadores);
        assertTrue(indiceSeleccionado >= 0 && indiceSeleccionado < jugadores.size(),
                "El índice sorteado debe ser un índice válido de la lista de jugadores.");

        // Valor límite inferior válido: 1 jugador (siempre debe retornar índice 0)
        List<Jugador> jugadores1 = Arrays.asList(
                new Jugador("J1", Color.ROJO, new Posicion(4, 8), 0, 5)
        );
        indiceSeleccionado = sorteador.sortear(jugadores1);
        assertTrue(indiceSeleccionado == 0,
                "El índice sorteado debe ser 0.");

        // Partición estándar válida: 4 jugadores (índice esperado en [0, 3])
        List<Jugador> jugadores4 = Arrays.asList(
                new Jugador("J1", Color.ROJO, new Posicion(4, 8), 0, 5),
                new Jugador("J2", Color.AZUL, new Posicion(4, 0), 8, 5),
                new Jugador("J3", Color.VERDE, new Posicion(0, 4), 4, 5),
                new Jugador("J4", Color.AMARILLO, new Posicion(8, 4), 4, 5)
        );
        indiceSeleccionado = sorteador.sortear(jugadores4);
        assertTrue(indiceSeleccionado >= 0 && indiceSeleccionado < jugadores4.size(),
                "El índice sorteado debe ser un índice válido de la lista de jugadores.");

        // Verificación repetida de postcondición de rango en 15 invocaciones con 4 jugadores
        List<Jugador> jugadoresLoop = Arrays.asList(
                new Jugador("J1", Color.ROJO, new Posicion(4, 8), 0, 5),
                new Jugador("J2", Color.AZUL, new Posicion(4, 0), 8, 5),
                new Jugador("J3", Color.VERDE, new Posicion(0, 4), 4, 5),
                new Jugador("J4", Color.AMARILLO, new Posicion(8, 4), 4, 5)
        );
        for (int i = 0; i < 15; i++) {
            indiceSeleccionado = sorteador.sortear(jugadoresLoop);
            assertTrue(indiceSeleccionado >= 0 && indiceSeleccionado < jugadoresLoop.size(),
                    "El índice sorteado debe ser un índice válido de la lista de jugadores.");
        }

        // Precondición DbC (partición inválida / frontera 0 jugadores): lista vacía lanza AssertionError
        List<Jugador> jugadores0 = new ArrayList<>();
        assertThrows(AssertionError.class, () -> sorteador.sortear(jugadores0),
                "Debe lanzar AssertionError si la lista de jugadores está vacía.");

        // Precondición DbC (partición inválida): lista nula lanza AssertionError
        assertThrows(AssertionError.class, () -> sorteador.sortear(null),
                "Debe lanzar AssertionError si la lista de jugadores es null.");
    }
}
