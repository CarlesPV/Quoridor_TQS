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
		// Caso jugador 1 coloca muro
		partida.iniciarPartida(); // Turno del Jugador 1

        Muro muroPrueba = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
        
        // El jugador mock tiene muros disponibles
        when(jugador1Mock.hasMurosRestantes()).thenReturn(true);
        when(jugador1Mock.getMurosRestantes()).thenReturn(10);
        // El tablero acepta el muro
        when(tableroMock.esColocacionMuroValida(eq(muroPrueba))).thenReturn(true);
        
        boolean colocado = partida.colocarMuro(muroPrueba);
        assertTrue(colocado, "El muro debería colocarse con éxito.");
        
        // Verificamos que se ejecutó el método consumirMuro() en el mock del jugador
        verify(jugador1Mock, times(1)).consumirMuro();
        
        // Verificamos que se colocó en el tablero y cambió el turno
        verify(tableroMock, times(1)).colocarMuro(eq(muroPrueba));
        assertEquals("J2", partida.getJugadorActual().getNombre(), "El turno debe cambiar al siguiente jugador.");
	}

	@Test
	void testComprobarVictoria() {
		fail("Not yet implemented");
	}

}
