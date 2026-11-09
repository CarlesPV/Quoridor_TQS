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
	
	@Test
	void testEsValida() {
		assertTrue(p1.esValida(9));
	}

	@Test
	void testDistanciaManhattan() {
		fail("Not yet implemented");
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
