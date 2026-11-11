package org.quoridor.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

class ValidadorCaminosTest {
	
	private ValidadorCaminos validador;
    private int TAMANO_TABLERO = 9;

	@BeforeEach
	void setUp() throws Exception {
		validador = new ValidadorCaminos();
	}

	@Test
	void testHayCaminoDisponible() {
		fail("Not yet implemented");
	}

	@Test
	void testCalcularDistanciaMinima() {
		fail("Not yet implemented");
	}

	@Test
	void testObtenerVecinosValidos() {
		Posicion p = new Posicion(2, 2);
		List<Muro> muros = new ArrayList<>();
		List<Posicion> vecinos = validador.obtenerVecinosValidos(p, muros, TAMANO_TABLERO);
		Posicion[] esperados = {
			    new Posicion(2, 1), // Arriba
			    new Posicion(2, 3), // Abajo
			    new Posicion(1, 2), // Izquierda
			    new Posicion(3, 2)  // Derecha
			};
		for (int i = 0; i < vecinos.size(); i++) {
	        assertEquals(esperados[i].getX(), vecinos.get(i).getX());
	        assertEquals(esperados[i].getY(), vecinos.get(i).getY());
	    }
		
	}

	@Test
	void testEstaBloqueadoPorMuro() {
		Posicion oP = new Posicion(4,0);
		Posicion dP = new Posicion(4,1);
		Muro m = new Muro(new Posicion(3,0), Orientacion.HORIZONTAL);
		List<Muro> muros = new ArrayList<>(List.of(m));
		assertTrue(validador.estaBloqueadoPorMuro(oP, dP, muros));
		
		oP = new Posicion(4,1);
		dP = new Posicion(5,1);
		assertFalse(validador.estaBloqueadoPorMuro(oP, dP, muros));
		
		m = new Muro(new Posicion(4,1), Orientacion.VERTICAL);
		muros.add(m);
		assertTrue(validador.estaBloqueadoPorMuro(oP, dP, muros));
		
		try
		{
			validador.estaBloqueadoPorMuro(null, dP, muros);
			assertTrue(false);
		}
		catch (AssertionError e) {}
		
		try
		{
			validador.estaBloqueadoPorMuro(oP, null, muros);
			assertTrue(false);
		}
		catch (AssertionError e) {}
	}

}
