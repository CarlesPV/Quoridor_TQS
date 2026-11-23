package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// Mock 2 (Manual - Modelo): Simula ISorteadorTurno con índice determinista y contador de llamadas
class SorteadorTurnoFijoMock implements ISorteadorTurno {

    private final int indiceFijo;
    private int vecesLlamado;
    private List<Jugador> ultimaListaJugadores;

    public SorteadorTurnoFijoMock(int indiceFijo) {
        this.indiceFijo = indiceFijo;
        this.vecesLlamado = 0;
    }

    @Override
    public int sortear(List<Jugador> jugadores) {
        this.vecesLlamado++;
        this.ultimaListaJugadores = (jugadores != null) ? new ArrayList<>(jugadores) : null;
        return indiceFijo; // Devuelve siempre el índice preconfigurado
    }

    public int getVecesLlamado() {
        return this.vecesLlamado;
    }

    public List<Jugador> getUltimaListaJugadores() {
        return this.ultimaListaJugadores;
    }
}

/**
 * Pruebas unitarias para la clase fachada Partida.
 * Técnicas aplicadas: Caja Blanca (Path Coverage en colocarMuro y comprobarVictoria),
 * Caja Negra (Particiones Equivalentes, Valores Límite/Frontera y Pairwise Testing),
 * Data-Driven Testing (@CsvSource), Programación por Contrato (DbC) y aislamiento con Mock Objects
 * (SorteadorTurnoFijoMock manual, Tablero mock con Mockito y Jugador mocks con Mockito).
 */
class PartidaTest {

    private Partida partida;
    // Mock 2 (Manual - Modelo): Simula ISorteadorTurno con índice determinista y contador de llamadas
    private ISorteadorTurno sorteadorMock;
    // Mock 3 (Mockito - Modelo): Simula Tablero para aislar la lógica de Partida
    private Tablero tableroMock;
    // Mock 4 (Mockito - Modelo): Simula Jugador para controlar muros restantes y meta
    private Jugador jugador1Mock;
    private Jugador jugador2Mock;

    @BeforeEach
    void setUp() throws Exception {
        // Mock 2 (Manual - Modelo): Simula ISorteadorTurno con índice determinista y contador de llamadas
        sorteadorMock = new SorteadorTurnoFijoMock(0);

        // Mock 3 (Mockito - Modelo): Simula Tablero para aislar la lógica de Partida
        tableroMock = mock(Tablero.class);
        // Mock 4 (Mockito - Modelo): Simula Jugador para controlar muros restantes y meta
        jugador1Mock = mock(Jugador.class);
        jugador2Mock = mock(Jugador.class);

        when(jugador1Mock.getColor()).thenReturn(Color.ROJO);
        when(jugador2Mock.getColor()).thenReturn(Color.AZUL);
        when(jugador1Mock.getNombre()).thenReturn("J1");
        when(jugador2Mock.getNombre()).thenReturn("J2");
        when(jugador1Mock.getPosicionActual()).thenReturn(new Posicion(4, 0));
        when(jugador2Mock.getPosicionActual()).thenReturn(new Posicion(4, 8));

        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
    }

    /**
     * Prueba de Caja Negra (Partición Equivalente) y Contrato (DbC):
     * Verifica el estado inicial del constructor por defecto (partida terminada y sin ganador)
     * y las precondiciones del constructor con inyección frente a dependencias nulas.
     */
    @Test
    @DisplayName("Constructores de Partida: estado inicial por defecto y precondiciones DbC con nulos")
    void testConstructor() {
        // Partición válida: constructor por defecto crea partida en estado terminada
        assertDoesNotThrow(() -> {
            Partida partidaPorDefecto = new Partida();
            assertTrue(partidaPorDefecto.isTerminada(), "Una partida recién creada debe estar terminada.");
            assertNull(partidaPorDefecto.comprobarVictoria(), "Sin jugadores no debe haber ganador.");
        });

        // Precondiciones DbC (partición inválida): tablero o sorteador nulos lanzan AssertionError
        assertThrows(AssertionError.class, () -> new Partida(null, sorteadorMock),
                "Debe lanzar AssertionError si el tablero es null.");
        assertThrows(AssertionError.class, () -> new Partida(tableroMock, null),
                "Debe lanzar AssertionError si el sorteador es null.");
    }

