package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

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

		assertFalse(new Posicion(0, 0).esValida(0));
		assertFalse(new Posicion(0, 0).esValida(-1));
		
	}

	// Se calcula la distancia Manhattan entre dos posiciones
	// Miarar primero dos posiciones en ambos ordenes 
	// y luego se cambia p1 sin cambiar p2 para ver que sigue siendo correcto el calculo
	// además se prueba la assercion
	@Test
	void testDistanciaManhattan() {
		p2 = new Posicion(5,5);
		assertEquals(10, p1.distanciaManhattan(p2));
		
		assertEquals(10, p2.distanciaManhattan(p1));
		
		p1 = new Posicion(-5,-5);
		assertEquals(20, p1.distanciaManhattan(p2));
		
		try
		{
			assertEquals(10, p1.distanciaManhattan(null));
			assertTrue(false);
		}
		catch (AssertionError e) {}
	}
	
	// Se mira que 2 posiciones sean adyacentes
	// Mirar todos los casos de adyacencia (derecha, izquierda, arriba, abajo)
	// y despues los que no son
	@Test
	void testEsAdyacente() {
		p2 = new Posicion(0,1);
		assertTrue(p1.esAdyacente(p2)); // caso adyacencia derecha
		
		p2 = new Posicion(1,0);
		assertTrue(p1.esAdyacente(p2)); // caso adyacencia abajo
		
		p2 = new Posicion(1,1);
		
		p1 = new Posicion(0,1);
		assertTrue(p2.esAdyacente(p1)); // caso adyacencia arriba
		
		p1 = new Posicion(1,0);
		assertTrue(p2.esAdyacente(p1)); // caso adyacencia izquierda
		
		p1 = new Posicion(0,0);
		assertFalse(p1.esAdyacente(p2)); // caso límite no adyacente
		
		p2 = new Posicion(0,0);
		assertFalse(p1.esAdyacente(p2)); // caso límite misma posicion
		
		try
		{
			assertEquals(10, p1.esAdyacente(null));
			assertTrue(false);
		}
		catch (AssertionError e) {}
	}

	// Se desplaza la posicion dx y dy casillas
	// Mirar desplazamiento en todos los casos 
	@Test
	void testDesplazar() {
		p1.desplazar(1, 0); // solo x
		assertEquals(1, p1.getX());
		assertEquals(0, p1.getY());
		
		p1.desplazar(0, 1); // solo y
		assertEquals(1, p1.getX());
		assertEquals(1, p1.getY());
		
		p1.desplazar(1, 1); // ambos
		assertEquals(2, p1.getX());
		assertEquals(2, p1.getY());
		
		p1.desplazar(-1, -1); // ambos negativo
		assertEquals(1, p1.getX());
		assertEquals(1, p1.getY());
		
		p1.desplazar(-1, 0); // solo x negativo
		assertEquals(0, p1.getX());
		assertEquals(1, p1.getY());
		
		p1.desplazar(0, -1); // solo y negativo
		assertEquals(0, p1.getX());
		assertEquals(0, p1.getY());
	}
	
	@Test
	void testEqualsPosicion() {
		assertTrue(p1.equals(p2));
		assertTrue(p2.equals(p1));
		
		p2 = new Posicion(1,0);
		assertFalse(p1.equals(p2));
		
		p2 = new Posicion(0,1);
		assertFalse(p1.equals(p2));
		
		assertFalse(p2.equals(p1));
		
		try
		{
			assertTrue(p1.equals(null));
			assertTrue(false);
		}
		catch (AssertionError e) {}
	}

	@Test
	void testHashCode() {
		assertEquals(p1.hashCode(), p2.hashCode());
		
		Posicion p3 = new Posicion(0,0);
		Posicion p4 = new Posicion(1,0);
		
		assertNotEquals(p3.hashCode(), p4.hashCode());
		
		p3 = new Posicion(0,0);
		p4 = new Posicion(0,1);
		
		assertNotEquals(p3.hashCode(), p4.hashCode());
		
		Set<Posicion> posiciones = new HashSet<>();
		posiciones.add(new Posicion(6,3));
		assertTrue(posiciones.contains(new Posicion(6,3)));
		
		posiciones.add(new Posicion(2,5));	
		assertFalse(posiciones.contains(new Posicion(5,2)));
	}

	@Test
	void testEqualsObject() {
		Object mismaRef = p1;
		assertTrue(p1.equals(mismaRef));
		assertFalse(p1.equals((Object) null));
		assertFalse(p1.equals("no es posicion"));
		assertTrue(p1.equals((Object) new Posicion(0, 0)));
		assertFalse(p1.equals((Object) new Posicion(1, 0)));
		assertFalse(p1.equals((Object) new Posicion(0, 1)));
	}

	@ParameterizedTest
	@CsvFileSource(resources = "/data/movimientos_test.csv", numLinesToSkip = 1)
	void testEsAdyacenteDataDriven(int oX, int oY, int dX, int dY, boolean esperado) {
		Posicion origen = new Posicion(oX, oY);
		Posicion destino = new Posicion(dX, dY);
		assertEquals(esperado, origen.esAdyacente(destino));
	}

}
