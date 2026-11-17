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
import static org.mockito.Mockito.atLeastOnce;

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

    /**
     * Prueba de Caja Blanca / Camino de Partida Terminada:
     * Verifica que si la partida ha finalizado, al intentar colocar un muro la vista muestre
     * un mensaje de error indicando que la partida ya terminó.
     */
    @Test
    @DisplayName("onInterseccionClic rechaza colocación de muros si la partida ya ha finalizado")
    void testOnInterseccionClicPartidaTerminada() {
        // Partida recién instanciada sin iniciar → isTerminada() == true
        assertTrue(modelo.isTerminada());

        controlador.onInterseccionClic(2, 2, Orientacion.HORIZONTAL);
        verify(vistaMock).mostrarError("La partida ya ha terminado. Pulsa Reiniciar.");
    }

    /**
     * Prueba de Caja Negra (Valores Inválidos / Nulos):
     * Verifica que si se pasa una orientación nula, el controlador informe del error
     * en la vista en lugar de lanzar una excepción al crear el Muro.
     */
    @Test
    @DisplayName("onInterseccionClic muestra error cuando la orientación es nula")
    void testOnInterseccionClicOrientacionNula() {
        controlador.iniciarJuego();

        controlador.onInterseccionClic(3, 3, null);
        verify(vistaMock).mostrarError(
                "No se puede colocar el muro en esa posición o no te quedan muros.");
    }

    /**
     * Prueba de Caja Negra (Partición Inválida / Valor Frontera):
     * Verifica que si las coordenadas del muro provocan un rechazo del modelo
     * (posición fuera del rango válido para muros), la vista muestre el error correspondiente.
     */
    @Test
    @DisplayName("onInterseccionClic muestra error al intentar colocar un muro en posición inválida")
    void testOnInterseccionClicMuroInvalido() {
        controlador.iniciarJuego();

        // Coordenada x=8 en tablero 9x9 → no hay espacio para muro horizontal de 2 celdas
        controlador.onInterseccionClic(8, 4, Orientacion.HORIZONTAL);
        verify(vistaMock).mostrarError(
                "No se puede colocar el muro en esa posición o no te quedan muros.");
    }

    /**
     * Prueba de Caja Negra (Valor Límite: 0 muros restantes):
     * Verifica que cuando un jugador ha consumido todos sus muros, el controlador muestre
     * un error descriptivo al intentar colocar uno más.
     */
    @Test
    @DisplayName("onInterseccionClic muestra error cuando el jugador no tiene muros restantes")
    void testOnInterseccionClicSinMurosRestantes() {
        // Configurar partida con solo 1 muro por jugador para agotar rápidamente
        ISorteadorTurno sorteadorFijo = mock(ISorteadorTurno.class);
        when(sorteadorFijo.sortear(any())).thenReturn(0);

        Partida partidaLimitada = new Partida(new Tablero(), sorteadorFijo);
        partidaLimitada.configurarPartida(2, Tablero.TAMANO_TABLERO, 1, true);
        partidaLimitada.iniciarPartida();

        QuoridorControlador ctrlLimitado = new QuoridorControlador(partidaLimitada, vistaMock);

        // Colocar el único muro disponible (turno del jugador 1)
        ctrlLimitado.onInterseccionClic(2, 2, Orientacion.HORIZONTAL);
        assertEquals(1, partidaLimitada.getMurosColocados().size());

        // Ahora es turno del jugador 2: colocar su único muro
        ctrlLimitado.onInterseccionClic(5, 5, Orientacion.VERTICAL);
        assertEquals(2, partidaLimitada.getMurosColocados().size());

        // Vuelve al jugador 1: ya no tiene muros → debe fallar
        ctrlLimitado.onInterseccionClic(3, 3, Orientacion.HORIZONTAL);
        verify(vistaMock).mostrarError(
                "No se puede colocar el muro en esa posición o no te quedan muros.");
    }
    
    /**
     * Prueba de Caja Blanca / Integración con Mock:
     * Verifica que onBotonReiniciar reinicie la partida (dejándola activa) 
     * y sincronice la limpieza de mensajes y la actualización del tablero en la vista.
     */
    @Test
    @DisplayName("onBotonReiniciar reactiva la partida terminada y resetea posicion de peones")
    void testOnBotonReiniciar() {
    	// Iniciamos partida y movemos el jugador actual
        controlador.iniciarJuego();
        
        // Guardamos cuál era su posición inicial antes de mover
        Posicion posicionInicialJugador1 = modelo.getJugadores().get(0).getPosicionActual();
        
        // Realizamos un movimiento para cambiarlo de sitio
        controlador.onCasillaClic(4, 1);

        // Invocamos el reinicio
        controlador.onBotonReiniciar();

        // Verificamos que la partida ya no está terminada
        assertFalse(modelo.isTerminada(), "La partida debe dejar de estar terminada tras reiniciar.");
        
        // Verificamos que el jugador 1 ha vuelto exactamente a su posición inicial
        Posicion posicionTrasReiniciar = modelo.getJugadores().get(0).getPosicionActual();
        assertEquals(posicionInicialJugador1, posicionTrasReiniciar, "El peón del jugador debe volver a su posición de inicio tras el reinicio.");

        // Verificaciones de la vista
        verify(vistaMock, atLeastOnce()).actualizarTablero(any(), any());
        // times(2) ya que al iniciar juego se llama a limpiarMensajes 1 vez y otra al reiniciar
        verify(vistaMock, times(2)).limpiarMensajes();
    }
    
    /**
     * Prueba de Caja Blanca / Integración con Mock:
     * Verifica que onBotonReiniciar reactive la partida,
     * devuelva los muros a sus contadores iniciales y limpie el tablero y la vista.
     */
    @Test
    @DisplayName("onBotonReiniciar reactiva la partida terminada y resetea los muros colocados")
    void testOnBotonReiniciarReseteaEstadoCompleto() {
        // Iniciamos partida y realizamos acciones (movemos y colocamos un muro)
        controlador.iniciarJuego();
        
        // Colocamos un muro para modificar el estado del tablero
        controlador.onInterseccionClic(2, 2, Orientacion.HORIZONTAL);
        
        // Verificamos que el muro se colocó (ahora hay 1 en el tablero)
        assertEquals(1, modelo.getMurosColocados().size());

        // Invocamos el reinicio
        controlador.onBotonReiniciar();

        // Comprobaciones del modelo tras el reinicio
        assertFalse(modelo.isTerminada(), "La partida debe dejar de estar terminada tras reiniciar.");

        // Comprobar que los muros colocados en el tablero vuelven a 0
        assertTrue(modelo.getMurosColocados().isEmpty(), "El tablero debe quedarse sin muros colocados tras reiniciar.");
    }
}
