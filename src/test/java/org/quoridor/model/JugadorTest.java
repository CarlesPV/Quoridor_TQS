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
	
	// Prueba de Caja Negra: Partición Equivalente y valores límite / frontera válidos
	@Test
	void testConstructorConParametros() {
		Posicion pos = new Posicion(4, 0);
		jugador = new Jugador("Jugador 1", Color.ROJO, pos, 8, 10);
		
		assertEquals("Jugador 1", jugador.getNombre());
		assertEquals(Color.ROJO, jugador.getColor());
		assertEquals(pos, jugador.getPosicionActual());
		assertEquals(8, jugador.getFilaObjetivo());
		assertEquals(10, jugador.getMurosRestantes());

		// Límite inferior de fila objetivo (0) con jugador que empieza en fila 8
		Posicion posFila8 = new Posicion(4, 8);
		Jugador jugador2 = new Jugador("Jugador 2", Color.AZUL, posFila8, 0, 10);
		assertEquals(0, jugador2.getFilaObjetivo());
		assertEquals(posFila8, jugador2.getPosicionActual());
		
		// Límite inferior válido de muros (0 muros disponibles)
		Jugador jugadorSinMuros = new Jugador("Sin Muros", Color.VERDE, new Posicion(0, 4), 8, 0);
		assertEquals(0, jugadorSinMuros.getMurosRestantes());
		
		// Partición para 4 jugadores (5 muros iniciales por jugador)
		Jugador jugador4P = new Jugador("Jugador 4", Color.AMARILLO, new Posicion(8, 4), 0, 5);
		assertEquals(5, jugador4P.getMurosRestantes());
		assertEquals(Color.AMARILLO, jugador4P.getColor());
		
		// Valores frontera en esquinas del tablero (0,0) y (8,8)
		Jugador jugadorEsquina = new Jugador("Esquina", Color.ROJO, new Posicion(0, 0), 8, 10);
		assertEquals(new Posicion(0, 0), jugadorEsquina.getPosicionActual());
		Jugador jugadorEsquinaMax = new Jugador("EsquinaMax", Color.AZUL, new Posicion(8, 8), 0, 10);
		assertEquals(new Posicion(8, 8), jugadorEsquinaMax.getPosicionActual());
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