    /**
     * Prueba de Programación por Contrato (DbC — Precondiciones) y Caja Negra (Partición Inválida):
     * Comprueba que configurarPartida(List) rechace listas nulas, vacías, con elementos nulos,
     * con un único jugador o con un número de jugadores distinto de 2 y 4.
     */
    @Test
    @DisplayName("configurarPartida(List): rechazo DbC de lista nula, vacía, elementos nulos y tamaños != 2,4")
    void testConfigurarPartida() {
        // Precondiciones inválidas de lista de jugadores personalizada
        assertThrows(AssertionError.class, () -> partida.configurarPartida(null),
                "Debe lanzar AssertionError si la lista de jugadores es null.");
        assertThrows(AssertionError.class, () -> partida.configurarPartida(List.of()),
                "Debe lanzar AssertionError si la lista de jugadores está vacía.");
        assertThrows(AssertionError.class, () -> partida.configurarPartida(Arrays.asList(jugador1Mock, null)),
                "Debe lanzar AssertionError si la lista de jugadores contiene algún null.");
        assertThrows(AssertionError.class, () -> partida.configurarPartida(List.of(jugador1Mock)),
                "Debe lanzar AssertionError si la lista tiene menos de 2 jugadores.");
        assertThrows(AssertionError.class,
                () -> partida.configurarPartida(List.of(jugador1Mock, jugador2Mock, mock(Jugador.class))),
                "Debe lanzar AssertionError si el número de jugadores no es 2 ni 4.");
    }

    /**
     * Prueba de Caja Negra (Particiones Equivalentes), Contrato (DbC) e Interacción con Mock Manual:
     * Verifica iniciarPartida con sorteo determinista para J1 y J2, política sorteoAleatorio = true/false
     * comprobando las invocaciones a SorteadorTurnoFijoMock, y postcondiciones ante índices fuera de rango.
     */
    @Test
    @DisplayName("iniciarPartida: selección de turno inicial, política de sorteo y postcondición de rango")
    void testIniciarPartida() {
        // Partición válida (sorteoAleatorio = true, índice 0 -> empieza J1)
        SorteadorTurnoFijoMock sorteadorJ1 = (SorteadorTurnoFijoMock) sorteadorMock;
        partida.iniciarPartida();
        assertFalse(partida.isTerminada(), "La partida no debe estar terminada al iniciar.");
        assertEquals("J1", partida.getJugadorActual().getNombre(),
                "Debe empezar el Jugador 1 según el sorteador fijo.");
        assertEquals(1, sorteadorJ1.getVecesLlamado(),
                "Con sorteoAleatorio == true debe invocar al sorteador exactamente una vez.");
        assertEquals(List.of(jugador1Mock, jugador2Mock), sorteadorJ1.getUltimaListaJugadores(),
                "El sorteador debe recibir la lista completa de jugadores configurados.");

        // Partición válida (sorteoAleatorio = true, índice 1 -> empieza J2)
        SorteadorTurnoFijoMock sorteadorJ2 = new SorteadorTurnoFijoMock(1);
        partida = new Partida(tableroMock, sorteadorJ2);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        partida.iniciarPartida();
        assertFalse(partida.isTerminada(), "La partida no debe estar terminada al iniciar.");
        assertEquals("J2", partida.getJugadorActual().getNombre(),
                "Debe empezar el Jugador 2 según el sorteador fijo.");
        assertEquals(1, sorteadorJ2.getVecesLlamado(),
                "Con sorteoAleatorio == true debe invocar al sorteador exactamente una vez.");
        assertEquals(List.of(jugador1Mock, jugador2Mock), sorteadorJ2.getUltimaListaJugadores());

        // Partición válida: configuración estándar con sorteoAleatorio = true
        SorteadorTurnoFijoMock sorteadorActivo = new SorteadorTurnoFijoMock(1);
        Partida partidaConSorteo = new Partida(new Tablero(), sorteadorActivo);
        partidaConSorteo.configurarPartida(2, 9, 10, true);
        partidaConSorteo.iniciarPartida();
        assertEquals(1, sorteadorActivo.getVecesLlamado(),
                "Debe invocar al sorteador 1 vez cuando sorteoAleatorio es true.");
        assertEquals(2, sorteadorActivo.getUltimaListaJugadores().size());
        assertEquals(Color.AZUL, partidaConSorteo.getJugadorActual().getColor());

        // Partición válida: sorteoAleatorio = false no invoca al sorteador y empieza el jugador 0 (ROJO)
        SorteadorTurnoFijoMock sorteadorSinUso = new SorteadorTurnoFijoMock(1);
        Partida partidaSinSorteo = new Partida(new Tablero(), sorteadorSinUso);
        partidaSinSorteo.configurarPartida(2, 9, 10, false);
        partidaSinSorteo.iniciarPartida();
        assertFalse(partidaSinSorteo.isTerminada());
        assertEquals(0, sorteadorSinUso.getVecesLlamado(),
                "Con sorteoAleatorio == false no debe invocar al sorteador.");
        assertNull(sorteadorSinUso.getUltimaListaJugadores(),
                "Sin sorteo no debe registrarse ninguna invocación.");
        assertEquals(Color.ROJO, partidaSinSorteo.getJugadorActual().getColor(),
                "Sin sorteo debe comenzar siempre el primer jugador.");

        // Postcondición DbC (valores fuera de frontera): índice >= size y índice < 0 lanzan AssertionError
        sorteadorMock = new SorteadorTurnoFijoMock(2);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        assertThrows(AssertionError.class, () -> partida.iniciarPartida(),
                "Debe lanzar AssertionError si el índice sorteado supera el rango.");

        sorteadorMock = new SorteadorTurnoFijoMock(-1);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        assertThrows(AssertionError.class, () -> partida.iniciarPartida(),
                "Debe lanzar AssertionError si el índice sorteado es negativo.");
    }

