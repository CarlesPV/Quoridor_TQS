package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
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

	// Prueba de Caja Blanca: Loop Testing (bucle simple de murosColocados: 0, 1, 2, m, n-1, n iteraciones)
	@Test
	void testColisionaConMurosExistentes() {
		// 1. Caso 0 iteraciones (tablero vacío): el bucle no se ejecuta
		Muro muroTest = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
		assertFalse(tablero.colisionaConMurosExistentes(muroTest),
				"0 iteraciones: Un tablero sin muros no debe detectar colisiones");

		// 2. Caso 1 iteración:
			// 1 iteración completa sin colisión
		tablero.getMurosColocados().add(new Muro(new Posicion(5, 5), Orientacion.VERTICAL));
		assertFalse(tablero.colisionaConMurosExistentes(muroTest),
				"1 iteración completa: No debe colisionar con un muro distante");

			// 1 iteración con salida temprana por colisión
		tablero.getMurosColocados().clear();
		tablero.getMurosColocados().add(new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL));
		assertTrue(tablero.colisionaConMurosExistentes(muroTest),
				"1 iteración con salida: Debe detectar colisión con el único muro");

		// 3. Caso 2 iteraciones:
			// 2 iteraciones completas sin colisión
		tablero.getMurosColocados().clear();
		tablero.getMurosColocados().add(new Muro(new Posicion(0, 0), Orientacion.VERTICAL));
		tablero.getMurosColocados().add(new Muro(new Posicion(5, 5), Orientacion.VERTICAL));
		assertFalse(tablero.colisionaConMurosExistentes(muroTest),
				"2 iteraciones completas: No debe colisionar tras evaluar 2 muros");

			// 2 iteraciones con salida temprana en el segundo muro
		tablero.getMurosColocados().clear();
		tablero.getMurosColocados().add(new Muro(new Posicion(0, 0), Orientacion.VERTICAL));
		tablero.getMurosColocados().add(new Muro(new Posicion(2, 2), Orientacion.VERTICAL));
		Muro muroCruz = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
		assertTrue(tablero.colisionaConMurosExistentes(muroCruz),
				"2 iteraciones con salida: Debe iterar el 1º y colisionar en el 2º");

		// 4. Caso m iteraciones típicas (m = 5 muros):
			// 5 iteraciones completas sin colisión
		tablero.getMurosColocados().clear();
		tablero.getMurosColocados().add(new Muro(new Posicion(0, 0), Orientacion.HORIZONTAL));
		tablero.getMurosColocados().add(new Muro(new Posicion(3, 0), Orientacion.HORIZONTAL));
		tablero.getMurosColocados().add(new Muro(new Posicion(0, 3), Orientacion.VERTICAL));
		tablero.getMurosColocados().add(new Muro(new Posicion(0, 5), Orientacion.VERTICAL));
		tablero.getMurosColocados().add(new Muro(new Posicion(6, 0), Orientacion.HORIZONTAL));
		Muro muroLibre = new Muro(new Posicion(6, 6), Orientacion.HORIZONTAL);
		assertFalse(tablero.colisionaConMurosExistentes(muroLibre),
				"m iteraciones completas: No colisiona tras recorrer m muros");

			// m iteraciones con salida en el m-ésimo muro (5º muro)
		Muro muroEnQuinto = new Muro(new Posicion(6, 0), Orientacion.VERTICAL);
		assertTrue(tablero.colisionaConMurosExistentes(muroEnQuinto),
				"m iteraciones con salida: Debe recorrer hasta el 5º muro y colisionar");

		// 5. Configuración de 20 muros no colisionantes para n-1 y n iteraciones:
			// 8 verticales en Y=0, 8 verticales en Y=2, 4 verticales en Y=4
		tablero.getMurosColocados().clear();
		for (int x = 0; x < 8; x++) {
			tablero.getMurosColocados().add(new Muro(new Posicion(x, 0), Orientacion.VERTICAL));
		}
		for (int x = 0; x < 8; x++) {
			tablero.getMurosColocados().add(new Muro(new Posicion(x, 2), Orientacion.VERTICAL));
		}
		for (int x = 0; x < 3; x++) {
			tablero.getMurosColocados().add(new Muro(new Posicion(x, 4), Orientacion.VERTICAL));
		}
		// Caso n-1 iteraciones (19 muros): 19 iteraciones completas sin colisión
		Muro muroEnPosicion20 = new Muro(new Posicion(3, 4), Orientacion.VERTICAL);
		assertFalse(tablero.colisionaConMurosExistentes(muroEnPosicion20),
				"n-1 iteraciones completas: 19 muros recorridos sin colisión");

			// Caso n iteraciones máximas (20 muros):
		tablero.getMurosColocados().add(muroEnPosicion20);

			// 20 iteraciones completas sin colisión
		Muro muroDisjunto = new Muro(new Posicion(6, 6), Orientacion.HORIZONTAL);
		assertFalse(tablero.colisionaConMurosExistentes(muroDisjunto),
				"n iteraciones completas: 20 muros evaluados sin colisión");

			// 20 iteraciones con colisión en el último muro (el número 20)
		Muro muroCruzUltimo = new Muro(new Posicion(3, 4), Orientacion.HORIZONTAL);
		assertTrue(tablero.colisionaConMurosExistentes(muroCruzUltimo),
				"n iteraciones con salida: Debe recorrer los 20 muros y colisionar en el último");
	}

	// Prueba de Caja Blanca: Decision/Condition Coverage y Mock de IValidadorCaminos
	@Test
	void testEsColocacionMuroValida() {
		Muro muroValido = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
		Muro muroFueraLimites = new Muro(new Posicion(8, 8), Orientacion.HORIZONTAL);

		// Muro dentro de límites en tablero vacío
		assertTrue(tablero.esColocacionMuroValida(muroValido),
				"Debería ser válida la colocación de un muro dentro de los límites en tablero vacío");

		// Muro con coordenadas fuera de límites
		assertFalse(tablero.esColocacionMuroValida(muroFueraLimites),
				"No debería ser válida la colocación de un muro fuera de los límites");

		// Muro que colisiona con uno ya colocado
		tablero.getMurosColocados().add(muroValido);
		assertFalse(tablero.esColocacionMuroValida(muroValido),
				"No debería ser válida la colocación de un muro que colisiona con otro existente");

		// Decision & Condition Coverage: límites frontera válidos (0,0), (7,7) vertical y horizontal
		tablero.getMurosColocados().clear();
		assertTrue(tablero.esColocacionMuroValida(new Muro(new Posicion(0, 0), Orientacion.VERTICAL)),
				"Límite superior izquierdo vertical debe ser válido");
		assertTrue(tablero.esColocacionMuroValida(new Muro(new Posicion(7, 7), Orientacion.VERTICAL)),
				"Límite inferior derecho vertical debe ser válido");
		assertTrue(tablero.esColocacionMuroValida(new Muro(new Posicion(0, 7), Orientacion.HORIZONTAL)),
				"Frontera esquina inferior izquierda horizontal debe ser válida");
		assertTrue(tablero.esColocacionMuroValida(new Muro(new Posicion(7, 0), Orientacion.HORIZONTAL)),
				"Frontera esquina superior derecha horizontal debe ser válida");

		// Decision Coverage: Límite máximo de 20 muros colocados
		for (int x = 0; x < 8; x++) {
			tablero.getMurosColocados().add(new Muro(new Posicion(x, 0), Orientacion.VERTICAL));
			tablero.getMurosColocados().add(new Muro(new Posicion(x, 2), Orientacion.VERTICAL));
		}
		for (int x = 0; x < 4; x++) {
			tablero.getMurosColocados().add(new Muro(new Posicion(x, 4), Orientacion.VERTICAL));
		}
		assertEquals(Tablero.MAX_MUROS, tablero.getMurosColocados().size());
		assertFalse(tablero.esColocacionMuroValida(new Muro(new Posicion(6, 6), Orientacion.HORIZONTAL)),
				"No debe permitir colocar muros si ya se ha alcanzado el límite máximo de 20");

		// Validación de caminos aislando con Mock Manual de IValidadorCaminos (Mock 1 de rúbrica)
		MockValidadorCaminos mockValidador = new MockValidadorCaminos();
		Tablero tableroConMock = new Tablero(mockValidador);
		tableroConMock.getPosicionesJugadores().put(Color.ROJO, new Posicion(4, 0));
		tableroConMock.getPosicionesJugadores().put(Color.AZUL, new Posicion(4, 8));

		// Caso validador retorna true: camino disponible para todos
		mockValidador.setCaminoDisponible(true);
		assertTrue(tableroConMock.esColocacionMuroValida(muroValido),
				"Debe permitir colocar muro si el validador confirma camino disponible");

		// Caso validador retorna false: el muro encierra a algún jugador
		mockValidador.setCaminoDisponible(false);
		assertFalse(tableroConMock.esColocacionMuroValida(muroValido),
				"No debe permitir colocar muro si el validador indica que bloquea el camino");
	}

	// Mock Manual de IValidadorCaminos
	private static class MockValidadorCaminos implements IValidadorCaminos {
		private boolean caminoDisponible = true;

		public void setCaminoDisponible(boolean caminoDisponible) {
			this.caminoDisponible = caminoDisponible;
		}

		@Override
		public boolean hayCaminoDisponible(Posicion inicio, int filaObjetivo,
				List<Muro> muros, int tamanoTablero) {
			return this.caminoDisponible;
		}

		@Override
		public int calcularDistanciaMinima(Posicion inicio, int filaObjetivo,
				List<Muro> muros, int tamanoTablero) {
			return this.caminoDisponible ? 5 : -1;
		}
	}

	@Test
	void testColocarMuro() {
		Muro muro1 = new Muro(new Posicion(2, 2), Orientacion.HORIZONTAL);
		Muro muro2 = new Muro(new Posicion(5, 5), Orientacion.VERTICAL);

		tablero.colocarMuro(muro1);
		assertEquals(1, tablero.getMurosColocados().size(),
				"El número de muros debe ser 1 tras colocar el primer muro");
		assertTrue(tablero.getMurosColocados().contains(muro1),
				"La lista de muros debe contener el primer muro colocado");

		tablero.colocarMuro(muro2);
		assertEquals(2, tablero.getMurosColocados().size(),
				"El número de muros debe ser 2 tras colocar el segundo muro");
		assertTrue(tablero.getMurosColocados().contains(muro2),
				"La lista de muros debe contener el segundo muro colocado");
	}

	@Test
	void testHayCaminoParaTodos() {
		fail("Not yet implemented");
	}

}
