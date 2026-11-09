package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;
class PosicionTest {
	Posicion p1, p2;
	
	@BeforeEach
	void setUp() throws Exception {
		p1 = new Posicion(); // (0,0)
		p2 = new Posicion(); // (0,0)
	}
	
	// Se mira que la posicion sea valida y no este fuera del tablero de juego
	// Mirar primero casos frontera true y luego todos los falsos en entorno normal 9x9
	// Finalmente mirar si cambiar el límite afecta las posiciones que antes aceptaba
	@Test
	void testEsValida() {
		assertTrue(p1.esValida(9));
		
		p1 = new Posicion(8,8);
		assertTrue(p1.esValida(9));
		
		p1 = new Posicion(-1,0);
		assertFalse(p1.esValida(9));
		
		p1 = new Posicion(0,-1);
		assertFalse(p1.esValida(9));
		
		p1 = new Posicion(9,0);
		assertFalse(p1.esValida(9));
		
		p1 = new Posicion(0,9);
		assertFalse(p1.esValida(9));
		
		p1 = new Posicion(8,8);
		assertFalse(p1.esValida(8));
		
	}

	@Test
	void testDistanciaManhattan() {
		p2 = new Posicion(5,5);
		assertEquals(10, p1.distanciaManhattan(p2));
	}
	
	@Test
	void testEsAdyacente() {
		fail("Not yet implemented");
	}

	@Test
	void testDesplazar() {
		fail("Not yet implemented");
	}
	
	@Test
	void testEqualsPosicion() {
		fail("Not yet implemented");
	}

	@Test
	void testHashCode() {
		fail("Not yet implemented");
	}

}
