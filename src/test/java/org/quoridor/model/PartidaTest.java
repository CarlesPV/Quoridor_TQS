package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Mock manual para controlar exactamente quién empieza en los tests
class SorteadorTurnoFijoMock implements ISorteadorTurno {
	private int indiceFijo;
	
	public SorteadorTurnoFijoMock(int indiceFijo) 
	{
		this.indiceFijo = indiceFijo;
	}
	@Override
	public int sortear(List<Jugador> jugadores) 
	{
		return indiceFijo; // Devuelve siempre el índice preconfigurado
 	}
}

class PartidaTest {

	private Partida partida;
    private ISorteadorTurno sorteadorMock; // Mock manual
    private Tablero tableroMock; // Mockito 
    // Declaramos los mocks mockito para los jugadores
    private Jugador jugador1Mock;
    private Jugador jugador2Mock;
	
	@BeforeEach
	void setUp() throws Exception {
		// Sorteador fijo para que siempre empiece el Jugador 1 (índice 0)
        sorteadorMock = new SorteadorTurnoFijoMock(0);
        
        // Creamos los Mocks con Mockito
        tableroMock = mock(Tablero.class);
        jugador1Mock = mock(Jugador.class);
        jugador2Mock = mock(Jugador.class);
        
        // Configuramos comportamientos básicos si es necesario para los mocks
        when(jugador1Mock.getColor()).thenReturn(Color.ROJO);
        when(jugador2Mock.getColor()).thenReturn(Color.AZUL);
        when(jugador1Mock.getNombre()).thenReturn("J1");
        when(jugador2Mock.getNombre()).thenReturn("J2");
        when(jugador1Mock.getPosicionActual()).thenReturn(new Posicion(4,0));
        when(jugador2Mock.getPosicionActual()).thenReturn(new Posicion(4,8));

        // Instanciamos el objeto Partida
        partida = new Partida(tableroMock, sorteadorMock);
        
        // Configuramos el objeto Partida con los jugadores mock
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
	}

	@Test
	void testIniciarPartida() {
		// Caso J1 empieza
        partida.iniciarPartida();
        assertFalse(partida.isTerminada(), "La partida no debe estar terminada al iniciar.");
        assertEquals("J1", partida.getJugadorActual().getNombre(), "Debe empezar el Jugador 1 según el sorteador fijo.");
        
        // Caso J2 empieza
        sorteadorMock = new SorteadorTurnoFijoMock(1);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        partida.iniciarPartida();
        assertFalse(partida.isTerminada(), "La partida no debe estar terminada al iniciar.");
        assertEquals("J2", partida.getJugadorActual().getNombre(), "Debe empezar el Jugador 2 según el sorteador fijo.");
        
        // Caso inicializar sorteador fuera de rango superior
        sorteadorMock = new SorteadorTurnoFijoMock(2);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        try 
        {
        	partida.iniciarPartida();
        	assertTrue(false);
        }
        catch (AssertionError e) {}
        
        // Caso inicializar sorteador fuera de rango inferior
        sorteadorMock = new SorteadorTurnoFijoMock(-1);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        try 
        {
        	partida.iniciarPartida();
        	assertTrue(false);
        }
        catch (AssertionError e) {}
        
        
    }

