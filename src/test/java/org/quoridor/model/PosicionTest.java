package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.api.DisplayName;

import java.util.*;
class PosicionTest {
	Posicion p1, p2;
	
	@BeforeEach
	void setUp() throws Exception {
		p1 = new Posicion(); // (0,0)
		p2 = new Posicion(); // (0,0)
	}

	// Prueba de Caja Negra: Partición Equivalente para constructores (por defecto y parametrizado)
	@Test
	void testConstructoresPosicion() {
		Posicion porDefecto = new Posicion();
		assertEquals(0, porDefecto.getX());
		assertEquals(0, porDefecto.getY());

		Posicion ceroExplicito = new Posicion(0, 0);
		assertEquals(0, ceroExplicito.getX());
		assertEquals(0, ceroExplicito.getY());

		Posicion positivas = new Posicion(4, 7);
		assertEquals(4, positivas.getX());
		assertEquals(7, positivas.getY());

		Posicion negativas = new Posicion(-3, -6);
		assertEquals(-3, negativas.getX());
		assertEquals(-6, negativas.getY());

		Posicion mixtas = new Posicion(-1, 8);
		assertEquals(-1, mixtas.getX());
		assertEquals(8, mixtas.getY());
	}

	// Se mira que la posicion sea valida y no este fuera del tablero de juego
	// Mirar primero casos frontera true y luego todos los falsos en entorno normal 9x9
	// Finalmente mirar si cambiar el límite afecta las posiciones que antes aceptaba
	@Test
	void testEsValida() {
		// Valores límite y esquinas válidas en tablero 9x9
		assertTrue(p1.esValida(9));
		assertTrue(new Posicion(8, 8).esValida(9));
		assertTrue(new Posicion(0, 8).esValida(9));
		assertTrue(new Posicion(8, 0).esValida(9));

		// Valores frontera internos válidos en tablero 9x9
		assertTrue(new Posicion(1, 1).esValida(9));
		assertTrue(new Posicion(7, 7).esValida(9));

		// Valores frontera inválidos en un eje y en ambos ejes (9x9)
		assertFalse(new Posicion(-1, 0).esValida(9));
		assertFalse(new Posicion(0, -1).esValida(9));
		assertFalse(new Posicion(-1, -1).esValida(9));
		assertFalse(new Posicion(9, 0).esValida(9));
		assertFalse(new Posicion(0, 9).esValida(9));
		assertFalse(new Posicion(9, 9).esValida(9));

		// Cambio de límite a 8: (8,8) deja de ser válida
		assertFalse(new Posicion(8, 8).esValida(8));

		// Tablero mínimo válido (limiteTablero = 1)
		assertTrue(new Posicion(0, 0).esValida(1));
		assertFalse(new Posicion(1, 0).esValida(1));
		assertFalse(new Posicion(0, 1).esValida(1));

		// Tablero reducido 7x7 (límite válido (6,6) y fronteras inválidas (7,6) / (6,7))
		assertTrue(new Posicion(6, 6).esValida(7));
		assertFalse(new Posicion(7, 6).esValida(7));
		assertFalse(new Posicion(6, 7).esValida(7));

		// Límites de tablero no positivos (0 y -1)
		assertFalse(new Posicion(0, 0).esValida(0));
		assertFalse(new Posicion(0, 0).esValida(-1));
	}