    /**
     * Prueba de Caja Negra (Particiones Válidas e Inválidas), Caja Blanca e Interacción con Mocks:
     * Valida realizarMovimiento con mocks de Tablero y Jugador: desplazamiento válido y cambio de turno
     * para J1 y J2, movimiento rechazado sin cambio de turno, llegada a meta finalizando partida,
     * precondición de destino nulo, partida terminada y oponente con posición nula.
     */
    @Test
    @DisplayName("realizarMovimiento: movimientos válidos/inválidos, cambio de turno, meta y mocks")
    void testRealizarMovimiento() {
        // Partición válida: J1 realiza movimiento válido y el turno avanza a J2
        partida.iniciarPartida();
        Posicion destinoValido = new Posicion(4, 1);
        when(tableroMock.esMovimientoValido(any(Posicion.class), eq(destinoValido), any(Posicion.class)))
                .thenReturn(true);

        boolean resultado = partida.realizarMovimiento(destinoValido);

        assertTrue(resultado, "El movimiento debería realizarse con éxito.");
        verify(jugador1Mock, times(1)).moverA(destinoValido);
        verify(tableroMock, times(1)).setPosicionJugador(Color.ROJO, destinoValido);
        assertEquals("J2", partida.getJugadorActual().getNombre(), "El turno debe cambiar al siguiente jugador.");

        // Partición válida: J2 realiza movimiento válido y el turno regresa a J1
        sorteadorMock = new SorteadorTurnoFijoMock(1);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        partida.iniciarPartida();

        destinoValido = new Posicion(4, 7);
        when(tableroMock.esMovimientoValido(any(Posicion.class), eq(destinoValido), any(Posicion.class)))
                .thenReturn(true);

        resultado = partida.realizarMovimiento(destinoValido);

        assertTrue(resultado, "El movimiento debería realizarse con éxito.");
        verify(jugador2Mock, times(1)).moverA(destinoValido);
        verify(tableroMock, times(1)).setPosicionJugador(Color.AZUL, destinoValido);
        assertEquals("J1", partida.getJugadorActual().getNombre(), "El turno debe cambiar al siguiente jugador.");

        // Partición inválida: tablero rechaza el movimiento -> retorna false y conserva turno
        sorteadorMock = new SorteadorTurnoFijoMock(0);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        partida.iniciarPartida();
        Posicion destinoInvalido = new Posicion(0, 0);

        when(tableroMock.esMovimientoValido(any(Posicion.class), eq(destinoInvalido), any(Posicion.class)))
                .thenReturn(false);

        resultado = partida.realizarMovimiento(destinoInvalido);
        assertFalse(resultado, "El movimiento inválido debe ser rechazado.");
        verify(jugador1Mock, never()).moverA(destinoInvalido);
        verify(tableroMock, never()).setPosicionJugador(Color.ROJO, destinoInvalido);
        assertEquals("J1", partida.getJugadorActual().getNombre(),
                "El turno no debe cambiar si el movimiento falla.");

        // Partición válida con fin de partida: movimiento válido que alcanza la fila meta
        partida.iniciarPartida();
        Posicion destinoMeta = new Posicion(3, 8);
        when(tableroMock.esMovimientoValido(any(Posicion.class), eq(destinoMeta), any(Posicion.class)))
                .thenReturn(true);
        when(jugador1Mock.haLlegadoAMeta()).thenReturn(true);

        resultado = partida.realizarMovimiento(destinoMeta);

        assertTrue(resultado);
        verify(jugador1Mock, times(1)).moverA(destinoMeta);
        verify(tableroMock, times(1)).setPosicionJugador(Color.ROJO, destinoMeta);
        assertTrue(partida.isTerminada(),
                "La partida debería terminar cuando el jugador alcanza su fila objetivo.");

        // Precondición DbC: destino nulo lanza AssertionError
        partida.iniciarPartida();
        assertThrows(AssertionError.class, () -> partida.realizarMovimiento(null),
                "Debe lanzar AssertionError si el destino es null.");

        // Rama de partida terminada: movimiento rechazado con false
        sorteadorMock = new SorteadorTurnoFijoMock(0);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        Posicion destinoCualquiera = new Posicion(4, 7);
        resultado = partida.realizarMovimiento(destinoCualquiera);
        assertFalse(resultado, "No se puede mover si la partida está terminada.");

        // Caja Blanca (cobertura rama posOtro != null == false): oponente con posicionActual null
        when(jugador2Mock.getPosicionActual()).thenReturn(null);
        sorteadorMock = new SorteadorTurnoFijoMock(0);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        partida.iniciarPartida();
        Posicion destinoConOponenteSinPos = new Posicion(4, 1);
        when(tableroMock.esMovimientoValido(any(Posicion.class), eq(destinoConOponenteSinPos), isNull()))
                .thenReturn(true);

        assertTrue(partida.realizarMovimiento(destinoConOponenteSinPos),
                "Debe permitir el movimiento cuando el oponente tiene posición null y el tablero lo aprueba.");
        verify(jugador1Mock, times(2)).moverA(destinoConOponenteSinPos);
    }

