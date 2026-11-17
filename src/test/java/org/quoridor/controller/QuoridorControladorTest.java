package org.quoridor.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.quoridor.model.Color;
import org.quoridor.model.ISorteadorTurno;
import org.quoridor.model.Jugador;
import org.quoridor.model.Orientacion;
import org.quoridor.model.Partida;
import org.quoridor.model.Posicion;
import org.quoridor.model.Tablero;
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

    /**
     * Prueba de Caja Negra (Valores Límite y Frontera):
     * Verifica que si el usuario pulsa coordenadas fuera del rango del tablero (0 a 8),
     * el controlador no invoque al modelo y muestre un error descriptivo en la vista.
     */
    @Test
    @DisplayName("onCasillaClic muestra error al pulsar fuera de los límites del tablero")
    void testOnCasillaClicCasillaFueraDelTablero() {
        controlador.iniciarJuego();

        controlador.onCasillaClic(-1, 4);
        verify(vistaMock).mostrarError("Casilla fuera del tablero.");

        controlador.onCasillaClic(9, 4);
        verify(vistaMock, times(2)).mostrarError("Casilla fuera del tablero.");
    }

    /**
     * Prueba de Caja Negra (Partición Inválida):
     * Verifica que si el movimiento no es legal (ej. casilla no adyacente),
     * la vista informe de que el movimiento no es válido.
     */
    @Test
    @DisplayName("onCasillaClic muestra error ante un movimiento inválido según las reglas")
    void testOnCasillaClicMovimientoInvalido() {
        controlador.iniciarJuego();

        // (0, 4) no es adyacente a la casilla de inicio (4, 0) o (4, 8)
        controlador.onCasillaClic(0, 4);
        verify(vistaMock).mostrarError("Movimiento no válido.");
    }

    /**
     * Prueba de Caja Blanca / Camino de Partida Terminada:
     * Verifica que si se intenta mover con la partida terminada, la vista muestre un mensaje
     * indicando que ha concluido y debe pulsarse reiniciar.
     */
    @Test
    @DisplayName("onCasillaClic rechaza jugadas si la partida ya ha finalizado")
    void testOnCasillaClicPartidaTerminada() {
        // Una partida recién instanciada sin iniciar está terminada
        assertTrue(modelo.isTerminada());

        controlador.onCasillaClic(4, 1);
        verify(vistaMock).mostrarError("La partida ya ha terminado. Pulsa Reiniciar.");
    }

    /**
     * Prueba de Caja Blanca / Camino de Victoria:
     * Verifica que cuando un peón alcanza su fila objetivo, se comprueba la victoria
     * y la vista anuncia al ganador en lugar de avanzar al siguiente turno.
     */
    @Test
    @DisplayName("onCasillaClic anuncia la victoria del jugador al alcanzar la fila meta")
    void testOnCasillaClicVictoria() {
        ISorteadorTurno sorteadorMock = mock(ISorteadorTurno.class);
        when(sorteadorMock.sortear(any())).thenReturn(0);

        Partida partidaVictoria = new Partida(new Tablero(), sorteadorMock);
        Jugador j1 = new Jugador("Ganador", Color.ROJO, new Posicion(4, 7), 8, 10);
        Jugador j2 = new Jugador("Otro", Color.AZUL, new Posicion(0, 0), 0, 10);
        partidaVictoria.configurarPartida(List.of(j1, j2));
        partidaVictoria.iniciarPartida();

        QuoridorControlador ctrlVictoria = new QuoridorControlador(partidaVictoria, vistaMock);
        ctrlVictoria.onCasillaClic(4, 8);

        assertTrue(partidaVictoria.isTerminada(), "La partida debe finalizar tras alcanzar la meta.");
        verify(vistaMock).mostrarVictoria("Ganador");
    }

    /**
     * Prueba de Caja Negra (Partición Válida) e Interacción con Mock:
     * Verifica que colocar un muro válido lo registre en el modelo, actualice el tablero,
     * decremente los muros restantes y avance el turno en la interfaz visual.
     */
    @Test
    @DisplayName("onInterseccionClic coloca muro válido, actualiza tablero, muros restantes y turno")
    void testOnInterseccionClicColocacionValida() {
        controlador.iniciarJuego();

        controlador.onInterseccionClic(2, 2, Orientacion.HORIZONTAL);

        assertEquals(1, modelo.getMurosColocados().size(),
                "Debe haber 1 muro colocado en el modelo tras colocarlo válidamente.");
        assertEquals(new Posicion(2, 2), modelo.getMurosColocados().get(0).getOrigen());
        assertEquals(Orientacion.HORIZONTAL, modelo.getMurosColocados().get(0).getOrientacion());

        // 1 al iniciar + 1 al colocar muro = 2 invocaciones
        verify(vistaMock, times(2)).actualizarTablero(any(), any());
        verify(vistaMock, times(2)).actualizarMurosRestantes(any());
        verify(vistaMock, times(2)).mostrarTurno(any(), any());
    }
}
