package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias para la clase Jugador.
 * Técnicas aplicadas: Caja Negra (Particiones Equivalentes y Valores Límite/Frontera)
 * y Programación por Contrato (DbC — Precondiciones, Postcondiciones e Invariante).
 */
class JugadorTest {

    private Jugador jugador;

    @BeforeEach
    void setUp() throws Exception {
        jugador = new Jugador();
    }

    /**
     * Prueba de Caja Negra (Partición Equivalente):
     * Verifica que el constructor de paquete reservado para pruebas DbC deje los atributos
     * de referencia en null y los enteros en 0.
     */
    @Test
    @DisplayName("Constructor de paquete: inicializa atributos por defecto para pruebas DbC")
    void testConstructor() {
        assertNull(jugador.getNombre());
        assertNull(jugador.getColor());
        assertNull(jugador.getPosicionActual());
        assertEquals(0, jugador.getFilaObjetivo());
        assertEquals(0, jugador.getMurosRestantes());
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Límite/Frontera):
     * Comprueba la creación de jugadores en modo 2P (10 muros) y 4P (5 muros), con 0 muros
     * como límite inferior válido, filas objetivo extremas (0 y 8) y esquinas del tablero.
     */
    @Test
    @DisplayName("Constructor parametrizado: configuraciones 2P, 4P, 0 muros y esquinas límite")
    void testConstructorConParametros() {
        // Partición válida estándar (2 jugadores, 10 muros, meta en fila 8)
        Posicion pos = new Posicion(4, 0);
        jugador = new Jugador("Jugador 1", Color.ROJO, pos, 8, 10);

        assertEquals("Jugador 1", jugador.getNombre());
        assertEquals(Color.ROJO, jugador.getColor());
        assertEquals(pos, jugador.getPosicionActual());
        assertEquals(8, jugador.getFilaObjetivo());
        assertEquals(10, jugador.getMurosRestantes());

        // Valor límite inferior de fila objetivo (0) con salida en fila superior límite (8)
        Posicion posFila8 = new Posicion(4, 8);
        Jugador jugador2 = new Jugador("Jugador 2", Color.AZUL, posFila8, 0, 10);
        assertEquals(0, jugador2.getFilaObjetivo());
        assertEquals(posFila8, jugador2.getPosicionActual());

        // Valor límite inferior válido de muros (0 muros disponibles)
        Jugador jugadorSinMuros = new Jugador("Sin Muros", Color.VERDE, new Posicion(0, 4), 8, 0);
        assertEquals(0, jugadorSinMuros.getMurosRestantes());

        // Partición válida para 4 jugadores (5 muros iniciales por jugador)
        Jugador jugador4P = new Jugador("Jugador 4", Color.AMARILLO, new Posicion(8, 4), 0, 5);
        assertEquals(5, jugador4P.getMurosRestantes());
        assertEquals(Color.AMARILLO, jugador4P.getColor());

        // Valores frontera en esquinas opuestas del tablero (0, 0) y (8, 8)
        Jugador jugadorEsquina = new Jugador("Esquina", Color.ROJO, new Posicion(0, 0), 8, 10);
        assertEquals(new Posicion(0, 0), jugadorEsquina.getPosicionActual());
        Jugador jugadorEsquinaMax = new Jugador("EsquinaMax", Color.AZUL, new Posicion(8, 8), 0, 10);
        assertEquals(new Posicion(8, 8), jugadorEsquinaMax.getPosicionActual());
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Límite/Frontera):
     * Valida moverA en desplazamientos consecutivos, hacia las cuatro esquinas límite,
     * los cuatro bordes del tablero y manteniendo la misma posición.
     */
    @Test
    @DisplayName("moverA: desplazamientos consecutivos, esquinas límite, bordes y misma casilla")
    void testMoverA() {
        Posicion posInicial = new Posicion(4, 0);
        Posicion posDestino = new Posicion(4, 1);
        jugador = new Jugador("Jugador 1", Color.ROJO, posInicial, 8, 10);

        jugador.moverA(posDestino);
        assertEquals(posDestino, jugador.getPosicionActual());

        // Partición equivalente válida: movimientos consecutivos
        Posicion posSiguiente = new Posicion(4, 2);
        jugador.moverA(posSiguiente);
        assertEquals(posSiguiente, jugador.getPosicionActual());

        // Valores límite y frontera válidos: cuatro esquinas del tablero (0,0), (8,8), (8,0), (0,8)
        Posicion esquinaSupIzq = new Posicion(0, 0);
        jugador.moverA(esquinaSupIzq);
        assertEquals(esquinaSupIzq, jugador.getPosicionActual());
        Posicion esquinaInfDer = new Posicion(8, 8);
        jugador.moverA(esquinaInfDer);
        assertEquals(esquinaInfDer, jugador.getPosicionActual());
        Posicion esquinaSupDer = new Posicion(8, 0);
        jugador.moverA(esquinaSupDer);
        assertEquals(esquinaSupDer, jugador.getPosicionActual());
        Posicion esquinaInfIzq = new Posicion(0, 8);
        jugador.moverA(esquinaInfIzq);
        assertEquals(esquinaInfIzq, jugador.getPosicionActual());

        // Valores frontera válidos en centros de los cuatro bordes del tablero
        Posicion bordeSuperior = new Posicion(4, 0);
        jugador.moverA(bordeSuperior);
        assertEquals(bordeSuperior, jugador.getPosicionActual());
        Posicion bordeInferior = new Posicion(4, 8);
        jugador.moverA(bordeInferior);
        assertEquals(bordeInferior, jugador.getPosicionActual());
        Posicion bordeIzquierdo = new Posicion(0, 4);
        jugador.moverA(bordeIzquierdo);
        assertEquals(bordeIzquierdo, jugador.getPosicionActual());
        Posicion bordeDerecho = new Posicion(8, 4);
        jugador.moverA(bordeDerecho);
        assertEquals(bordeDerecho, jugador.getPosicionActual());

        // Partición válida: mover a la misma posición actual
        jugador.moverA(bordeDerecho);
        assertEquals(bordeDerecho, jugador.getPosicionActual());
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valor Límite Inferior):
     * Verifica el decremento unitario en consumirMuro para partidas de 2P (10 muros) y 4P (5 muros)
     * hasta alcanzar exactamente 0 muros restantes.
     */
    @Test
    @DisplayName("consumirMuro: decremento unitario en 2P y 4P hasta límite inferior 0")
    void testConsumirMuro() {
        // Partición estándar 2P (10 muros): consumos consecutivos
        jugador = new Jugador("Jugador 1", Color.ROJO, new Posicion(4, 0), 8, 10);
        jugador.consumirMuro();
        assertEquals(9, jugador.getMurosRestantes());

        jugador.consumirMuro();
        assertEquals(8, jugador.getMurosRestantes());
        jugador.consumirMuro();
        assertEquals(7, jugador.getMurosRestantes());

        // Partición para 4 jugadores (5 muros iniciales por jugador)
        Jugador jugador4P = new Jugador("Jugador 4", Color.AMARILLO, new Posicion(8, 4), 0, 5);
        jugador4P.consumirMuro();
        assertEquals(4, jugador4P.getMurosRestantes());

        // Valor límite inferior válido: agotar todos los muros hasta quedar exactamente en 0
        jugador4P.consumirMuro(); // quedan 3
        jugador4P.consumirMuro(); // quedan 2
        jugador4P.consumirMuro(); // queda 1
        jugador4P.consumirMuro(); // quedan 0 (límite inferior)
        assertEquals(0, jugador4P.getMurosRestantes());
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Frontera 0/1):
     * Comprueba hasMurosRestantes con 10 muros, 5 muros, frontera 1 muro, transición a 0
     * tras consumir el último muro y construcción inicial con 0 muros.
     */
    @Test
    @DisplayName("hasMurosRestantes: particiones válidas, frontera 1 muro y límite 0 tras consumir")
    void testHasMurosRestantes() {
        // Partición estándar 2P (10 muros) y 4P (5 muros)
        jugador = new Jugador("Jugador 1", Color.ROJO, new Posicion(4, 0), 8, 10);
        assertTrue(jugador.hasMurosRestantes());

        Jugador jugador4P = new Jugador("Jugador 4", Color.AMARILLO, new Posicion(8, 4), 0, 5);
        assertTrue(jugador4P.hasMurosRestantes());

        // Valor frontera: exactamente 1 muro restante
        Jugador jugador1Muro = new Jugador("1 Muro", Color.AZUL, new Posicion(4, 8), 0, 1);
        assertTrue(jugador1Muro.hasMurosRestantes());

        // Transición de estado al límite inferior (1 -> 0 muros)
        jugador1Muro.consumirMuro();
        assertFalse(jugador1Muro.hasMurosRestantes());

        // Partición inicial sin muros (0 muros disponibles)
        Jugador jugadorSinMuros = new Jugador("Sin Muros", Color.VERDE, new Posicion(0, 4), 8, 0);
        assertFalse(jugadorSinMuros.hasMurosRestantes());
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Límite/Frontera):
     * Valida haLlegadoAMeta para metas extremas (fila 8 y fila 0), esquinas sobre la fila meta,
     * casillas frontera inmediatamente anteriores y una fila objetivo intermedia (y = 4) con fronteras 3 y 5.
     */
    @Test
    @DisplayName("haLlegadoAMeta: metas extremas (0 y 8), esquinas y fronteras de meta intermedia")
    void testHaLlegadoAMeta() {
        // Partición válida: jugador directamente en fila meta 8
        jugador = new Jugador("Jugador 1", Color.ROJO, new Posicion(4, 8), 8, 10);
        assertTrue(jugador.haLlegadoAMeta());

        // Partición equivalente: jugador fuera de su meta (en fila de origen)
        Jugador jugadorLejos = new Jugador("Lejos", Color.ROJO, new Posicion(4, 0), 8, 10);
        assertFalse(jugadorLejos.haLlegadoAMeta());

        // Partición para jugador con meta opuesta (fila 0)
        Jugador jugadorMeta0 = new Jugador("Jugador 2", Color.AZUL, new Posicion(4, 0), 0, 10);
        assertTrue(jugadorMeta0.haLlegadoAMeta());

        // Valor frontera: casilla inmediatamente anterior a la meta (fila 7 para meta 8)
        jugadorLejos.moverA(new Posicion(4, 7));
        assertFalse(jugadorLejos.haLlegadoAMeta());

        // Transición de estado: avanzar de fila 7 a la fila meta 8
        jugadorLejos.moverA(new Posicion(4, 8));
        assertTrue(jugadorLejos.haLlegadoAMeta());

        // Valores límite: esquinas sobre la fila meta 8 ((0,8) y (8,8))
        jugadorLejos.moverA(new Posicion(0, 8));
        assertTrue(jugadorLejos.haLlegadoAMeta());
        jugadorLejos.moverA(new Posicion(8, 8));
        assertTrue(jugadorLejos.haLlegadoAMeta());

        // Valor frontera y límites para meta 0 (fila 1 no es meta; esquinas (0,0) y (8,0) sí lo son)
        Jugador jugadorMeta0Aux = new Jugador("Meta0", Color.AZUL, new Posicion(4, 1), 0, 10);
        assertFalse(jugadorMeta0Aux.haLlegadoAMeta());
        jugadorMeta0Aux.moverA(new Posicion(0, 0));
        assertTrue(jugadorMeta0Aux.haLlegadoAMeta());
        jugadorMeta0Aux.moverA(new Posicion(8, 0));
        assertTrue(jugadorMeta0Aux.haLlegadoAMeta());

        // Valores frontera sobre fila objetivo intermedia (filaObjetivo = 4)
        // Frontera inferior (filaObjetivo - 1 -> y = 3)
        Jugador jugadorLimite = new Jugador("Limite", Color.AZUL, new Posicion(4, 3), 4, 10);
        assertFalse(jugadorLimite.haLlegadoAMeta());
        // Exactamente en filaObjetivo (y = 4)
        jugadorLimite.moverA(new Posicion(4, 4));
        assertTrue(jugadorLimite.haLlegadoAMeta());
        // Frontera superior (filaObjetivo + 1 -> y = 5)
        jugadorLimite.moverA(new Posicion(4, 5));
        assertFalse(jugadorLimite.haLlegadoAMeta());
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes y Valores Límite):
     * Verifica que reiniciarEstado restablezca posición y muros en configuraciones 2P, 4P,
     * esquinas límite del tablero y 0 muros iniciales.
     */
    @Test
    @DisplayName("reiniciarEstado: restablece origen y muros en modos 2P, 4P, esquinas y 0 muros")
    void testReiniciarEstado() {
        // Partición estándar 2P (10 muros)
        jugador = new Jugador("Jugador 1", Color.ROJO, new Posicion(4, 5), 8, 5);
        Posicion origen = new Posicion(4, 0);

        jugador.reiniciarEstado(origen, 10);
        assertEquals(origen, jugador.getPosicionActual());
        assertEquals(10, jugador.getMurosRestantes());

        // Partición para 4 jugadores (5 muros iniciales) y posición en fila 8
        Posicion origen4P = new Posicion(4, 8);
        jugador.reiniciarEstado(origen4P, 5);
        assertEquals(origen4P, jugador.getPosicionActual());
        assertEquals(5, jugador.getMurosRestantes());

        // Valores límite: esquinas del tablero como origen ((0,0) y (8,8))
        Posicion esquinaSupIzq = new Posicion(0, 0);
        jugador.reiniciarEstado(esquinaSupIzq, 10);
        assertEquals(esquinaSupIzq, jugador.getPosicionActual());
        assertEquals(10, jugador.getMurosRestantes());

        Posicion esquinaInfDer = new Posicion(8, 8);
        jugador.reiniciarEstado(esquinaInfDer, 5);
        assertEquals(esquinaInfDer, jugador.getPosicionActual());
        assertEquals(5, jugador.getMurosRestantes());

        // Valor límite inferior válido de muros iniciales (0 muros)
        Posicion centro = new Posicion(4, 4);
        jugador.reiniciarEstado(centro, 0);
        assertEquals(centro, jugador.getPosicionActual());
        assertEquals(0, jugador.getMurosRestantes());
    }

