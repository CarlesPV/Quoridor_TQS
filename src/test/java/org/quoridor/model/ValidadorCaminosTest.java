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
		fail("Not yet implemented");
	}

	@Test
	void testEstaBloqueadoPorMuro() {
		Posicion oP = new Posicion(4,0);
		Posicion dP = new Posicion(4,1);
		Muro m = new Muro(new Posicion(3,0), Orientacion.HORIZONTAL);
		List<Muro> muros = new ArrayList<>(List.of(m));
		assertTrue(validador.estaBloqueadoPorMuro(oP, dP, muros));
	}

}
