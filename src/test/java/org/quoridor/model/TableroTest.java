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
	// Prueba de Caja Negra: Particiones equivalentes, valores límite, salto y muros bloqueantes
	@Test
	void testEsMovimientoValido() {
		Posicion origen = new Posicion(4, 4);
		Posicion destinoValido = new Posicion(4, 5);
		Posicion destinoLejano = new Posicion(6, 6);
		Posicion mismoDestino = new Posicion(4, 4);

		// Movimiento ortogonal simple adyacente sin muros ni oponente
		assertTrue(tablero.esMovimientoValido(origen, destinoValido, null),
				"Debería ser válido un movimiento a una casilla adyacente sin obstáculos");

		// Movimiento no adyacente
		assertFalse(tablero.esMovimientoValido(origen, destinoLejano, null),
				"No debería ser válido moverse a una casilla lejana sin salto");

		// Movimiento a la misma casilla
		assertFalse(tablero.esMovimientoValido(origen, mismoDestino, null),
				"No debería ser válido moverse a la misma casilla de origen");

		// Valores límite: origen o destino fuera del tablero
		Posicion origenBorde = new Posicion(0, 0);
		Posicion destinoFueraIzquierda = new Posicion(-1, 0);
		Posicion destinoFueraArriba = new Posicion(0, -1);
		assertFalse(tablero.esMovimientoValido(origenBorde, destinoFueraIzquierda, null),
				"No debería ser válido un movimiento fuera del límite izquierdo");
		assertFalse(tablero.esMovimientoValido(origenBorde, destinoFueraArriba, null),
				"No debería ser válido un movimiento fuera del límite superior");
		assertFalse(tablero.esMovimientoValido(new Posicion(-1, 4), new Posicion(0, 4), null),
				"No debería ser válido si el origen está fuera del tablero");

		// Destino ocupado por oponente
		Posicion oponente = new Posicion(4, 5);
		assertFalse(tablero.esMovimientoValido(origen, oponente, oponente),
				"No debería ser válido moverse directamente a la casilla ocupada por el oponente");

		// Salto recto válido sobre oponente
		Posicion destinoSaltoRecto = new Posicion(4, 6);
		assertTrue(tablero.esMovimientoValido(origen, destinoSaltoRecto, oponente),
				"Debería permitir saltar en línea recta sobre el oponente si no hay muros");

		// Salto diagonal no permitido si el camino recto está libre
		Posicion destinoDiagonal = new Posicion(5, 5);
		assertFalse(tablero.esMovimientoValido(origen, destinoDiagonal, oponente),
				"No debería permitir salto diagonal si no hay muro detrás del oponente");

		// Movimiento bloqueado por muro horizontal
		tablero.getMurosColocados().add(new Muro(new Posicion(4, 4), Orientacion.HORIZONTAL));
		assertFalse(tablero.esMovimientoValido(origen, destinoValido, null),
				"No debería permitir movimiento si un muro horizontal bloquea el paso");

		// Movimiento adyacente lateral no bloqueado por muro horizontal
		Posicion destinoLateral = new Posicion(5, 4);
		assertTrue(tablero.esMovimientoValido(origen, destinoLateral, null),
				"Debería permitir movimiento horizontal que no esté bloqueado");

		// Salto bloqueado por muro entre oponente y casilla de salto
		tablero.getMurosColocados().clear();
		tablero.getMurosColocados().add(new Muro(new Posicion(4, 5), Orientacion.HORIZONTAL));
		assertFalse(tablero.esMovimientoValido(origen, destinoSaltoRecto, oponente),
				"No debería permitir salto sobre oponente si hay un muro detrás");
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