	@Test
	void testRealizarMovimiento() {
		// Caso J1 se mueve y cambia de turno
        partida.iniciarPartida();
        Posicion destinoValido = new Posicion(4, 1);
        // Simulamos que el Tablero aprueba el movimiento
        when(tableroMock.esMovimientoValido(any(Posicion.class), eq(destinoValido), any(Posicion.class)))
            .thenReturn(true);
        
        boolean resultado = partida.realizarMovimiento(destinoValido);
        
        assertTrue(resultado, "El movimiento debería realizarse con éxito.");
        // Verificamos que el turno avanzó automáticamente a J2
        assertEquals("J2", partida.getJugadorActual().getNombre(), "El turno debe cambiar al siguiente jugador.");
        
        
        // Caso J2 se mueve y cambia de turno
        sorteadorMock = new SorteadorTurnoFijoMock(1);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        partida.iniciarPartida();
        
        destinoValido = new Posicion(4, 7);
        // Simulamos que el Tablero aprueba el movimiento
        when(tableroMock.esMovimientoValido(any(Posicion.class), eq(destinoValido), any(Posicion.class)))
            .thenReturn(true);
        
        resultado = partida.realizarMovimiento(destinoValido);
        
        assertTrue(resultado, "El movimiento debería realizarse con éxito.");
        // Verificamos que el turno avanzó automáticamente a J1
        assertEquals("J1", partida.getJugadorActual().getNombre(), "El turno debe cambiar al siguiente jugador.");
        
        
        // Caso movimiento invalido
        sorteadorMock = new SorteadorTurnoFijoMock(0);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        partida.iniciarPartida(); // Turno actual: Jugador 1
        Posicion destinoInvalido = new Posicion(0, 0); // Lejos o bloqueado

        // Simulamos que el Tablero rechaza el movimiento
        when(tableroMock.esMovimientoValido(any(Posicion.class), eq(destinoInvalido), any(Posicion.class)))
            .thenReturn(false);

        resultado = partida.realizarMovimiento(destinoInvalido);
        assertFalse(resultado, "El movimiento inválido debe ser rechazado.");
        // Verificamos que el turno NO avanzó (sigue siendo el Jugador 1)
        assertEquals("J1", partida.getJugadorActual().getNombre(), "El turno no debe cambiar si el movimiento falla.");
        
        
        // Caso se llega a la victoria
        partida.iniciarPartida(); // Turno del Jugador 1
        // Para el Jugador 1 (abajo), su fila objetivo (meta) es 8.
        Posicion destinoMeta = new Posicion(3, 8);
        
        // Simulamos que el Tablero aprueba el movimiento
        when(tableroMock.esMovimientoValido(any(Posicion.class), eq(destinoMeta), any(Posicion.class)))
            .thenReturn(true);
        
        // Simulamos que jugador 1 llega a la meta
        when(jugador1Mock.haLlegadoAMeta()).thenReturn(true);
        resultado = partida.realizarMovimiento(destinoMeta);

        assertTrue(resultado);
        // Verificamos que la partida se marcó como terminada al llegar a la meta
        assertTrue(partida.isTerminada(), "La partida debería terminar cuando el jugador alcanza su fila objetivo.");
        
        
        // Caso destino null
        partida.iniciarPartida();
        
        try
        {
        	partida.realizarMovimiento(null);
        	assertTrue(false);
        }
        catch (AssertionError e) {}
        
        
        // Caso partida sin inicializar o ya terminada
        sorteadorMock = new SorteadorTurnoFijoMock(0);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        Posicion destinoCualquiera = new Posicion(4, 7);
        resultado = partida.realizarMovimiento(destinoCualquiera);

        assertFalse(resultado, "No se puede mover si la partida está terminada.");
	}