	// Se calcula la distancia Manhattan entre dos posiciones
	// Mirar primero dos posiciones en ambos ordenes
	// y luego se cambia p1 sin cambiar p2 para ver que sigue siendo correcto el calculo
	// además se prueba la aserción DbC con argumento nulo
	@Test
	void testDistanciaManhattan() {
		// Misma posición (distancia 0)
		assertEquals(0, p1.distanciaManhattan(p2));

		// Distancia 1 (adyacencia horizontal y vertical)
		assertEquals(1, new Posicion(3, 3).distanciaManhattan(new Posicion(4, 3)));
		assertEquals(1, new Posicion(3, 3).distanciaManhattan(new Posicion(3, 4)));

		// Distancia 2 (diagonal)
		assertEquals(2, new Posicion(3, 3).distanciaManhattan(new Posicion(4, 4)));

		// Distancia máxima en tablero 9x9: (0,0) a (8,8) = 16
		assertEquals(16, new Posicion(0, 0).distanciaManhattan(new Posicion(8, 8)));

		// Simetría y coordenadas negativas en ambos ejes
		p2 = new Posicion(5, 5);
		assertEquals(10, p1.distanciaManhattan(p2));
		assertEquals(10, p2.distanciaManhattan(p1));

		p1 = new Posicion(-5, -5);
		assertEquals(20, p1.distanciaManhattan(p2));

		// Diferencias negativas en un solo eje (X y luego Y)
		assertEquals(5, new Posicion(-2, 4).distanciaManhattan(new Posicion(3, 4)));
		assertEquals(5, new Posicion(4, -2).distanciaManhattan(new Posicion(4, 3)));

		// Precondición DbC: argumento nulo
		assertThrows(AssertionError.class, () -> p1.distanciaManhattan(null));
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
		
		assertThrows(AssertionError.class, () -> p1.esAdyacente(null));
	}

	// Se desplaza la posicion dx y dy casillas
	// Mirar desplazamiento en todos los casos (nulo, unitarios, signos mixtos y grandes)
	@Test
	void testDesplazar() {
		// Desplazamiento nulo (0,0)
		p1.desplazar(0, 0);
		assertEquals(0, p1.getX());
		assertEquals(0, p1.getY());

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

		// Signos mixtos (+dx, -dy) y (-dx, +dy)
		p1.desplazar(3, -2);
		assertEquals(3, p1.getX());
		assertEquals(-2, p1.getY());

		p1.desplazar(-4, 5);
		assertEquals(-1, p1.getX());
		assertEquals(3, p1.getY());

		// Desplazamientos grandes
		p1.desplazar(100, -50);
		assertEquals(99, p1.getX());
		assertEquals(-47, p1.getY());
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

		// Coordenadas intercambiadas (3,5) vs (5,3)
		Posicion a = new Posicion(3, 5);
		Posicion b = new Posicion(5, 3);
		assertFalse(a.equals(b));
		assertFalse(b.equals(a));

		assertThrows(AssertionError.class, () -> p1.equals((Posicion) null));
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

	
	@ParameterizedTest(name = "[{index}] ({0},{1}) adyacente a ({2},{3})? esperado={4}")
	@CsvFileSource(resources = "/data/movimientos_test.csv", numLinesToSkip = 1)
	@DisplayName("Data-Driven: Verificación de adyacencia ortogonal desde CSV")
	void testEsAdyacenteDataDriven(int oX, int oY, int dX, int dY, boolean esperado) {
		Posicion origen = new Posicion(oX, oY);
		Posicion destino = new Posicion(dX, dY);
		assertEquals(esperado, origen.esAdyacente(destino));
	}

	// Prueba de Programación por Contrato: verifica precondiciones, postcondiciones e invariante de Posicion
	@Test
	void testContratosDbCPosicion() {
		// Construcción por defecto y parametrizada mantienen estado consistente
		Posicion origen = new Posicion();
		Posicion destino = new Posicion(3, 4);

		// Postcondición de distanciaManhattan (>= 0) y simetría
		int dist = origen.distanciaManhattan(destino);
		assertTrue(dist >= 0, "La distancia Manhattan debe cumplir la postcondición >= 0");
		assertEquals(7, dist);
		assertEquals(0, origen.distanciaManhattan(origen));

		// Postcondición de desplazar: coordenadas finales == prev + delta
		int prevX = destino.getX();
		int prevY = destino.getY();
		destino.desplazar(-2, 3);
		assertEquals(prevX - 2, destino.getX());
		assertEquals(prevY + 3, destino.getY());

		// Postcondición de esAdyacente: una posición no es adyacente a sí misma
		assertFalse(destino.esAdyacente(destino));

		// Precondiciones con argumento nulo deben lanzar AssertionError
		assertThrows(AssertionError.class, () -> origen.distanciaManhattan(null));
		assertThrows(AssertionError.class, () -> origen.esAdyacente(null));
		assertThrows(AssertionError.class, () -> origen.equals((Posicion) null));
	}

}
