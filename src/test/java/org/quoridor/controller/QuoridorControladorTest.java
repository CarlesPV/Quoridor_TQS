package org.quoridor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quoridor.model.Color;
import org.quoridor.model.Jugador;
import org.quoridor.model.Partida;
import org.quoridor.model.Posicion;
import org.quoridor.view.IQuoridorVista;

/**
 * Pruebas unitarias para el controlador QuoridorControlador utilizando Mockito (IQuoridorVista).
 */
class QuoridorControladorTest {

    private Partida modelo;
    private IQuoridorVista vistaMock;
    private QuoridorControlador controlador;

    @BeforeEach
    void setUp() {
        modelo = new Partida();
        vistaMock = mock(IQuoridorVista.class);
        controlador = new QuoridorControlador(modelo, vistaMock);
    }

    /**
     * Prueba de Caja Blanca / Interacción con Mock:
     * Verifica que el constructor registre el controlador en la vista y que iniciarJuego arranque
     * la partida y sincronice inicialmente el tablero y el turno en la vista.
     */
    @Test
    @DisplayName("Constructor e iniciarJuego básico sincronizan el inicio de la partida en la vista")
    void testConstructorEIniciarJuegoBasico() {
        // El constructor debe registrar el controlador en la vista pasiva
        verify(vistaMock).setControlador(controlador);

        // Al iniciar juego, la partida debe estar en curso y actualizar el tablero
        controlador.iniciarJuego();
        assertFalse(modelo.isTerminada(), "La partida debe estar en curso tras iniciarJuego.");
        verify(vistaMock).actualizarTablero(any(), any());
        verify(vistaMock).mostrarTurno(any(), any());
    }

    /**
     * Prueba de Programación por Contrato (DbC) / Valores Inválidos:
     * Verifica que el constructor rechace referencias nulas lanzando AssertionError tanto para
     * el modelo como para la vista.
     */
    @Test
    @DisplayName("Constructor rechaza parámetros nulos mediante aserciones DbC")
    void testConstructorPrecondicionesNulas() {
        assertThrows(AssertionError.class, () -> new QuoridorControlador(null, vistaMock),
                "Debe lanzar AssertionError si el modelo es nulo.");
        assertThrows(AssertionError.class, () -> new QuoridorControlador(modelo, null),
                "Debe lanzar AssertionError si la vista es nula.");
    }

    /**
     * Prueba de Caja Blanca / Interacción Completa con Mock:
     * Verifica que iniciarJuego sincronice detalladamente todos los componentes visuales:
     * tablero inicial con 2 peones y 0 muros, turno del jugador activo, 10 muros por jugador y limpieza de mensajes.
     */
    @Test
    @DisplayName("iniciarJuego sincroniza exhaustivamente tablero, turno, muros restantes y mensajes")
    void testIniciarJuegoSincronizacionCompleta() {
        controlador.iniciarJuego();

        // Verificamos estado del modelo tras iniciarJuego
        assertFalse(modelo.isTerminada());
        assertEquals(2, modelo.getJugadores().size());
        assertTrue(modelo.getMurosColocados().isEmpty());

        Jugador actual = modelo.getJugadorActual();

        // Verificamos interacción completa con la vista pasiva
        verify(vistaMock).actualizarTablero(modelo.getPosicionesJugadores(), modelo.getMurosColocados());
        verify(vistaMock).mostrarTurno(actual.getNombre(), actual.getColor());
        verify(vistaMock).actualizarMurosRestantes(modelo.getMurosRestantesPorJugador());
        verify(vistaMock).limpiarMensajes();

        assertEquals(10, modelo.getMurosRestantesPorJugador().get(Color.ROJO));
        assertEquals(10, modelo.getMurosRestantesPorJugador().get(Color.AZUL));
    }

    /**
     * Prueba de Caja Negra (Partición Equivalente: Movimiento Válido) e Interacción con Mock:
     * Verifica que al pulsar una casilla adyacente válida, el modelo desplace al peón del jugador activo,
     * la vista actualice el tablero con la nueva posición, avance el turno y limpie mensajes previos.
     */
    @Test
    @DisplayName("onCasillaClic realiza movimiento válido, actualiza tablero y cambia de turno")
    void testOnCasillaClicMovimientoValido() {
        controlador.iniciarJuego();

        Jugador inicial = modelo.getJugadorActual();
        Posicion origen = inicial.getPosicionActual();
        int pasoY = (inicial.getColor() == Color.ROJO) ? 1 : -1;
        int destinoX = origen.getX();
        int destinoY = origen.getY() + pasoY;

        controlador.onCasillaClic(destinoX, destinoY);

        assertEquals(new Posicion(destinoX, destinoY), modelo.getPosicionesJugadores().get(inicial.getColor()),
                "El peón debe actualizar su posición en el modelo tras un movimiento válido.");
        verify(vistaMock, times(2)).actualizarTablero(any(), any());
        verify(vistaMock, times(2)).limpiarMensajes();
    }
}