    /**
     * Prueba de Caja Blanca (Path Coverage — Caminos Independientes 1 a 5 en colocarMuro):
     * Cubre los 5 caminos independientes del método:
     *  - Camino 1: m == null -> AssertionError.
     *  - Camino 2: partida terminada -> false.
     *  - Camino 3: jugador sin muros restantes -> false.
     *  - Camino 4: colocación inválida en tablero -> false.
     *  - Camino 5: colocación exitosa -> coloca muro, consume 1 muro y avanza turno.
     */
    @Test
    @DisplayName("Path Coverage en colocarMuro (5 caminos independientes aislados con mocks)")
    void testColocarMuro() {
        // Camino 1 (Path Coverage): m == null -> lanza AssertionError
        partida.iniciarPartida();
        assertThrows(AssertionError.class, () -> partida.colocarMuro(null),
                "Camino 1: Debe lanzar AssertionError si el muro es null.");

        // Camino 2 (Path Coverage): terminada == true -> retorna false
        sorteadorMock = new SorteadorTurnoFijoMock(0);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        Muro muroPrueba = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
        boolean colocado = partida.colocarMuro(muroPrueba);
        assertFalse(colocado, "Camino 2: No se puede colocar muros si la partida está terminada.");

        // Camino 3 (Path Coverage): !hasMurosRestantes -> retorna false sin consultar al tablero
        partida.iniciarPartida();
        when(jugador1Mock.hasMurosRestantes()).thenReturn(false);
        colocado = partida.colocarMuro(muroPrueba);
        assertFalse(colocado, "Camino 3: No se puede colocar muro si el jugador no tiene restantes.");
        verify(jugador1Mock, never()).consumirMuro();
        verify(tableroMock, never()).esColocacionMuroValida(any());

        // Camino 4 (Path Coverage): !esColocacionMuroValida -> retorna false sin consumir muro
        partida.iniciarPartida();
        when(jugador1Mock.hasMurosRestantes()).thenReturn(true);
        when(tableroMock.esColocacionMuroValida(any(Muro.class))).thenReturn(false);
        colocado = partida.colocarMuro(muroPrueba);
        assertFalse(colocado, "Camino 4: Posición del muro inválida según el tablero.");
        verify(tableroMock, times(1)).esColocacionMuroValida(any());
        verify(jugador1Mock, never()).consumirMuro();

        // Camino 5 (Path Coverage): Colocación exitosa -> coloca muro, consume 1 y cambia de turno
        int[] murosJ = {10};
        int[] murosT = {0};
        partida.iniciarPartida();

        when(jugador1Mock.hasMurosRestantes()).thenReturn(true);
        when(jugador1Mock.getMurosRestantes()).thenAnswer(inv -> murosJ[0]);
        doAnswer(inv -> {
            murosJ[0]--;
            return null;
        }).when(jugador1Mock).consumirMuro();

        when(tableroMock.esColocacionMuroValida(eq(muroPrueba))).thenReturn(true);
        when(tableroMock.getMurosColocados()).thenAnswer(inv -> {
            List<Muro> listaSimulada = new ArrayList<>();
            for (int i = 0; i < murosT[0]; i++) {
                listaSimulada.add(new Muro(new Posicion(0, i), Orientacion.HORIZONTAL));
            }
            return listaSimulada;
        });
        doAnswer(inv -> {
            murosT[0]++;
            return null;
        }).when(tableroMock).colocarMuro(any(Muro.class));

        colocado = partida.colocarMuro(muroPrueba);
        assertTrue(colocado, "Camino 5: El muro debería colocarse con éxito.");
        verify(jugador1Mock, times(1)).consumirMuro();
        verify(tableroMock, times(1)).colocarMuro(eq(muroPrueba));
        assertEquals("J2", partida.getJugadorActual().getNombre(),
                "Camino 5: El turno debe cambiar al siguiente jugador.");
    }

