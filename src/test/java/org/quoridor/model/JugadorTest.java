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

	// Prueba de Caja Negra: Partición Equivalente y valores límite / frontera válidos
	@Test
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

		// Valores límite y frontera válidos: esquinas del tablero (0,0), (8,8), (0,8), (8,0)
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

		// Valores frontera en bordes del tablero
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

		// Movimiento a la misma posición
		jugador.moverA(bordeDerecho);
		assertEquals(bordeDerecho, jugador.getPosicionActual());
	}
	
	// Prueba de Caja Negra: Partición Equivalente y valores límite / frontera válidos
	@Test
	void testConsumirMuro() {
		jugador = new Jugador("Jugador 1", Color.ROJO, new Posicion(4, 0), 8, 10);
		jugador.consumirMuro();
		assertEquals(9, jugador.getMurosRestantes());

		// Consumos consecutivos en partición estándar (10 muros)
		jugador.consumirMuro();
		assertEquals(8, jugador.getMurosRestantes());
		jugador.consumirMuro();
		assertEquals(7, jugador.getMurosRestantes());

		// Partición para 4 jugadores (5 muros iniciales por jugador)
		Jugador jugador4P = new Jugador("Jugador 4", Color.AMARILLO, new Posicion(8, 4), 0, 5);
		jugador4P.consumirMuro();
		assertEquals(4, jugador4P.getMurosRestantes());

		// Valor límite inferior válido: agotar todos los muros hasta quedar en 0
		jugador4P.consumirMuro(); // quedan 3
		jugador4P.consumirMuro(); // quedan 2
		jugador4P.consumirMuro(); // queda 1
		jugador4P.consumirMuro(); // quedan 0 (límite inferior)
		assertEquals(0, jugador4P.getMurosRestantes());
	}
	
	// Prueba de Caja Negra: Partición Equivalente y valores límite / frontera válidos
	@Test
	void testHasMurosRestantes() {
		jugador = new Jugador("Jugador 1", Color.ROJO, new Posicion(4, 0), 8, 10);
		assertTrue(jugador.hasMurosRestantes());

		// Partición para 4 jugadores (5 muros iniciales por jugador)
		Jugador jugador4P = new Jugador("Jugador 4", Color.AMARILLO, new Posicion(8, 4), 0, 5);
		assertTrue(jugador4P.hasMurosRestantes());

		// Valor frontera: exactamente 1 muro restante
		Jugador jugador1Muro = new Jugador("1 Muro", Color.AZUL, new Posicion(4, 8), 0, 1);
		assertTrue(jugador1Muro.hasMurosRestantes());

		// Transición de estado: consumir el último muro y verificar límite inferior (0 muros)
		jugador1Muro.consumirMuro();
		assertFalse(jugador1Muro.hasMurosRestantes());

		// Partición inicial con 0 muros disponibles
		Jugador jugadorSinMuros = new Jugador("Sin Muros", Color.VERDE, new Posicion(0, 4), 8, 0);
		assertFalse(jugadorSinMuros.hasMurosRestantes());
	}
	
	// Prueba de Caja Negra: Partición Equivalente y valores límite / frontera válidos
	@Test
	void testHaLlegadoAMeta() {
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

		// Valores límite: esquinas sobre la fila meta 8 (0,8) y (8,8)
		jugadorLejos.moverA(new Posicion(0, 8));
		assertTrue(jugadorLejos.haLlegadoAMeta());
		jugadorLejos.moverA(new Posicion(8, 8));
		assertTrue(jugadorLejos.haLlegadoAMeta());

		// Valor frontera y límites para meta 0 (fila 1 no es meta; (0,0) y (8,0) sí lo son)
		Jugador jugadorMeta0Aux = new Jugador("Meta0", Color.AZUL, new Posicion(4, 1), 0, 10);
		assertFalse(jugadorMeta0Aux.haLlegadoAMeta());
		jugadorMeta0Aux.moverA(new Posicion(0, 0));
		assertTrue(jugadorMeta0Aux.haLlegadoAMeta());
		jugadorMeta0Aux.moverA(new Posicion(8, 0));
		assertTrue(jugadorMeta0Aux.haLlegadoAMeta());
	}
	
	@Test
	void testReiniciarEstado() {
		fail("Not yet implemented");
	}

}
