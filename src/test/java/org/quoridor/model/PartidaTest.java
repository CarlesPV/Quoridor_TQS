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
        partida.iniciarPartida();
        
        assertFalse(partida.isTerminada(), "La partida no debe estar terminada al iniciar.");
        assertEquals("Jugador 1", partida.getJugadorActual().getNombre(), "Debe empezar el Jugador 1 según el sorteador fijo.");
    }

	@Test
	void testRealizarMovimiento() {
		fail("Not yet implemented");
	}

	@Test
	void testColocarMuro() {
		fail("Not yet implemented");
	}

	@Test
	void testComprobarVictoria() {
		fail("Not yet implemented");
	}

}