    /**
     * Prueba de Caja Blanca (Path Coverage — Caminos Independientes 1 a 4 en comprobarVictoria):
     * Cubre todos los caminos posibles del método:
     *  - Camino 1: lista de jugadores vacía (0 iteraciones) -> null.
     *  - Camino 2: J1 gana en la 1ª iteración -> marca terminada y retorna J1.
     *  - Camino 3: J1 no gana y J2 gana en la 2ª iteración -> marca terminada y retorna J2.
     *  - Camino 4: ningún jugador en meta tras recorrer toda la lista -> retorna null sin terminar.
     */
    @Test
    @DisplayName("Path Coverage en comprobarVictoria (4 caminos independientes con mocks)")
    void testComprobarVictoria() {
        // Camino 1 (Path Coverage): Lista vacía de jugadores -> bucle no ejecuta -> retorna null
        Partida partidaSinJugadores = new Partida(tableroMock, sorteadorMock);
        assertNull(partidaSinJugadores.comprobarVictoria(),
                "Camino 1: Con lista vacía de jugadores debe retornar null.");

        // Camino 2 (Path Coverage): Gana J1 en la 1ª iteración -> marca terminada y retorna J1
        partida.iniciarPartida();
        when(jugador1Mock.haLlegadoAMeta()).thenReturn(true);

        Jugador ganador = partida.comprobarVictoria();
        assertNotNull(ganador, "Camino 2: Debe haber un ganador.");
        assertEquals("J1", ganador.getNombre(), "Camino 2: El ganador debe ser J1 en la 1ª iteración.");
        assertTrue(partida.isTerminada(), "Camino 2: La partida debe marcarse como terminada tras la victoria.");

        // Camino 3 (Path Coverage): J1 no gana (1ª iteración) y gana J2 en la 2ª iteración -> retorna J2
        partida.iniciarPartida();
        when(jugador1Mock.haLlegadoAMeta()).thenReturn(false);
        when(jugador2Mock.haLlegadoAMeta()).thenReturn(true);

        ganador = partida.comprobarVictoria();
        assertNotNull(ganador, "Camino 3: Debe haber un ganador.");
        assertEquals("J2", ganador.getNombre(), "Camino 3: El ganador debe ser J2 en la 2ª iteración.");
        assertTrue(partida.isTerminada(), "Camino 3: La partida debe marcarse como terminada tras la victoria.");

        // Camino 4 (Path Coverage): Ningún jugador ha llegado a meta -> retorna null y sigue en curso
        partida.iniciarPartida();
        when(jugador1Mock.haLlegadoAMeta()).thenReturn(false);
        when(jugador2Mock.haLlegadoAMeta()).thenReturn(false);

        ganador = partida.comprobarVictoria();
        assertNull(ganador, "Camino 4: No debe haber ganador.");
        assertFalse(partida.isTerminada(), "Camino 4: La partida no debe terminar si nadie ha ganado.");
    }