	@Test
	void testColocarMuro() {
		int[] murosJ = {10};
		int[] murosT = {0};
		
		// Caso jugador 1 coloca muro
		partida.iniciarPartida(); // Turno del Jugador 1

        Muro muroPrueba = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
        
        // El jugador mock tiene muros disponibles
        when(jugador1Mock.hasMurosRestantes()).thenReturn(true);
        // Se tiene que decrementar en uno cada vez que se consume un muro
        when(jugador1Mock.getMurosRestantes()).thenAnswer(inv -> murosJ[0]);
        doAnswer(inv -> {
            murosJ[0]--;
            return null;
        }).when(jugador1Mock).consumirMuro();
        
        // El tablero acepta el muro
        when(tableroMock.esColocacionMuroValida(eq(muroPrueba))).thenReturn(true);
        // Mockeamos getMurosColocados() para que devuelva una lista 
        // cuyo tamaño dependa de nuestra variable 'murosT[0]'
        when(tableroMock.getMurosColocados()).thenAnswer(inv -> {
            List<Muro> listaSimulada = new ArrayList<>();
            // Añadimos tantos elementos falsos como indique murosT[0]
            for (int i = 0; i < murosT[0]; i++) 
            {
                listaSimulada.add(new Muro(new Posicion(0, i), Orientacion.HORIZONTAL));
            }
            return listaSimulada;
        });

        // Cuando se ejecute tableroMock.colocarMuro(...), incrementamos el contador
        doAnswer(inv -> {
            murosT[0]++;
            return null;
        }).when(tableroMock).colocarMuro(any(Muro.class));
        
        
        boolean colocado = partida.colocarMuro(muroPrueba);
        assertTrue(colocado, "El muro debería colocarse con éxito.");
        
        // Verificamos que se ejecutó el método consumirMuro() en el mock del jugador
        verify(jugador1Mock, times(1)).consumirMuro();
        
        // Verificamos que se colocó en el tablero y cambió el turno
        verify(tableroMock, times(1)).colocarMuro(eq(muroPrueba));
        assertEquals("J2", partida.getJugadorActual().getNombre(), "El turno debe cambiar al siguiente jugador.");
        
        
        // Caso jugador 1 sin muros
        partida.iniciarPartida();

        // Configuramos el mock para que devuelva false en muros restantes
        when(jugador1Mock.hasMurosRestantes()).thenReturn(false);

        muroPrueba = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);

        colocado = partida.colocarMuro(muroPrueba);

        assertFalse(colocado, "No se puede colocar muro si el jugador no tiene.");
        
        // Verificamos que nunca se intentó consumir muro ni validar en el tablero
        // times(1) ya que viene del anterior test
        verify(jugador1Mock, times(1)).consumirMuro();
        verify(tableroMock, times(1)).esColocacionMuroValida(any());
        
        
        // Caso muro no válido 
        partida.iniciarPartida();
        
        // Configuramos el mock para que devuelva false en muros restantes
        when(tableroMock.esColocacionMuroValida(any(Muro.class))).thenReturn(false);

        muroPrueba = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
        
        colocado = partida.colocarMuro(muroPrueba);
        
        assertFalse(colocado, "Posicion del muro inválido.");
        // Verificamos que nunca se intentó consumir muro
        verify(jugador1Mock, times(1)).consumirMuro();
        
        // Caso partida sin inicializar o ya terminada
        sorteadorMock = new SorteadorTurnoFijoMock(0);
        partida = new Partida(tableroMock, sorteadorMock);
        partida.configurarPartida(List.of(jugador1Mock, jugador2Mock));
        muroPrueba = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
        colocado = partida.colocarMuro(muroPrueba);

        assertFalse(colocado, "No se puede colocar muros si la partida está terminada.");
        
        
        // Caso muro null
        partida.iniciarPartida();
        
        try
        {
        	partida.colocarMuro(null);
        	assertTrue(false);
        }
        catch (AssertionError e) {}
	}

	@Test
	void testComprobarVictoria() {
		// Caso jugador 1 gana
		partida.iniciarPartida();
		
        // Simulamos que al mover a la meta, el jugador mock cumple la condición de victoria
        when(jugador1Mock.haLlegadoAMeta()).thenReturn(true);
        
        // Comprobamos que comprobarVictoria() identifica correctamente al ganador
        Jugador ganador = partida.comprobarVictoria();
        assertNotNull(ganador, "Debe haber un ganador.");
        assertEquals("J1", ganador.getNombre(), "El ganador debe ser el Jugador 1.");
        
        
        // Caso seguir partida 
        partida.iniciarPartida();
        // Simulamos que al mover, el jugador mock no cumple la condición de victoria
        when(jugador1Mock.haLlegadoAMeta()).thenReturn(false);
        
        // Comprobamos que comprobarVictoria() devuelve null al no haber ganador
        ganador = partida.comprobarVictoria();
        assertNull(ganador, "No debe de haber ganador.");
	}

}