    /**
     * Prueba de Programación por Contrato (DbC — Precondiciones e Invariante):
     * Comprueba que todas las precondiciones inválidas del constructor, moverA, consumirMuro
     * y reiniciarEstado, así como la violación de invariante en un Jugador sin inicializar,
     * lancen AssertionError.
     */
    @Test
    @DisplayName("Contratos DbC: precondiciones inválidas e invariante de Jugador")
    void testPrecondicionesEInvariante() {
        Posicion posValida = new Posicion(4, 0);
        Posicion posInvalida = new Posicion(-1, 0);

        // Precondiciones inválidas del constructor
        assertThrows(AssertionError.class, () -> new Jugador(null, Color.ROJO, posValida, 8, 10));
        assertThrows(AssertionError.class, () -> new Jugador("   ", Color.ROJO, posValida, 8, 10));
        assertThrows(AssertionError.class, () -> new Jugador("J1", null, posValida, 8, 10));
        assertThrows(AssertionError.class, () -> new Jugador("J1", Color.ROJO, null, 8, 10));
        assertThrows(AssertionError.class, () -> new Jugador("J1", Color.ROJO, posInvalida, 8, 10));
        assertThrows(AssertionError.class, () -> new Jugador("J1", Color.ROJO, posValida, -1, 10));
        assertThrows(AssertionError.class, () -> new Jugador("J1", Color.ROJO, posValida, 9, 10));
        assertThrows(AssertionError.class, () -> new Jugador("J1", Color.ROJO, posValida, 8, -1));

        // Precondiciones inválidas de moverA
        jugador = new Jugador("J1", Color.ROJO, posValida, 8, 0);
        assertThrows(AssertionError.class, () -> jugador.moverA(null));
        assertThrows(AssertionError.class, () -> jugador.moverA(new Posicion(-1, 0)));
        assertThrows(AssertionError.class, () -> jugador.moverA(new Posicion(0, -1)));
        assertThrows(AssertionError.class, () -> jugador.moverA(new Posicion(9, 0)));
        assertThrows(AssertionError.class, () -> jugador.moverA(new Posicion(0, 9)));

        // Precondición inválida de consumirMuro sin muros disponibles
        assertThrows(AssertionError.class, () -> jugador.consumirMuro());

        // Precondiciones inválidas de reiniciarEstado
        assertThrows(AssertionError.class, () -> jugador.reiniciarEstado(null, 5));
        assertThrows(AssertionError.class, () -> jugador.reiniciarEstado(posInvalida, 5));
        assertThrows(AssertionError.class, () -> jugador.reiniciarEstado(new Posicion(9, 0), 5));
        assertThrows(AssertionError.class, () -> jugador.reiniciarEstado(posValida, -1));

        // Invariante violada con jugador sin inicializar (posicionActual == null)
        Jugador jugadorVacio = new Jugador();
        assertThrows(AssertionError.class, () -> jugadorVacio.hasMurosRestantes());
        assertThrows(AssertionError.class, () -> jugadorVacio.haLlegadoAMeta());
    }
}
