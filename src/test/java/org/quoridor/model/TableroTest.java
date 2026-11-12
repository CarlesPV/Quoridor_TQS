package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TableroTest {

	private Tablero tablero;

	@BeforeEach
	void setUp() throws Exception {
		tablero = new Tablero();
	}

	///
	/// Tests de constructores
	///
	@Test
	void testConstructor() {
		assertNotNull(tablero.getMurosColocados(), "La lista de muros no debe ser null");
		assertTrue(tablero.getMurosColocados().isEmpty(), "El tablero debe comenzar sin muros colocados");
		assertNotNull(tablero.getPosicionesJugadores(), "El mapa de posiciones no debe ser null");
		assertTrue(tablero.getPosicionesJugadores().isEmpty(), "El mapa de posiciones debe iniciar vacío");
		assertNotNull(tablero.getValidadorCaminos(), "El validador de caminos debe inicializarse por defecto");

		// Valores constantes del tablero
		assertEquals(9, Tablero.TAMANO_TABLERO);
		assertEquals(20, Tablero.MAX_MUROS);
	}

	// Prueba de Caja Negra: Partición Equivalente y valores límite / frontera válidos
	@Test
	void testConstructorConParametros() {
		IValidadorCaminos mockValidador = new ValidadorCaminos();
		Tablero tableroInyectado = new Tablero(mockValidador);
		assertNotNull(tableroInyectado.getMurosColocados());
		assertTrue(tableroInyectado.getMurosColocados().isEmpty());
		assertEquals(mockValidador, tableroInyectado.getValidadorCaminos(), "Debe usar el validador inyectado");

		// Prueba de Caja Negra: Partición inválida de precondición (validador nulo)
		assertThrows(AssertionError.class, () -> new Tablero(null),
				"Debe lanzar AssertionError si el validador inyectado es nulo");
	}

	///
	/// Tests de funciones
	///
	@Test
	void testEsMovimientoValido() {
		fail("Not yet implemented");
	}

	@Test
	void testColisionaConMurosExistentes() {
		fail("Not yet implemented");
	}

	@Test
	void testEsColocacionMuroValida() {
		fail("Not yet implemented");
	}

	@Test
	void testColocarMuro() {
		fail("Not yet implemented");
	}

	@Test
	void testHayCaminoParaTodos() {
		fail("Not yet implemented");
	}

}