    /**
     * Prueba de Caja Negra (Partición Equivalente) y Programación por Contrato (DbC):
     * Verifica todos los getters públicos de fachada de Partida en una partida real 2P (9x9, 10 muros)
     * y comprueba la postcondición de inmutabilidad de las colecciones retornadas.
     */
    @Test
    @DisplayName("Getters públicos de Partida: estado inicial, actualización tras muro e inmutabilidad")
    void testGettersPublicos() {
        // Partición válida: partida estándar 2P (9x9, 10 muros, sin sorteo)
        Partida partidaReal = new Partida();
        partidaReal.configurarPartida(2, 9, 10, false);
        partidaReal.iniciarPartida();

        assertFalse(partidaReal.isTerminada());
        assertEquals(2, partidaReal.getJugadores().size());
        assertNotNull(partidaReal.getTablero());
        assertEquals(new Posicion(4, 0), partidaReal.getPosicionesJugadores().get(Color.ROJO));
        assertEquals(new Posicion(4, 8), partidaReal.getPosicionesJugadores().get(Color.AZUL));
        assertTrue(partidaReal.getMurosColocados().isEmpty());
        assertEquals(10, partidaReal.getMurosRestantesPorJugador().get(Color.ROJO));
        assertEquals(10, partidaReal.getMurosRestantesPorJugador().get(Color.AZUL));

        // Colocar un muro actualiza la fachada de muros colocados y restantes
        assertTrue(partidaReal.colocarMuro(new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL)));
        assertEquals(1, partidaReal.getMurosColocados().size());
        assertEquals(9, partidaReal.getMurosRestantesPorJugador().get(Color.ROJO));
        assertEquals(10, partidaReal.getMurosRestantesPorJugador().get(Color.AZUL));

