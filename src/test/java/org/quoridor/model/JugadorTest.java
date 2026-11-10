package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JugadorTest {

	private Jugador jugador;

	@BeforeEach
	void setUp() throws Exception {
		jugador = new Jugador();
	}

	///
	/// Tests de constructores
	///
	@Test
	void testConstructor() {
		assertNull(jugador.getNombre());
		assertNull(jugador.getColor());
		assertNull(jugador.getPosicionActual());
		assertEquals(0, jugador.getFilaObjetivo());
		assertEquals(0, jugador.getMurosRestantes());
	}
	
	@Test
	void testConstructorConParametros() {
		Posicion pos = new Posicion(4, 0);
		jugador = new Jugador("Jugador 1", Color.ROJO, pos, 8, 10);
		
		assertEquals("Jugador 1", jugador.getNombre());
		assertEquals(Color.ROJO, jugador.getColor());
		assertEquals(pos, jugador.getPosicionActual());
		assertEquals(8, jugador.getFilaObjetivo());
		assertEquals(10, jugador.getMurosRestantes());
	}
	
	///
	/// Tests de funciones
	///
	@Test
	void testMoverA() {
		fail("Not yet implemented");
	}
	
	@Test
	void testConsumirMuro() {
		fail("Not yet implemented");
	}
	
	@Test
	void testHasMurosRestantes() {
		fail("Not yet implemented");
	}
	
	@Test
	void testHaLlegadoAMeta() {
		fail("Not yet implemented");
	}
	
	@Test
	void testReiniciarEstado() {
		fail("Not yet implemented");
	}

}