        // Contrato de inmutabilidad: ninguna colección expuesta puede modificarse externamente
        assertThrows(UnsupportedOperationException.class, () -> partidaReal.getJugadores().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> partidaReal.getPosicionesJugadores().put(Color.ROJO, new Posicion(0, 0)));
        assertThrows(UnsupportedOperationException.class, () -> partidaReal.getMurosColocados().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> partidaReal.getMurosRestantesPorJugador().put(Color.ROJO, 99));
    }

    /**
     * Prueba de Caja Negra (Pairwise Testing) y Data-Driven Testing (@CsvSource):
     * Ejecuta las 6 combinaciones que cubren el 100% de las parejas entre los factores
     * numJugadores={2,4}, tamanoTablero={7,9}, murosPorJugador={5,10} y sorteoAleatorio={true,false}.
     */
    @ParameterizedTest(name = "[{index}] jugadores={0}, tablero={1}, muros={2}, sorteoAleatorio={3}")
    @CsvSource({
        "2, 7, 5,  true",
        "2, 9, 10, false",
        "4, 7, 10, false",
        "4, 9, 5,  true",
        "2, 7, 10, true",
        "2, 9, 5,  false"
    })
    @DisplayName("Pairwise Testing: Configuración de partida (2/4 jugadores, tablero 7/9, muros 5/10, sorteo)")
    void testConfigurarPartidaPairwise(int numJugadores, int tamanoTablero,
            int murosPorJugador, boolean sorteoAleatorio) {
        Partida partidaReal = new Partida();
        partidaReal.configurarPartida(numJugadores, tamanoTablero, murosPorJugador, sorteoAleatorio);

        assertTrue(partidaReal.isTerminada(), "Antes de iniciarPartida la partida debe estar en pausa/terminada");
        assertEquals(numJugadores, partidaReal.getJugadores().size());
        assertEquals(tamanoTablero, partidaReal.getTablero().getTamanoTablero());
        assertTrue(partidaReal.getMurosColocados().isEmpty());

        int centro = tamanoTablero / 2;
        assertEquals(new Posicion(centro, 0), partidaReal.getPosicionesJugadores().get(Color.ROJO));
        assertEquals(new Posicion(centro, tamanoTablero - 1), partidaReal.getPosicionesJugadores().get(Color.AZUL));

        if (numJugadores == 4) {
            assertEquals(new Posicion(0, centro), partidaReal.getPosicionesJugadores().get(Color.VERDE));
            assertEquals(new Posicion(tamanoTablero - 1, centro),
                    partidaReal.getPosicionesJugadores().get(Color.AMARILLO));
        }

        for (Jugador j : partidaReal.getJugadores()) {
            assertEquals(murosPorJugador, j.getMurosRestantes());
            assertEquals(murosPorJugador, partidaReal.getMurosRestantesPorJugador().get(j.getColor()));
        }

        partidaReal.iniciarPartida();
        assertFalse(partidaReal.isTerminada());

        // Verificar turno inicial según política de sorteo
        if (sorteoAleatorio) {
            Jugador jugadorActual = partidaReal.getJugadorActual();
            assertNotNull(jugadorActual, "Con sorteo aleatorio debe haber un jugador actual asignado");
            assertTrue(partidaReal.getJugadores().contains(jugadorActual),
                    "El jugador actual sorteado debe pertenecer a la lista de jugadores de la partida");
        } else {
            assertEquals(Color.ROJO, partidaReal.getJugadorActual().getColor(),
                    "Sin sorteo aleatorio debe comenzar siempre el primer jugador (ROJO)");
        }
    }

    /**
     * Prueba de Caja Negra (Valores Frontera e Inválidos) y Programación por Contrato (DbC):
     * Valida el límite inferior aceptado de configurarPartida (tablero 5x5 y 0 muros) y el rechazo
     * mediante AssertionError de parámetros fuera de dominio (1/3 jugadores, tamaño 0/4/8, muros -1/21).
     */
    @Test
    @DisplayName("configurarPartida(int...): frontera inferior válida (5x5, 0 muros) y valores inválidos DbC")
    void testConfigurarPartidaCasosInvalidosYFrontera() {
        Partida partidaReal = new Partida();

        // Valor frontera inferior válido: 0 muros por jugador y tablero mínimo impar (5x5)
        partidaReal.configurarPartida(2, 5, 0, false);
        assertEquals(5, partidaReal.getTablero().getTamanoTablero());
        assertEquals(0, partidaReal.getMurosRestantesPorJugador().get(Color.ROJO));

        // Partición inválida: número de jugadores distinto de 2 o 4
        assertThrows(AssertionError.class, () -> partidaReal.configurarPartida(1, 9, 10, true));
        assertThrows(AssertionError.class, () -> partidaReal.configurarPartida(3, 9, 10, true));

        // Partición inválida: tamaño de tablero no positivo (0), menor que 5 (4) o par (8)
        assertThrows(AssertionError.class, () -> partidaReal.configurarPartida(2, 0, 10, true));
        assertThrows(AssertionError.class, () -> partidaReal.configurarPartida(2, 4, 10, true));
        assertThrows(AssertionError.class, () -> partidaReal.configurarPartida(2, 8, 10, true));

        // Partición inválida: muros negativos (-1) o superiores al máximo permitido (21)
        assertThrows(AssertionError.class, () -> partidaReal.configurarPartida(2, 9, -1, true));
        assertThrows(AssertionError.class, () -> partidaReal.configurarPartida(2, 9, 21, true));
    }

    /**
     * Prueba de Caja Negra (Partición Equivalente) y Caja Blanca (gestión de múltiples oponentes):
     * Comprueba la rotación cíclica completa entre 4 jugadores, el bloqueo de casillas ocupadas
     * por cualquier oponente y la selección del candidato de salto cuando hay varios peones adyacentes.
     */
    @Test
    @DisplayName("realizarMovimiento en 4 jugadores: rotación de turnos, ocupación y salto con múltiples vecinos")
    void testRealizarMovimientoCuatroJugadores() {
        // Partición válida: rotación cíclica de turnos ROJO -> AZUL -> VERDE -> AMARILLO -> ROJO
        Partida partida4P = new Partida();
        partida4P.configurarPartida(4, 9, 5, false);
        partida4P.iniciarPartida();

        assertEquals(Color.ROJO, partida4P.getJugadorActual().getColor());
        assertTrue(partida4P.realizarMovimiento(new Posicion(4, 1)));
        assertEquals(Color.AZUL, partida4P.getJugadorActual().getColor());
        assertTrue(partida4P.realizarMovimiento(new Posicion(4, 7)));
        assertEquals(Color.VERDE, partida4P.getJugadorActual().getColor());
        assertTrue(partida4P.realizarMovimiento(new Posicion(1, 4)));
        assertEquals(Color.AMARILLO, partida4P.getJugadorActual().getColor());
        assertTrue(partida4P.realizarMovimiento(new Posicion(7, 4)));
        assertEquals(Color.ROJO, partida4P.getJugadorActual().getColor());

        // Caja Blanca: múltiples oponentes adyacentes (J2 abajo y J3 a la derecha de J1)
        Jugador j1 = new Jugador("J1", Color.ROJO, new Posicion(4, 4), 8, 5);
        Jugador j2 = new Jugador("J2", Color.AZUL, new Posicion(4, 5), 0, 5);
        Jugador j3 = new Jugador("J3", Color.VERDE, new Posicion(5, 4), 8, 5);
        Jugador j4 = new Jugador("J4", Color.AMARILLO, new Posicion(8, 4), 0, 5);
        Partida partidaSaltoFija = new Partida(new Tablero(), new SorteadorTurnoFijoMock(0));
        partidaSaltoFija.configurarPartida(List.of(j1, j2, j3, j4));
        partidaSaltoFija.iniciarPartida();

        assertFalse(partidaSaltoFija.realizarMovimiento(new Posicion(4, 5)),
                "No debe poder moverse encima de J2");
        assertFalse(partidaSaltoFija.realizarMovimiento(new Posicion(5, 4)),
                "No debe poder moverse encima de J3");
        assertTrue(partidaSaltoFija.realizarMovimiento(new Posicion(4, 6)),
                "Debe poder saltar sobre J2 aunque J3 también sea adyacente");
    }
}
